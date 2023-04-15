package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.BingoReloadedCore;
import io.github.steaf23.bingoreloaded.BingoSettingsBuilder;
import io.github.steaf23.bingoreloaded.gui.base.InventoryItem;
import io.github.steaf23.bingoreloaded.gui.base.MenuInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.EnumSet;
import java.util.List;

public class EffectOptionsMenu extends MenuInventory
{
    private final BingoSettingsBuilder settings;
    private final EnumSet<EffectOptionFlags> flags;
    private final InventoryItem[] options;

    public EffectOptionsMenu(MenuInventory parent, BingoSettingsBuilder settings)
    {
        super(45, BingoReloadedCore.translate("menu.options.effects.name"), parent);
        this.settings = settings;

        options = new InventoryItem[]{
                new InventoryItem(4, 3,
                        Material.CARROT, ""),
                new InventoryItem(2, 3,
                        Material.PUFFERFISH, ""),
                new InventoryItem(6, 3,
                        Material.MAGMA_CREAM, ""),
                new InventoryItem(3, 1,
                        Material.NETHERITE_BOOTS, ""),
                new InventoryItem(5, 1,
                        Material.FEATHER, ""),
                new InventoryItem(44, Material.DIAMOND,
                        "" + ChatColor.AQUA + ChatColor.BOLD + BingoReloadedCore.translate("menu.save_exit"))
        };
        fillOptions(options);

        flags = settings.view().effects();
        updateUI();
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        for (int i = 0; i < options.length - 1; i++)
        {
            if (slotClicked == options[i].getSlot())
            {
                toggleOption(i);
            }
        }

        if (slotClicked == options[5].getSlot())
        {
            settings.effects(flags);
            close(player);
        }
    }

    private void toggleOption(int index)
    {
        EffectOptionFlags option = EffectOptionFlags.values()[index];
        if (flags.contains(option))
            flags.remove(option);
        else
            flags.add(option);
        updateUI();
    }

    public void updateUI()
    {
        for (int i = 0; i < options.length - 1; i++)
        {
            ItemMeta meta = options[i].getItemMeta();
            if (meta != null)
            {
                if (flags.contains(EffectOptionFlags.values()[i]))
                {
                    meta.setDisplayName("" + ChatColor.GREEN + ChatColor.BOLD + EffectOptionFlags.values()[i].name + " " + BingoReloadedCore.translate("menu.effects.enabled"));
                    meta.setLore(List.of(ChatColor.GREEN + BingoReloadedCore.translate("menu.effects.disable")));
                }
                else
                {
                    meta.setDisplayName("" + ChatColor.RED + ChatColor.BOLD + EffectOptionFlags.values()[i].name + " " + BingoReloadedCore.translate("menu.effects.disabled"));
                    meta.setLore(List.of(ChatColor.RED + BingoReloadedCore.translate("menu.effects.enable")));
                }
            }
            options[i].setItemMeta(meta);
            addOption(options[i]);
        }
    }
}
