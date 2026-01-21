package io.github.steaf23.bingoreloaded.gui.inventory;

import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.gui.inventory.core.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.player.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.settings.BingoSettingsBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;

import java.util.EnumSet;

public class EffectOptionsMenu extends BasicMenu
{
    private final EnumSet<EffectOptionFlags> flags;
    private final BingoSettingsBuilder settingsBuilder;

    public EffectOptionsMenu(MenuBoard menuBoard, BingoSettingsBuilder settings) {
        super(menuBoard, BingoMessage.OPTIONS_EFFECTS.asPhrase(), 6);
        this.settingsBuilder = settings;
        flags = settings.view().effects();

        for (int i = 0; i < 8; i++) {
            addItem(BLANK.copyToSlot(i, 5));
        }

        addEffectAction(EffectOptionFlags.NIGHT_VISION, 5, 3, Material.GOLDEN_CARROT);
        addEffectAction(EffectOptionFlags.WATER_BREATHING, 3, 3, Material.PUFFERFISH);
        addEffectAction(EffectOptionFlags.FIRE_RESISTANCE, 7, 3, Material.MAGMA_CREAM);
        addEffectAction(EffectOptionFlags.NO_FALL_DAMAGE, 2, 1, Material.NETHERITE_BOOTS);
        addEffectAction(EffectOptionFlags.SPEED, 1, 3, Material.FEATHER);
        addEffectAction(EffectOptionFlags.NO_DURABILITY, 6, 1, Material.NETHERITE_PICKAXE);
        addEffectAction(EffectOptionFlags.KEEP_INVENTORY, 4, 1, Material.CHEST);
        addCloseAction(new ItemTemplate(8, 5, ItemTypePaper.of(Material.DIAMOND), BingoMessage.MENU_SAVE_EXIT.asPhrase().color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD)));
    }

    private void addEffectAction(EffectOptionFlags flag, int slotX, int slotY, Material material) {
        ItemTemplate item = new ItemTemplate(slotX, slotY, ItemTypePaper.of(material), null);
        updateUI(flag, item);
        addAction(item,
                player -> {
                    toggleOption(flag);
                    updateUI(flag, item);
                }
        );
    }

    private void toggleOption(EffectOptionFlags flag) {
        if (flags.contains(flag))
            flags.remove(flag);
        else
            flags.add(flag);
    }

    public void updateUI(EffectOptionFlags flag, ItemTemplate item) {
        if (flags.contains(flag)) {
            item.setName(Component.text().append(flag.name, Component.text(" "), BingoMessage.EFFECTS_ENABLED.asPhrase()).color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD).build());
            item.setLore(BingoMessage.EFFECTS_DISABLE.asMultiline(NamedTextColor.GREEN));
        } else {
            item.setName(Component.text().append(flag.name, Component.text(" "), BingoMessage.EFFECTS_DISABLED.asPhrase()).color(NamedTextColor.RED).decorate(TextDecoration.BOLD).build());
            item.setLore(BingoMessage.EFFECTS_ENABLE.asMultiline(NamedTextColor.RED));
        }
    }

    @Override
    public void beforeClosing(PlayerHandle player) {
        settingsBuilder.effects(flags);
        super.beforeClosing(player);
    }
}
