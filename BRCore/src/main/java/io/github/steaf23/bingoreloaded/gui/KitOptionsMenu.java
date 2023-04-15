package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.BingoReloadedCore;
import io.github.steaf23.bingoreloaded.BingoSession;
import io.github.steaf23.bingoreloaded.player.CustomKit;
import io.github.steaf23.bingoreloaded.player.PlayerKit;
import io.github.steaf23.bingoreloaded.gui.base.InventoryItem;
import io.github.steaf23.bingoreloaded.gui.base.MenuInventory;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class KitOptionsMenu extends MenuInventory
{
    private final BingoSession session;

    private static final InventoryItem HARDCORE = new InventoryItem(1, 1,
            Material.RED_CONCRETE, PlayerKit.HARDCORE.displayName,
            BingoReloadedCore.get().getTranslator().itemDescription("menu.kits.hardcore"));
    private static final InventoryItem NORMAL = new InventoryItem(3, 1,
            Material.YELLOW_CONCRETE, PlayerKit.NORMAL.displayName,
            BingoReloadedCore.get().getTranslator().itemDescription("menu.kits.normal"));
    private static final InventoryItem OVERPOWERED = new InventoryItem(5, 1,
            Material.PURPLE_CONCRETE, PlayerKit.OVERPOWERED.displayName,
            BingoReloadedCore.get().getTranslator().itemDescription("menu.kits.overpowered"));
    private static final InventoryItem RELOADED = new InventoryItem(7, 1,
            Material.CYAN_CONCRETE, PlayerKit.RELOADED.displayName,
            BingoReloadedCore.get().getTranslator().itemDescription("menu.kits.reloaded"));

    public KitOptionsMenu(MenuInventory parent, BingoSession session)
    {
        super(45, BingoReloadedCore.get().getTranslator().itemName("menu.options.kit"), parent);
        this.session = session;

        fillOptions(HARDCORE, NORMAL, OVERPOWERED, RELOADED);
        for (int i = 0; i < 5; i++)
        {
            addOption(new InventoryItem(i * 2, 3, Material.GRAY_CONCRETE,
                    "" + ChatColor.GRAY + "Custom Kit Slot " + (i + 1),
                    "Create a custom kit from your inventory using ",
                    "" + ChatColor.RED + ChatColor.ITALIC + "/bingo kit add " + (i + 1) + " <name>!"));
        }

        CustomKit kit = PlayerKit.getCustomKit(PlayerKit.CUSTOM_1);
        if (kit != null)
            addOption(new InventoryItem(0, 3, Material.WHITE_CONCRETE,
                    ChatColor.RESET + kit.getName(), "Custom kit"));
        kit = PlayerKit.getCustomKit(PlayerKit.CUSTOM_2);
        if (kit != null)
            addOption(new InventoryItem(2, 3, Material.WHITE_CONCRETE,
                    ChatColor.RESET + kit.getName(), "Custom kit"));
        kit = PlayerKit.getCustomKit(PlayerKit.CUSTOM_3);
        if (kit != null)
            addOption(new InventoryItem(4, 3, Material.WHITE_CONCRETE,
                    ChatColor.RESET + kit.getName(), "Custom kit"));
        kit = PlayerKit.getCustomKit(PlayerKit.CUSTOM_4);
        if (kit != null)
            addOption(new InventoryItem(6, 3, Material.WHITE_CONCRETE,
                    ChatColor.RESET + kit.getName(), "Custom kit"));
        kit = PlayerKit.getCustomKit(PlayerKit.CUSTOM_5);
        if (kit != null)
            addOption(new InventoryItem(8, 3, Material.WHITE_CONCRETE,
                    ChatColor.RESET + kit.getName(), "Custom kit"));
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        if (slotClicked == HARDCORE.getSlot())
        {
            setKit(PlayerKit.HARDCORE);
        }
        else if (slotClicked == NORMAL.getSlot())
        {
            setKit(PlayerKit.NORMAL);
        }
        else if (slotClicked == OVERPOWERED.getSlot())
        {
            setKit(PlayerKit.OVERPOWERED);
        }
        else if (slotClicked == RELOADED.getSlot())
        {
            setKit(PlayerKit.RELOADED);
        }
        else if (slotClicked == 27)
        {
            if (event.getCurrentItem().getType() != Material.GRAY_CONCRETE)
                setKit(PlayerKit.CUSTOM_1);
            else
                return;
        }
        else if (slotClicked == 27 + 2)
        {
            if (event.getCurrentItem().getType() != Material.GRAY_CONCRETE)
                setKit(PlayerKit.CUSTOM_2);
            else
                return;
        }
        else if (slotClicked == 27 + 4)
        {
            if (event.getCurrentItem().getType() != Material.GRAY_CONCRETE)
                setKit(PlayerKit.CUSTOM_3);
            else
                return;
        }
        else if (slotClicked == 27 + 6)
        {
            if (event.getCurrentItem().getType() != Material.GRAY_CONCRETE)
                setKit(PlayerKit.CUSTOM_4);
            else
                return;
        }
        else if (slotClicked == 27 + 8)
        {
            if (event.getCurrentItem().getType() != Material.GRAY_CONCRETE)
                setKit(PlayerKit.CUSTOM_5);
            else
                return;
        }
        else
        {
            return;
        }
        close(player);
    }

    private void setKit(PlayerKit kit)
    {
        session.settingsBuilder.kit(kit);
    }
}
