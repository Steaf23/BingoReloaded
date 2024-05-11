package io.github.steaf23.bingoreloaded.gui;


import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.easymenulib.menu.*;
import io.github.steaf23.easymenulib.menu.item.MenuItem;
import io.github.steaf23.easymenulib.menu.item.action.ComboBoxButtonAction;
import io.github.steaf23.easymenulib.menu.item.action.SpinBoxButtonAction;
import io.github.steaf23.easymenulib.menu.item.action.ToggleButtonAction;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

public class AdminBingoMenu extends BasicMenu
{
    private final BingoSession session;

    private static final int DURATION_MAX = 60;
    private static final int TEAMSIZE_MAX = 64;

    private static final MenuItem START = new MenuItem(6, 0,
            Material.LIME_CONCRETE, TITLE_PREFIX + BingoTranslation.OPTIONS_START.translate());
    private static final MenuItem END = new MenuItem(6, 0,
            Material.RED_CONCRETE, TITLE_PREFIX + BingoTranslation.OPTIONS_END.translate());
    private static final MenuItem JOIN = new MenuItem(2, 0,
            Material.WHITE_GLAZED_TERRACOTTA, TITLE_PREFIX + BingoTranslation.OPTIONS_TEAM.translate());
    private static final MenuItem CARD = new MenuItem(1, 2,
            Material.MAP, TITLE_PREFIX + BingoTranslation.OPTIONS_CARD.translate());
    private static final MenuItem KIT = new MenuItem(3, 2,
            Material.LEATHER_HELMET, TITLE_PREFIX + BingoTranslation.OPTIONS_KIT.translate());
    private static final MenuItem MODE = new MenuItem(1, 4,
            Material.ENCHANTED_BOOK, TITLE_PREFIX + BingoTranslation.OPTIONS_GAMEMODE.translate())
            .addDescription("input", 10, Menu.inputButtonText("Right Click") + "gamemode specific settings");
    private static final MenuItem EFFECTS = new MenuItem(3, 4,
            Material.POTION, TITLE_PREFIX + BingoTranslation.OPTIONS_EFFECTS.translate());
    private static final MenuItem COUNTDOWN = new MenuItem(5, 2,
            Material.CLOCK, TITLE_PREFIX + "Enable Countdown Timer");
    private static final MenuItem DURATION = new MenuItem(5, 4,
            Material.RECOVERY_COMPASS, TITLE_PREFIX + "Countdown Duration");
    private static final MenuItem TEAM_SIZE = new MenuItem(7, 2,
            Material.ENDER_EYE, TITLE_PREFIX + "Maximum Team Size");
    private static final MenuItem PRESETS = new MenuItem(7, 4,
            Material.CHEST_MINECART, TITLE_PREFIX + "Setting Presets",
            org.bukkit.ChatColor.GRAY + "Click to apply settings from saved presets");

    public AdminBingoMenu(MenuBoard menuBoard, BingoSession session, ConfigData config) {
        super(menuBoard, BingoTranslation.OPTIONS_TITLE.translate(), 6);
        this.session = session;

        addAction(JOIN, p -> {
            TeamSelectionMenu selectionMenu = new TeamSelectionMenu(menuBoard, session.teamManager);
            selectionMenu.open(p);
        });
        addAction(KIT, arguments -> new KitOptionsMenu(getMenuBoard(), session).open(arguments.player()));
        addAction(MODE, arguments -> {
            if (arguments.clickType().isLeftClick()) {
                new GamemodeOptionsMenu(getMenuBoard(), session).open(arguments.player());
            }
            else if (arguments.clickType().isRightClick()) {
                showGamemodeSettings(arguments.player());
            }
        });
        addAction(CARD, this::openCardPicker);
        addAction(EFFECTS, arguments -> new EffectOptionsMenu(getMenuBoard(), session.settingsBuilder).open(arguments.player()));
        addAction(PRESETS, arguments -> new SettingsPresetMenu(getMenuBoard(), session.settingsBuilder).open(arguments.player()));

        MenuItem teamSizeItem = TEAM_SIZE.copy();
        int maxTeamSize = session.settingsBuilder.view().maxTeamSize();
        updateTeamSizeLore(teamSizeItem, maxTeamSize);
        teamSizeItem.setAction(new SpinBoxButtonAction(1, TEAMSIZE_MAX, maxTeamSize, value -> {
            session.settingsBuilder.maxTeamSize(value);
            updateTeamSizeLore(teamSizeItem, value);
        }));

        MenuItem durationItem = DURATION.copy();
        int duration = session.settingsBuilder.view().maxTeamSize();
        updateDurationLore(durationItem, duration);
        durationItem.setAction(new SpinBoxButtonAction(1, DURATION_MAX, duration, value -> {
            session.settingsBuilder.countdownGameDuration(value);
            updateDurationLore(durationItem, value);
        }));

        MenuItem countdownItem = COUNTDOWN.copy();
        boolean enableCountdown = session.settingsBuilder.view().enableCountdown();
        updateCountdownEnabledLore(countdownItem, enableCountdown);
        countdownItem.setAction(new ToggleButtonAction(enableCountdown, enable -> {
            session.settingsBuilder.enableCountdown(enable);
            updateCountdownEnabledLore(countdownItem, enable);
        }));
        addItems(teamSizeItem, durationItem, countdownItem);

        MenuItem centerButton = START.copy();
        centerButton.setAction(new ComboBoxButtonAction(value -> {
            if (value.equals("end")) {
                session.startGame();
            }
            else if (value.equals("start")) {
                session.endGame();
            }
        })
                .addOption("start", START.buildStack())
                .addOption("end", END.buildStack())
                .selectOption(session.isRunning() ? "end" : "start"));
        addItem(centerButton);
    }

