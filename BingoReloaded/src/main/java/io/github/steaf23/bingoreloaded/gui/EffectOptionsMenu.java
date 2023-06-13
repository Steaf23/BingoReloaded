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
import java.util.stream.Stream;

public class EffectOptionsMenu extends MenuInventory {
    private final BingoSettingsBuilder settings;
    private final EnumSet<EffectOptionFlags> flags;
    private final EffectOptionsMenuItem[] menuItems;

    private final BingoSession session;

    public EffectOptionsMenu(MenuInventory parent, BingoSettingsBuilder settings, BingoSession session) {
        super(45, BingoTranslation.OPTIONS_EFFECTS.translate(), parent);
        this.settings = settings;
        this.session = session;

        menuItems = new EffectOptionsMenuItem[]{
                new EffectOptionsMenuItem(EffectOptionFlags.NIGHT_VISION, 4, 3, Material.CARROT),
                new EffectOptionsMenuItem(EffectOptionFlags.WATER_BREATHING, 2, 3, Material.PUFFERFISH),
                new EffectOptionsMenuItem(EffectOptionFlags.FIRE_RESISTANCE, 6, 3, Material.MAGMA_CREAM),
                new EffectOptionsMenuItem(EffectOptionFlags.NO_FALL_DAMAGE, 2, 1, Material.NETHERITE_BOOTS),
                new EffectOptionsMenuItem(EffectOptionFlags.SPEED, 4, 1, Material.FEATHER),
                new EffectOptionsMenuItem(EffectOptionFlags.NO_DURABILITY, 6, 1, Material.NETHERITE_PICKAXE),
                new EffectOptionsMenuItem(
                        null,
                        44, Material.DIAMOND,
                        "" + ChatColor.AQUA + ChatColor.BOLD + BingoTranslation.MENU_SAVE_EXIT.translate()
                )
        };
        MenuItem[] options = Stream.of(menuItems).map(EffectOptionsMenuItem::getMenuItem).toArray(MenuItem[]::new);
        addItems(options);

        flags = settings.view().effects();
        for (EffectOptionsMenuItem menuItem : menuItems) {
            updateUI(menuItem);
        }
    }

    @Override
    public void onItemClicked(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType) {
        for (EffectOptionsMenuItem menuItem : menuItems) {
            if (slotClicked == menuItem.getMenuItem().getSlot()) {
                if (menuItem.getFlag() == null) {
                    settings.effects(flags, session);
                    close(player);
                } else {
                    toggleOption(menuItem);
                }
                return;
            }
        }
    }

    private void toggleOption(EffectOptionsMenuItem menuItem) {
        EffectOptionFlags flag = menuItem.getFlag();
        if (flags.contains(flag))
            flags.remove(flag);
        else
            flags.add(flag);
        updateUI(menuItem);
    }

    public void updateUI(EffectOptionsMenuItem effectOptionsMenuItem) {
        EffectOptionFlags flag = effectOptionsMenuItem.getFlag();
        if (flag == null) {
            return;
        }
        MenuItem menuItem = effectOptionsMenuItem.getMenuItem();
        ItemMeta meta = menuItem.getItemMeta();
        if (meta != null) {
            if (flags.contains(flag)) {
                meta.setDisplayName("" + ChatColor.GREEN + ChatColor.BOLD + flag.name + " " + BingoTranslation.EFFECTS_ENABLED.translate());
                meta.setLore(List.of(ChatColor.GREEN + BingoTranslation.EFFECTS_DISABLE.translate()));
            } else {
                meta.setDisplayName("" + ChatColor.RED + ChatColor.BOLD + flag.name + " " + BingoTranslation.EFFECTS_DISABLED.translate());
                meta.setLore(List.of(ChatColor.RED + BingoTranslation.EFFECTS_ENABLE.translate()));
            }
        }
        menuItem.setItemMeta(meta);
        addItem(menuItem);
    }
}
