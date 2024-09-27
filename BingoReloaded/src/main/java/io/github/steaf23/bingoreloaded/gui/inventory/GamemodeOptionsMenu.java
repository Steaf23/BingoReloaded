package io.github.steaf23.bingoreloaded.gui.inventory;

import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.settings.BingoSettingsBuilder;
import io.github.steaf23.playerdisplay.inventory.BasicMenu;
import io.github.steaf23.playerdisplay.inventory.Menu;
import io.github.steaf23.playerdisplay.inventory.MenuBoard;
import io.github.steaf23.playerdisplay.inventory.item.ItemTemplate;
import io.github.steaf23.playerdisplay.inventory.item.action.ComboBoxButtonAction;
import io.github.steaf23.playerdisplay.inventory.item.action.MenuAction;
import io.github.steaf23.playerdisplay.inventory.item.action.SpinBoxButtonAction;
import io.github.steaf23.playerdisplay.util.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GamemodeOptionsMenu extends BasicMenu
{
    private final BingoSession session;

    public GamemodeOptionsMenu(MenuBoard menuBoard, BingoSession session)
    {
        super(menuBoard, Component.text("Select Gamemode"), 1);
        this.session = session;

        addAction(new ItemTemplate(1,
                Material.LIME_CONCRETE, BasicMenu.applyTitleFormat("Regular 5x5")), arguments -> selectGamemode(arguments.player(), BingoGamemode.REGULAR));
        addAction(new ItemTemplate(3,
                Material.MAGENTA_CONCRETE, BasicMenu.applyTitleFormat("Lockout 5x5")), arguments -> selectGamemode(arguments.player(), BingoGamemode.LOCKOUT));
        addAction(new ItemTemplate(5,
                Material.LIGHT_BLUE_CONCRETE, BasicMenu.applyTitleFormat("Complete-All 5x5")), arguments -> selectGamemode(arguments.player(), BingoGamemode.COMPLETE));
        addAction(new ItemTemplate(7,
                Material.ORANGE_CONCRETE, BasicMenu.applyTitleFormat("HotSwap 5x5")), arguments -> selectGamemode(arguments.player(), BingoGamemode.HOTSWAP));
    }

    public void selectGamemode(HumanEntity player, BingoGamemode chosenMode) {
        BasicMenu optionMenu = new BasicMenu(getMenuBoard(), Component.text("Select Gamemode Options"), 1);
        List<Consumer<BingoSettingsBuilder>> additionalOptions = new ArrayList<>();

        Component toggleInput = Menu.INPUT_LEFT_CLICK.append(Component.text("toggle option"));
        ItemTemplate cardSizeItem = new ComboBoxButtonAction.Builder("3",
                new ItemTemplate(Material.RABBIT_HIDE, BasicMenu.applyTitleFormat("Small card (3x3)")).addDescription("input", 10, toggleInput))
                .addOption("5",
                        new ItemTemplate(Material.LEATHER, BasicMenu.applyTitleFormat("Big card (5x5)")).addDescription("input", 10, toggleInput))
                .buildItem(3, session.settingsBuilder.view().size() == CardSize.X5 ? "5" : "3");
        optionMenu.addItem(cardSizeItem);

        if (chosenMode == BingoGamemode.COMPLETE) {
            int completeGoal = session.settingsBuilder.view().completeGoal();
            ItemTemplate completeGoalItem = new ItemTemplate(5, Material.RECOVERY_COMPASS, BasicMenu.applyTitleFormat("Complete-All Win Score"),
                    Component.text("Complete " + completeGoal + " tasks to win complete-all."),
                    Component.text("Only effective if countdown mode is disabled"));
            SpinBoxButtonAction goalAction = new SpinBoxButtonAction(1, 64, completeGoal, value -> {
                session.settingsBuilder.completeGoal(value);
                completeGoalItem.setLore(ComponentUtils.createComponentsFromString(
                        "Complete " + value + " tasks to win complete-all.",
                        "Only effective if countdown mode is disabled"));
            });
            optionMenu.addAction(completeGoalItem, goalAction);
            additionalOptions.add(settings -> settings.completeGoal(goalAction.getValue()));

        } else if (chosenMode == BingoGamemode.HOTSWAP) {
            int hotswapGoal = session.settingsBuilder.view().hotswapGoal();
            ItemTemplate hotswapGoalItem = new ItemTemplate(5, Material.FIRE_CHARGE, BasicMenu.applyTitleFormat("Hot-Swap Win Score"),
                    Component.text("Complete " + hotswapGoal + " tasks to win hot-swap."),
                    Component.text("Only effective if countdown mode is disabled"));
            SpinBoxButtonAction goalAction = new SpinBoxButtonAction(1, 64, hotswapGoal, value -> {
                session.settingsBuilder.hotswapGoal(value);
                hotswapGoalItem.setLore(ComponentUtils.createComponentsFromString(
                        "Complete " + value + " tasks to win hot-swap.",
                        "Only effective if countdown mode is disabled"));
            });
            optionMenu.addAction(hotswapGoalItem, goalAction);
            additionalOptions.add(settings -> settings.hotswapGoal(goalAction.getValue()));
        }

        optionMenu.addCloseAction(new ItemTemplate(0, Material.REDSTONE, BingoMessage.MENU_EXIT.asPhrase().color(NamedTextColor.RED).decorate(TextDecoration.BOLD)));
        optionMenu.addAction(getSaveButton(chosenMode, 8), args -> {
            CardSize size = CardSize.X5;
            MenuAction action = cardSizeItem.getAction();
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

    private static ItemTemplate getSaveButton(BingoGamemode mode, int slot) {
        return new ItemTemplate(slot, Material.EMERALD, Component.text("Play ").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD).append(mode.asComponent()).append(Component.text(" with selected options")));
    }
}
