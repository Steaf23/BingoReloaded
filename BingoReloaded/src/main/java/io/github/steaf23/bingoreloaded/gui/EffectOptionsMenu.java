package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.settings.BingoSettingsBuilder;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.gui.base.MenuItem;
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
    private final MenuItem[] options;

    private final BingoSession session;

    public EffectOptionsMenu(MenuInventory parent, BingoSettingsBuilder settings, BingoSession session)
    {
        super(45, BingoTranslation.OPTIONS_EFFECTS.translate(), parent);
        this.settings = settings;
        this.session = session;

        options = new MenuItem[]{
                new MenuItem(4, 3,
                        Material.CARROT, ""),
                new MenuItem(2, 3,
                        Material.PUFFERFISH, ""),
                new MenuItem(6, 3,
                        Material.MAGMA_CREAM, ""),
                new MenuItem(3, 1,
                        Material.NETHERITE_BOOTS, ""),
                new MenuItem(5, 1,
                        Material.FEATHER, ""),
                new MenuItem(44, Material.DIAMOND,
                        "" + ChatColor.AQUA + ChatColor.BOLD + BingoTranslation.MENU_SAVE_EXIT.translate())
        };
        addItems(options);

        flags = settings.view().effects();
        updateUI();
    }

    @Override
    public void onItemClicked(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
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
            settings.effects(flags, session);
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
                    meta.setDisplayName("" + ChatColor.GREEN + ChatColor.BOLD + EffectOptionFlags.values()[i].name + " " + BingoTranslation.EFFECTS_ENABLED.translate());
                    meta.setLore(List.of(ChatColor.GREEN + BingoTranslation.EFFECTS_DISABLE.translate()));
                }
                else
                {
                    meta.setDisplayName("" + ChatColor.RED + ChatColor.BOLD + EffectOptionFlags.values()[i].name + " " + BingoTranslation.EFFECTS_DISABLED.translate());
                    meta.setLore(List.of(ChatColor.RED + BingoTranslation.EFFECTS_ENABLE.translate()));
                }
            }
            options[i].setItemMeta(meta);
            addItem(options[i]);
        }
    }
}
