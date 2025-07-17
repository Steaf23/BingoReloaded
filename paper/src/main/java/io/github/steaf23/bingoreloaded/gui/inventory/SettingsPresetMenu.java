package io.github.steaf23.bingoreloaded.gui.inventory;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoSettingsData;
import io.github.steaf23.bingoreloaded.lib.api.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.dialog.TextInputDialog;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.settings.BingoSettingsBuilder;
import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.FilterType;
import io.github.steaf23.bingoreloaded.lib.inventory.InventoryMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.PaginatedSelectionMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.UserInputMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

public class SettingsPresetMenu extends PaginatedSelectionMenu
{
    private final BingoSettingsBuilder settingsBuilder;
    private final BingoSettingsData settingsData;

    public SettingsPresetMenu(MenuBoard board, BingoSettingsBuilder settingsBuilder) {
        super(board, Component.text("Setting Presets"), new ArrayList<>(), FilterType.ITEM_ID);

        this.settingsData = new BingoSettingsData();
        this.settingsBuilder = settingsBuilder;
    }

    private static final ItemTemplate SAVE_PRESET = new ItemTemplate(51, ItemTypePaper.of(Material.EMERALD),
            Component.text("Add preset from current settings").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));

    @Override
    public void onOptionClickedDelegate(InventoryClickEvent event, ItemTemplate clickedOption, PlayerHandle player) {
        if (event.isLeftClick()) {
            settingsBuilder.fromOther(settingsData.getSettings(clickedOption.getCompareKey()));
            close(player);
        } else if (event.isRightClick()) {
            BasicMenu context = new BasicMenu(getMenuBoard(), clickedOption.getName(), 1);
            context.addAction(new ItemTemplate(0, ItemTypePaper.of(Material.BARRIER), BingoReloaded.applyTitleFormat("Remove")), clickType -> {
                        settingsData.removeSettings(clickedOption.getCompareKey());
                        context.close(player);
                    })
                    .addAction(new ItemTemplate(1, ItemTypePaper.of(Material.SHULKER_SHELL), BingoReloaded.applyTitleFormat("Duplicate")), clickType -> {
                        BingoSettings oldSettings = settingsData.getSettings(clickedOption.getCompareKey());
                        settingsData.saveSettings(clickedOption.getCompareKey() + "_copy", oldSettings);
                        context.close(player);
                    })
                    .addAction(new ItemTemplate(2, ItemTypePaper.of(Material.NAME_TAG), BingoReloaded.applyTitleFormat("Rename")), clickType -> {
                        BingoSettings oldSettings = settingsData.getSettings(clickedOption.getCompareKey());
                        settingsData.removeSettings(clickedOption.getCompareKey());
                        new TextInputDialog(getMenuBoard(), clickedOption.getCompareKey(), input -> {
                            settingsData.saveSettings(input, oldSettings);
                            context.close(player);
                        }, Component.text("Rename preset to:"), null)
                                .open(player);
                    })
                    .addAction(new ItemTemplate(3, ItemTypePaper.of(Material.GLOBE_BANNER_PATTERN), BingoReloaded.applyTitleFormat("Overwrite"),
                            Component.text("This will overwrite the settings saved in "),
                            Component.text(clickedOption.getCompareKey() + " with the currently selected options!")), clickType -> {
                        settingsData.saveSettings(clickedOption.getCompareKey(), settingsBuilder.view());
                        context.close(player);
                    })
                    .addAction(new ItemTemplate(4, ItemTypePaper.of(Material.AMETHYST_SHARD), BingoReloaded.applyTitleFormat("Set As Default")), clickType -> {
                        settingsData.setDefaultSettings(clickedOption.getCompareKey());
                        context.close(player);
                    })
                    .addCloseAction(new ItemTemplate(8, ItemTypePaper.of(Material.DIAMOND), BingoReloaded.applyTitleFormat("Exit")))
                    .open(player);
        }
    }

    @Override
    public void beforeOpening(PlayerHandle player) {
        addAction(SAVE_PRESET, arguments -> {
            new UserInputMenu(getMenuBoard(), Component.text("Rename preset..."), input -> {
                settingsData.saveSettings(input, settingsBuilder.view());
                beforeOpening(arguments.player());
            }, "my_settings")
                    .open(player);
        });
        clearItems();

        List<ItemTemplate> items = new ArrayList<>();
        for (String preset : settingsData.getPresetNames()) {
            boolean def = preset.equals(settingsData.getDefaultSettingsName());
            Component name = LegacyComponentSerializer.legacySection().deserialize(preset);
            if (def) {
                name = name.append(Component.text(" (default)").color(NamedTextColor.LIGHT_PURPLE));
            }
            ItemTemplate item = new ItemTemplate(ItemTypePaper.of(Material.GLOBE_BANNER_PATTERN), name)
                    .addDescription("input", 5,
                            InventoryMenu.INPUT_LEFT_CLICK.append(Component.text("apply this preset")),
                            InventoryMenu.INPUT_RIGHT_CLICK.append(Component.text("more options")))
                    .setCompareKey(preset);
            items.add(item);
        }
        addItemsToSelect(items);

        super.beforeOpening(player);
    }

    @Override
    public void beforeClosing(PlayerHandle player) {
        settingsBuilder.settingsUpdated();
        super.beforeClosing(player);
    }
}
