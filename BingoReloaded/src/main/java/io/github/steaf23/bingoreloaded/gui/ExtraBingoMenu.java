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
        teamSizeItem.setAction(new SpinBoxButtonAction(1, TEAMSIZE_MAX, settings.view().maxTeamSize(), settings::maxTeamSize));

        MenuItem durationItem = DURATION.copy();
        durationItem.setAction(new SpinBoxButtonAction(1, DURATION_MAX, settings.view().countdownDuration(), settings::countdownGameDuration));

        MenuItem countDownItem = COUNTDOWN.copy();
        countDownItem.setAction(new ToggleButtonAction(settings::enableCountdown));

        addItems(teamSizeItem, durationItem, countDownItem);
        addCloseAction(EXIT);
    }

    @Override
    public boolean onClick(InventoryClickEvent event, HumanEntity player, int clickedSlot, ClickType clickType) {
        BingoSettings view = settings.view();

        int slotClicked = event.getRawSlot();

//        if (slotClicked == teamSize.getSlot())
//        {
//            if (clickType == ClickType.LEFT)
//            {
//                teamSize.setAmount(Math.min(TEAMSIZE_MAX, teamSize.getAmount() + 1));
//            }
//            else if (clickType == ClickType.RIGHT)
//            {
//                teamSize.setAmount(Math.max(1, teamSize.getAmount() - 1));
//            }
//
//            teamSize.setDescription(
//                    ChatColor.GRAY + "(When changing this setting all currently",
//                    ChatColor.GRAY + "joined players will be kicked from their teams!)",
//                    "§7Maximum team size set to " + teamSize.getAmount() + " players.",
//                    "§rUse the mouse buttons to increase/ decrease",
//                    "the amount of players a team can have.");
//            addItem(teamSize);
//            settings.maxTeamSize(teamSize.getAmount());
//        }
//        else if (slotClicked == countdown.getSlot())
//        {
//            settings.enableCountdown(!view.enableCountdown());
//            countdown.setGlowing(!view.enableCountdown());
//            addItem(countdown);
//        }
//        else if (slotClicked == gameDuration.getSlot())
//        {
//            if (clickType == ClickType.LEFT)
//            {
//                gameDuration.setAmount(Math.min(DURATION_MAX, gameDuration.getAmount() + 1));
//            }
//            else if (clickType == ClickType.RIGHT)
//            {
//                gameDuration.setAmount(Math.max(1, gameDuration.getAmount() - 1));
//            }
//
//            gameDuration.setDescription(
//                    "§7Timer set to " + gameDuration.getAmount() + " minute(s) for Countdown Bingo.",
//                    "§rUse the mouse buttons to increase/ decrease",
//                    "the amount of minutes that Countdown bingo will last.");
//            addItem(gameDuration);
//            settings.countdownGameDuration(gameDuration.getAmount());
//        }
        return super.onClick(event, player, clickedSlot, clickType);
    }

    @Override
    public void beforeOpening(HumanEntity player) {
        super.beforeOpening(player);
//        teamSize.setAmount(Math.max(0, Math.min(TEAMSIZE_MAX, settings.view().maxTeamSize())));
//        teamSize.setDescription(
//                ChatColor.GRAY + "(When changing this setting all currently",
//                ChatColor.GRAY + "joined players will be kicked from their teams!)",
//                ChatColor.DARK_PURPLE + "Maximum team size set to " + teamSize.getAmount() + " players.");
//
//        gameDuration.setAmount(Math.max(0, Math.min(DURATION_MAX, settings.view().countdownDuration())));
//        gameDuration.setDescription(
//                ChatColor.GRAY + "Timer set to " + gameDuration.getAmount() + " minute(s) for Bingo games",
//                "" + ChatColor.RESET + ChatColor.DARK_PURPLE + "Use the mouse buttons to increase/ decrease",
//                "the amount of minutes that Bingo will last.");

//        addItems(teamSize, gameDuration, countdown);
    }
}
