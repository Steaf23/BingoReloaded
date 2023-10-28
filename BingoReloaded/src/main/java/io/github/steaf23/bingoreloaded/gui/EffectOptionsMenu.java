package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gui.base.BasicMenu;
import io.github.steaf23.bingoreloaded.gui.base.MenuItem;
import io.github.steaf23.bingoreloaded.gui.base.MenuManager;
import io.github.steaf23.bingoreloaded.settings.BingoSettingsBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.EnumSet;
import java.util.List;

public class EffectOptionsMenu extends BasicMenu
{
    private final EnumSet<EffectOptionFlags> flags;

    public EffectOptionsMenu(MenuManager menuManager, BingoSettingsBuilder settings, BingoSession session) {
        super(menuManager, BingoTranslation.OPTIONS_EFFECTS.translate(), 6);
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
                    close(player);
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
                }
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
        updateActionItem(menuItem);
    }
}
