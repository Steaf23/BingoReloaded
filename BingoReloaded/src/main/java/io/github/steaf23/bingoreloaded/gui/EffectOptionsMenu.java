package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gui.base.ActionMenu;
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

public class EffectOptionsMenu extends ActionMenu {
    private final EnumSet<EffectOptionFlags> flags;

    public EffectOptionsMenu(MenuInventory parent, BingoSettingsBuilder settings, BingoSession session) {
        super(45, BingoTranslation.OPTIONS_EFFECTS.translate(), parent);
        flags = settings.view().effects();

        addEffectAction(EffectOptionFlags.NIGHT_VISION, 5, 3, Material.GOLDEN_CARROT);
        addEffectAction(EffectOptionFlags.WATER_BREATHING, 3, 3, Material.PUFFERFISH);
        addEffectAction(EffectOptionFlags.FIRE_RESISTANCE, 7, 3, Material.MAGMA_CREAM);
        addEffectAction(EffectOptionFlags.NO_FALL_DAMAGE, 2, 1, Material.NETHERITE_BOOTS);
        addEffectAction(EffectOptionFlags.SPEED, 1, 3, Material.FEATHER);
        addEffectAction(EffectOptionFlags.NO_DURABILITY, 6, 1, Material.NETHERITE_PICKAXE);
        addEffectAction(EffectOptionFlags.KEEP_INVENTORY, 4, 1, Material.CHEST);
        addAction(
                new MenuItem(
                        44,
                        Material.DIAMOND,
                        "" + ChatColor.AQUA + ChatColor.BOLD + BingoTranslation.MENU_SAVE_EXIT.translate()
                ),
                (player) -> {
                    settings.effects(flags, session);
                }
        );
    }

    private void addEffectAction(EffectOptionFlags flag, int slotX, int slotY, Material material) {
        MenuItem item = new MenuItem(slotX, slotY, material, "");
        addAction(
                item,
                (player) -> {
                    toggleOption(flag);
                    updateUI(flag, item);
                },
                false
        );
        updateUI(flag, item);
    }

    private void toggleOption(EffectOptionFlags flag) {
        if (flags.contains(flag))
            flags.remove(flag);
        else
            flags.add(flag);
    }

    public void updateUI(EffectOptionFlags flag, MenuItem menuItem) {
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