    private void openCardPicker(ActionArguments arguments) {
        HumanEntity player = arguments.player();
        BingoCardData cardsData = new BingoCardData();
        List<MenuItem> cards = new ArrayList<>();

        for (String cardName : cardsData.getCardNames()) {
            cards.add(new MenuItem(Material.PAPER, cardName,
                    ChatColor.DARK_PURPLE + BingoTranslation.LIST_COUNT.translate(
                            "" + cardsData.getListNames(cardName).size())));
        }

        BasicMenu cardPicker = new PaginatedSelectionMenu(getMenuBoard(), "Choose A Card", cards, FilterType.DISPLAY_NAME)
        {
            @Override
            public void onOptionClickedDelegate(InventoryClickEvent event, MenuItem clickedOption, HumanEntity player) {
                if (!clickedOption.getName().isEmpty()) {
                    cardSelected(clickedOption.getName());
                }
                close(player);
            }
        };
        cardPicker.open(player);
    }

    private void cardSelected(String cardName) {
        if (cardName == null) return;
        session.settingsBuilder.card(cardName);
    }

    private void updateDurationLore(MenuItem item, int duration) {
        item.setLore(
                ChatColor.DARK_PURPLE + "Timer set to " + duration + " minute(s)",
                "for bingo games on countdown mode");
    }

    private void updateCountdownEnabledLore(MenuItem item, boolean enabled) {
        if (enabled) {
            item.setLore(ChatColor.DARK_PURPLE + "Countdown mode is " + ChatColor.GREEN + "ENABLED");
        }
        else {
            item.setLore(ChatColor.DARK_PURPLE + "Countdown mode is " + ChatColor.RED + "DISABLED");
        }
    }

    private void updateTeamSizeLore(MenuItem item, int value) {
        item.setLore(
                ChatColor.GRAY + "(When changing this setting all currently",
                ChatColor.GRAY + "joined players will be kicked from their teams!)",
                ChatColor.DARK_PURPLE + "Maximum team size set to " + value + " players.");
    }

    private void showGamemodeSettings(HumanEntity player) {
        BasicMenu menu = new BasicMenu(getMenuBoard(), "", 1);

        int hotswapGoal = session.settingsBuilder.view().hotswapGoal();
        MenuItem hotswapGoalItem = new MenuItem(0, Material.FIRE_CHARGE, "Hot-Swap Win Score",
                "Complete " + hotswapGoal + " tasks to win hot-swap.",
                "Only effective if countdown mode is disabled");
        menu.addAction(hotswapGoalItem,
                new SpinBoxButtonAction(1, 64, hotswapGoal, value -> {
                    session.settingsBuilder.hotswapGoal(value);
                    hotswapGoalItem.setLore(
                            "Complete " + hotswapGoal + " tasks to win hot-swap.",
                            "Only effective if countdown mode is disabled");
                }));

        menu.addCloseAction(new MenuItem(8, Material.BARRIER, TITLE_PREFIX + ChatColor.LIGHT_PURPLE + BingoTranslation.MENU_EXIT.translate()));
        menu.open(player);
    }
}
