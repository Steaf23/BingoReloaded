package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.core.BingoSettings;
import io.github.steaf23.bingoreloaded.core.data.TranslationData;
import io.github.steaf23.bingoreloaded.gui.base.InventoryItem;
import io.github.steaf23.bingoreloaded.gui.base.MenuInventory;
import io.github.steaf23.bingoreloaded.util.GUIPreset5x9;
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
    private final BingoSettings settings;
    private final EnumSet<EffectOptionFlags> flags;

    private final InventoryItem[] options;

    public EffectOptionsMenu(MenuInventory parent, BingoSettings settings)
    {
        super(45, TranslationData.translate("menu.options.effects.name"), parent);
        this.settings = settings;

        options = new InventoryItem[]{
                new InventoryItem(GUIPreset5x9.FIVE_TOP_WIDE.positions[0],
                        Material.CARROT, ""),
                new InventoryItem(GUIPreset5x9.FIVE_TOP_WIDE.positions[1],
                        Material.PUFFERFISH, ""),
                new InventoryItem(GUIPreset5x9.FIVE_TOP_WIDE.positions[2],
                        Material.MAGMA_CREAM, ""),
                new InventoryItem(GUIPreset5x9.FIVE_TOP_WIDE.positions[3],
                        Material.NETHERITE_BOOTS, ""),
                new InventoryItem(GUIPreset5x9.FIVE_TOP_WIDE.positions[4],
                        Material.FEATHER, ""),
                new InventoryItem(44, Material.DIAMOND,
                        "" + ChatColor.AQUA + ChatColor.BOLD + TranslationData.translate("menu.save_exit"))
        };
        fillOptions(options);

        flags = settings.effects;
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
            settings.effects = flags;
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
                    meta.setDisplayName("" + ChatColor.GREEN + ChatColor.BOLD + EffectOptionFlags.values()[i].name + " " + TranslationData.translate("menu.effects.enabled"));
                    meta.setLore(List.of(ChatColor.GREEN + TranslationData.translate("menu.effects.disable")));
                }
                else
                {
                    meta.setDisplayName("" + ChatColor.RED + ChatColor.BOLD + EffectOptionFlags.values()[i].name + " " + TranslationData.translate("menu.effects.disabled"));
                    meta.setLore(List.of(ChatColor.RED + TranslationData.translate("menu.effects.enable")));
                }
            }
            options[i].setItemMeta(meta);
            addOption(options[i]);
        }
    }
}
