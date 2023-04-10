package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.core.BingoSettings;
import io.github.steaf23.bingoreloaded.core.BingoSettingsBuilder;
import io.github.steaf23.bingoreloaded.gui.base.InventoryItem;
import io.github.steaf23.bingoreloaded.gui.base.MenuInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class ExtraBingoMenu extends MenuInventory
{

    private static final int TEAM_MAX = 64;
    private static final int DURATION_MAX = 60;
    private final BingoSettingsBuilder settings;
    private final InventoryItem exit = new InventoryItem(36,
            Material.BARRIER, TITLE_PREFIX + BingoReloaded.translate("menu.prev"));
    private final InventoryItem countdown = new InventoryItem(3, 2,
            Material.CLOCK, TITLE_PREFIX + "Enable Countdown Timer");
    private final InventoryItem gameDuration = new InventoryItem(5, 2,
            Material.RECOVERY_COMPASS, TITLE_PREFIX + "Countdown Duration");

    public ExtraBingoMenu(MenuInventory parent, BingoSettingsBuilder settings)
    {
        super(45, BingoReloaded.translate("menu.options.title"), parent);
        this.settings = settings;
        var meta = countdown.getItemMeta();
        countdown.highlight(settings.view().enableCountdown());
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        BingoSettings view = settings.view();
        if (slotClicked == exit.getSlot())
        {
            close(player);
        }
        else if (slotClicked == countdown.getSlot())
        {
            settings.enableCountdown(!view.enableCountdown());
            countdown.highlight(!view.enableCountdown());
            addOption(countdown);
        }
        else if (slotClicked == gameDuration.getSlot())
        {
            if (clickType == ClickType.LEFT)
            {
                gameDuration.setAmount(Math.min(DURATION_MAX, gameDuration.getAmount() + 1));
            }
            else if (clickType == ClickType.RIGHT)
            {
                gameDuration.setAmount(Math.max(1, gameDuration.getAmount() - 1));
            }

            gameDuration.setDescription(
                    "§7Timer set to " + gameDuration.getAmount() + " minute(s) for Countdown Bingo",
                    "§rUse the mouse buttons to increase/ decrease",
                    "the amount of minutes that Countdown bingo will last.");
            addOption(gameDuration);
        }
    }

    @Override
    public void handleClose(final InventoryCloseEvent event)
    {
        settings.countdownGameDuration(gameDuration.getAmount());
        super.handleClose(event);
    }

    @Override
    public void handleOpen(InventoryOpenEvent event)
    {
        super.handleOpen(event);
        gameDuration.setAmount(Math.max(0, Math.min(DURATION_MAX, settings.view().countdownDuration())));
        gameDuration.setDescription(
                "§7Timer set to " + gameDuration.getAmount() + " minute(s) for Countdown Bingo",
                "§r§oUse the mouse buttons to increase/ decrease",
                "the amount of minutes that Countdown bingo will last.");

        fillOptions(exit, gameDuration, countdown);
    }
}
