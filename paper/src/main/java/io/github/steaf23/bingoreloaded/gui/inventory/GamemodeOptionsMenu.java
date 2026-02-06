package io.github.steaf23.bingoreloaded.gui.inventory;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.InventoryMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.action.ComboBoxButtonAction;
import io.github.steaf23.bingoreloaded.lib.inventory.action.MenuAction;
import io.github.steaf23.bingoreloaded.lib.inventory.action.SpinBoxButtonAction;
import io.github.steaf23.bingoreloaded.lib.inventory.action.ToggleButtonAction;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.lib.util.ComponentUtils;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.settings.BingoSettingsBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GamemodeOptionsMenu extends BasicMenu
{
    private final BingoSession session;

    public GamemodeOptionsMenu(MenuBoard menuBoard, BingoSession session) {
        super(menuBoard, Component.text("Select Gamemode"), 1);
        this.session = session;

        addAction(new ItemTemplate(1,
                ItemTypePaper.of(Material.LIME_CONCRETE), BingoReloaded.applyTitleFormat(BingoMessage.MODE_REGULAR.asPhrase())), arguments -> selectGamemode(arguments.player(), BingoGamemode.REGULAR));
        addAction(new ItemTemplate(3,
                ItemTypePaper.of(Material.MAGENTA_CONCRETE), BingoReloaded.applyTitleFormat(BingoMessage.MODE_LOCKOUT.asPhrase())), arguments -> selectGamemode(arguments.player(), BingoGamemode.LOCKOUT));
        addAction(new ItemTemplate(5,
                ItemTypePaper.of(Material.LIGHT_BLUE_CONCRETE), BingoReloaded.applyTitleFormat(BingoMessage.MODE_COMPLETE.asPhrase())), arguments -> selectGamemode(arguments.player(), BingoGamemode.COMPLETE));
        addAction(new ItemTemplate(7,
                ItemTypePaper.of(Material.ORANGE_CONCRETE), BingoReloaded.applyTitleFormat(BingoMessage.MODE_HOTSWAP.asPhrase())), arguments -> selectGamemode(arguments.player(), BingoGamemode.HOTSWAP));
    }

    public void selectGamemode(PlayerHandle player, BingoGamemode chosenMode) {
        BasicMenu optionMenu = new BasicMenu(getMenuBoard(), Component.text("Select Gamemode Options"), 1);
        List<Consumer<BingoSettingsBuilder>> additionalOptions = new ArrayList<>();

        Component toggleInput = InventoryMenu.INPUT_LEFT_CLICK.append(Component.text("toggle option"));
        MenuAction cardSizeAction = new ComboBoxButtonAction.Builder("3",
                new ItemTemplate(ItemTypePaper.of(Material.RABBIT_HIDE), BingoReloaded.applyTitleFormat("Small card (3x3)")).addDescription("input", 10, toggleInput))
                .addOption("5",
                        new ItemTemplate(ItemTypePaper.of(Material.LEATHER), BingoReloaded.applyTitleFormat("Big card (5x5)")).addDescription("input", 10, toggleInput))
                .buildAction(3, session.settingsBuilder.view().size() == CardSize.X5 ? "5" : "3");
        optionMenu.addAction(cardSizeAction);

        if (chosenMode == BingoGamemode.COMPLETE) {
            int completeGoal = session.settingsBuilder.view().completeGoal();
            ItemTemplate completeGoalItem = new ItemTemplate(5, ItemTypePaper.of(Material.RECOVERY_COMPASS), BingoReloaded.applyTitleFormat("Complete-X Win Score"),
                    Component.text("Complete " + completeGoal + " tasks to win complete-x."),
                    Component.text("Only effective if countdown mode is disabled"));
            // You can at most complete 25 tasks on complete-x (full-card)
            SpinBoxButtonAction goalAction = new SpinBoxButtonAction(1, 25, completeGoal, value -> {
                session.settingsBuilder.completeGoal(value);
                completeGoalItem.setLore(ComponentUtils.createComponentsFromString(
                        "Complete " + value + " tasks to win complete-x.",
                        "Only effective if countdown mode is disabled"));
            });
            optionMenu.addItem(completeGoalItem, goalAction);

        } else if (chosenMode == BingoGamemode.HOTSWAP) {
            int hotswapGoal = session.settingsBuilder.view().hotswapGoal();
            ItemTemplate hotswapGoalItem = new ItemTemplate(5, ItemTypePaper.of(Material.FIRE_CHARGE), BingoReloaded.applyTitleFormat("Hot-Swap Win Score"),
                    Component.text("Complete " + hotswapGoal + " tasks to win hot-swap."),
                    Component.text("Only effective if countdown mode is disabled"));
            SpinBoxButtonAction goalAction = new SpinBoxButtonAction(1, 64, hotswapGoal, value -> {
                session.settingsBuilder.hotswapGoal(value);
                hotswapGoalItem.setLore(ComponentUtils.createComponentsFromString(
                        "Complete " + value + " tasks to win hot-swap.",
                        "Only effective if countdown mode is disabled"));
            });
            optionMenu.addItem(hotswapGoalItem, goalAction);
            additionalOptions.add(settings -> settings.hotswapGoal(goalAction.getValue()));

            boolean expireTasks = session.settingsBuilder.view().expireHotswapTasks();
            ItemTemplate hotswapExpireItem = new ItemTemplate(6, ItemTypePaper.of(Material.ROTTEN_FLESH), BingoReloaded.applyTitleFormat("Expire tasks automatically"));
            updateExpireTasksEnabledVisual(hotswapExpireItem, expireTasks);
            ToggleButtonAction toggleExpireTasksAction = new ToggleButtonAction(expireTasks, newValue -> {
                session.settingsBuilder.expireHotswapTasks(newValue);
                updateExpireTasksEnabledVisual(hotswapExpireItem, newValue);
            });
            optionMenu.addItem(hotswapExpireItem, toggleExpireTasksAction);
            additionalOptions.add(settings -> settings.expireHotswapTasks(toggleExpireTasksAction.getValue()));
        }

        // Generate separate card per team option
        if (chosenMode == BingoGamemode.REGULAR || chosenMode == BingoGamemode.COMPLETE) {
            int slot = chosenMode == BingoGamemode.REGULAR ? 5 : 6;
            boolean separateGeneration = session.settingsBuilder.view().differentCardPerTeam();
            ItemTemplate separateGenerationItem = new ItemTemplate(slot, ItemTypePaper.of(Material.GLOBE_BANNER_PATTERN));
            updateSeparateGenerationVisual(separateGenerationItem, separateGeneration);
            ToggleButtonAction separateGenerationAction = new ToggleButtonAction(separateGeneration, newValue -> {
                session.settingsBuilder.differentCardPerTeam(newValue);
                updateSeparateGenerationVisual(separateGenerationItem, newValue);
            });
            optionMenu.addItem(separateGenerationItem, separateGenerationAction);
            additionalOptions.add(settings -> settings.differentCardPerTeam(separateGenerationAction.getValue()));
        }

        optionMenu.addCloseAction(new ItemTemplate(0, ItemTypePaper.of(Material.REDSTONE), BingoMessage.MENU_EXIT.asPhrase().color(NamedTextColor.RED).decorate(TextDecoration.BOLD)));
        optionMenu.addAction(getSaveButton(chosenMode, 8), args -> {
            CardSize size = CardSize.X5;
            MenuAction action = cardSizeAction;
            if (action instanceof ComboBoxButtonAction comboAction) {
                size = comboAction.getSelectedOptionName().equals("5") ? CardSize.X5 : CardSize.X3;
            }

            session.settingsBuilder.mode(chosenMode);
            session.settingsBuilder.cardSize(size);

            for (Consumer<BingoSettingsBuilder> option : additionalOptions) {
                option.accept(session.settingsBuilder);
            }
            optionMenu.close(player);
            GamemodeOptionsMenu.this.close(player);
        });
        optionMenu.open(player, true);
    }

    private static void updateExpireTasksEnabledVisual(ItemTemplate item, boolean enabled) {
        if (enabled) {
			item.setName(BingoReloaded.applyTitleFormat("Expire tasks automatically"));
            item.setLore(
                    Component.text("Tasks always expire when they get completed, however..."),
                    ComponentUtils.MINI_BUILDER.deserialize("Tasks <red>EXPIRE</red> automatically after some random amount of time"));
        } else {
			item.setName(BingoReloaded.applyTitleFormat("Do NOT expire tasks automatically"));
            item.setLore(
                    Component.text("Tasks always expire when they get completed, however..."),
                    ComponentUtils.MINI_BUILDER.deserialize("Tasks <gray>DO NOT EXPIRE</gray> automatically after some random amount of time"));
        }
    }

    private static void updateSeparateGenerationVisual(ItemTemplate item, boolean enabled) {
        if (enabled) {
			item.setName(BingoReloaded.applyTitleFormat("Different tasks generated per team"));
            item.setLore(ComponentUtils.MINI_BUILDER.deserialize(("Different teams get <red>DIFFERENT</red> cards")));
        } else {
			item.setName(BingoReloaded.applyTitleFormat("Same tasks generated for everyone"));
            item.setLore(ComponentUtils.MINI_BUILDER.deserialize(("Different teams get <gray>THE SAME</gray> cards")));
        }
    }

    private static ItemTemplate getSaveButton(BingoGamemode mode, int slot) {
        return new ItemTemplate(slot, ItemTypePaper.of(Material.EMERALD), Component.text("Play ").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD).append(mode.asComponent()).append(Component.text(" with selected options")));
    }
}
