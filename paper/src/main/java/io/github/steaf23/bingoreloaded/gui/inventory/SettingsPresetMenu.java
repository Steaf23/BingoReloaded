package io.github.steaf23.bingoreloaded.gui.inventory;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoSettingsData;
import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.InventoryMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.PaginatedDataMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.UserInputMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.action.MenuAction;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.settings.BingoSettingsBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;

import java.util.ArrayList;

public class SettingsPresetMenu extends PaginatedDataMenu.TextDataMenu
{
    private final BingoSettingsBuilder settingsBuilder;
    private final BingoSettingsData settingsData;

    private PlayerHandle cachedPlayer = null;

    public SettingsPresetMenu(MenuBoard board, BingoSettingsBuilder settingsBuilder) {
        super(board, Component.text("Setting Presets"), new ArrayList<>());

        this.settingsData = new BingoSettingsData();
        this.settingsBuilder = settingsBuilder;
    }

    private static final ItemTemplate SAVE_PRESET = new ItemTemplate(51, ItemTypePaper.of(Material.EMERALD),
            Component.text("Add preset from current settings").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));

    @Override
    public void beforeOpening(PlayerHandle player) {
        cachedPlayer = player;
        addAction(SAVE_PRESET, arguments -> {
            new UserInputMenu(getMenuBoard(), Component.text("Rename preset..."), input -> {
                settingsData.saveSettings(input, settingsBuilder.view());
                settingsBuilder.fromOther(settingsBuilder.view(), input);
                beforeOpening(arguments.player());
            }, "my_settings")
                    .open(player);
        });
        setData(settingsData.getPresetNames());

        super.beforeOpening(player);
    }

    @Override
    public void beforeClosing(PlayerHandle player) {
        settingsBuilder.settingsUpdated();
        super.beforeClosing(player);
    }

    @Override
    public void onOptionClickedDelegate(MenuAction.ActionArguments args, String clickedPreset) {
        PlayerHandle player = args.player();
        if (args.isLeftClick()) {
            settingsBuilder.fromOther(settingsData.getSettings(clickedPreset), clickedPreset);
            close(player);
        } else if (args.isRightClick() && BingoReloaded.isAdmin(player)) {
            BasicMenu context = new BasicMenu(getMenuBoard(), LegacyComponentSerializer.legacySection().deserialize(clickedPreset), 1);
            context.addAction(new ItemTemplate(0, ItemTypePaper.of(Material.BARRIER), BingoReloaded.applyTitleFormat("Remove")), clickType -> {
                settingsData.removeSettings(clickedPreset);
                context.close(player);
            });
            context.addAction(new ItemTemplate(1, ItemTypePaper.of(Material.SHULKER_SHELL), BingoReloaded.applyTitleFormat("Duplicate")), clickType -> {
                BingoSettings oldSettings = settingsData.getSettings(clickedPreset);
                settingsData.saveSettings(clickedPreset + "_copy", oldSettings);
                context.close(player);
            });
            context.addAction(new ItemTemplate(2, ItemTypePaper.of(Material.NAME_TAG), BingoReloaded.applyTitleFormat("Rename")), clickType -> {
                BingoSettings oldSettings = settingsData.getSettings(clickedPreset);
                settingsData.removeSettings(clickedPreset);
                new UserInputMenu(getMenuBoard(), Component.text("Rename preset to:"), input -> {
                    settingsData.saveSettings(input, oldSettings);
                    context.close(player);
                }, clickedPreset)
                        .open(player);
            });
            context.addAction(new ItemTemplate(3, ItemTypePaper.of(Material.GLOBE_BANNER_PATTERN), BingoReloaded.applyTitleFormat("Overwrite"),
                    Component.text("This will overwrite the settings saved in "),
                    Component.text(clickedPreset + " with the currently selected options!")), clickType -> {
                settingsData.saveSettings(clickedPreset, settingsBuilder.view());
                context.close(player);
            });
            context.addAction(new ItemTemplate(4, ItemTypePaper.of(Material.AMETHYST_SHARD), BingoReloaded.applyTitleFormat("Set As Default")), clickType -> {
                settingsData.setDefaultSettings(clickedPreset);
                context.close(player);
            });
            context.addCloseAction(new ItemTemplate(8, ItemTypePaper.of(Material.DIAMOND), BingoReloaded.applyTitleFormat("Exit")))
                    .open(player);
        }
    }

    @Override
    public Material material(String preset, boolean selected) {
        return Material.GLOBE_BANNER_PATTERN;
    }

    @Override
    public Component displayName(String preset, boolean selected) {
        boolean def = preset.equals(settingsData.getDefaultSettingsName());
        Component name = LegacyComponentSerializer.legacySection().deserialize(preset);
        if (def) {
            name = name.append(Component.text(" (default)").color(NamedTextColor.LIGHT_PURPLE));
        }
        return name;
    }

    @Override
    public ItemTemplate editItem(ItemTemplate item, String preset, boolean selected) {
        if (cachedPlayer != null && BingoReloaded.isAdmin(cachedPlayer)) {
            item.addDescription("input", 5,
                    InventoryMenu.INPUT_LEFT_CLICK.append(Component.text("apply this preset")),
                    InventoryMenu.INPUT_RIGHT_CLICK.append(Component.text("more options")));
        } else {
            item.addDescription("input", 5,
                    InventoryMenu.INPUT_LEFT_CLICK.append(Component.text("apply this preset")));
        }

        return item;
    }
}
