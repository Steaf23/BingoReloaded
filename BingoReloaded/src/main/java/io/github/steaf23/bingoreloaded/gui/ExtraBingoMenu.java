package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.gui.base.BasicMenu;
import io.github.steaf23.bingoreloaded.gui.base.MenuBoard;
import io.github.steaf23.bingoreloaded.gui.base.item.MenuItem;
import io.github.steaf23.bingoreloaded.gui.item.SpinBoxButtonAction;
import io.github.steaf23.bingoreloaded.gui.item.ToggleButtonAction;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.settings.BingoSettingsBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ExtraBingoMenu extends BasicMenu
{
    private static final int DURATION_MAX = 60;
    private static final int TEAMSIZE_MAX = 64;
    private final BingoSettingsBuilder settings;
    private static final MenuItem EXIT = new MenuItem(0, 5,
            Material.BARRIER, TITLE_PREFIX + BingoTranslation.MENU_PREV.translate());
    private static final MenuItem TEAM_SIZE = new MenuItem(2, 2,
            Material.ENDER_EYE, TITLE_PREFIX + "Maximum Team Size",
            ChatColor.GRAY + "(When changing this setting all currently",
            ChatColor.GRAY + "joined players will be kicked from their teams!)");
    private static final MenuItem COUNTDOWN = new MenuItem(4, 1,
            Material.CLOCK, TITLE_PREFIX + "Enable Countdown Timer");
    private static final MenuItem DURATION = new MenuItem(4, 3,
            Material.RECOVERY_COMPASS, TITLE_PREFIX + "Countdown Duration");

    public ExtraBingoMenu(MenuBoard menuBoard, BingoSettingsBuilder settings)
    {
        super(menuBoard, BingoTranslation.OPTIONS_TITLE.translate(), 6);
        this.settings = settings;

        for (int i = 1; i < 9; i++) {
            addItem(BLANK.copyToSlot(i, 5));
        }

        BingoSettings initialSettings = settings.view();

        MenuItem teamSizeItem = TEAM_SIZE.copy();
        teamSizeItem.setAction(new SpinBoxButtonAction(1, TEAMSIZE_MAX, settings.view().maxTeamSize(), (value, item) -> {
            settings.maxTeamSize(value);
        }));

        MenuItem durationItem = DURATION.copy();
        durationItem.setAction(new SpinBoxButtonAction(1, DURATION_MAX, settings.view().countdownDuration(), (value, item) -> {
            settings.countdownGameDuration(value);
        }));

        MenuItem countDownItem = COUNTDOWN.copy();
        countDownItem.setAction(new ToggleButtonAction(enable -> {
            settings.enableCountdown(enable);
            return countDownItem;
        }));

        addItems(teamSizeItem, durationItem, countDownItem);
        addCloseAction(EXIT);
    }

    //TODO: update item description in menu action!
    @Override
    public void beforeOpening(HumanEntity player) {
        super.beforeOpening(player);
//        teamSize.setAmount(Math.max(0, Math.min(TEAMSIZE_MAX, settings.view().maxTeamSize())));
//        teamSize.addDescription(
//                ChatColor.GRAY + "(When changing this setting all currently",
//                ChatColor.GRAY + "joined players will be kicked from their teams!)",
//                ChatColor.DARK_PURPLE + "Maximum team size set to " + teamSize.getAmount() + " players.");
//
//        gameDuration.setAmount(Math.max(0, Math.min(DURATION_MAX, settings.view().countdownDuration())));
//        gameDuration.addDescription(
//                ChatColor.GRAY + "Timer set to " + gameDuration.getAmount() + " minute(s) for Bingo games",
//                "" + ChatColor.RESET + ChatColor.DARK_PURPLE + "Use the mouse buttons to increase/ decrease",
//                "the amount of minutes that Bingo will last.");

//        addItems(teamSize, gameDuration, countdown);
    }
}
