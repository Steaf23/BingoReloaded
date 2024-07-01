package io.github.steaf23.bingoreloaded.gui.inventory;

import io.github.steaf23.bingoreloaded.data.BingoSettingsData;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.settings.BingoSettingsBuilder;
import io.github.steaf23.playerdisplay.inventory.*;
import io.github.steaf23.playerdisplay.inventory.item.ItemTemplate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

public class SettingsPresetMenu extends PaginatedSelectionMenu
{
    private final BingoSettingsBuilder settingsBuilder;
    private final BingoSettingsData settingsData;

    public SettingsPresetMenu(MenuBoard board, BingoSettingsBuilder settingsBuilder) {
        super(board, Component.text("Setting Presets"), new ArrayList<>(), FilterType.ITEM_KEY);

        this.settingsData = new BingoSettingsData();
        this.settingsBuilder = settingsBuilder;
    }

    private static final ItemTemplate SAVE_PRESET = new ItemTemplate(51, Material.EMERALD,
            Component.text("Add preset from current settings").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));

    @Override
    public void onOptionClickedDelegate(InventoryClickEvent event, ItemTemplate clickedOption, HumanEntity player) {
        if (event.isLeftClick()) {
            settingsBuilder.fromOther(settingsData.getSettings(clickedOption.getCompareKey()));
            close(player);
        } else if (event.isRightClick()) {
            BasicMenu context = new BasicMenu(getMenuBoard(), clickedOption.getName(), 1);
            context.addAction(new ItemTemplate(0, Material.BARRIER, BasicMenu.applyTitleFormat("Remove")), clickType -> {
                        settingsData.removeSettings(clickedOption.getCompareKey());
                        context.close(player);
                    })
                    .addAction(new ItemTemplate(1, Material.SHULKER_SHELL, BasicMenu.applyTitleFormat("Duplicate")), clickType -> {
                        BingoSettings oldSettings = settingsData.getSettings(clickedOption.getCompareKey());
                        settingsData.saveSettings(clickedOption.getCompareKey() + "_copy", oldSettings);
                        context.close(player);
                    })
                    .addAction(new ItemTemplate(2, Material.NAME_TAG, BasicMenu.applyTitleFormat("Rename")), clickType -> {
                        BingoSettings oldSettings = settingsData.getSettings(clickedOption.getCompareKey());
                        settingsData.removeSettings(clickedOption.getCompareKey());
                        new UserInputMenu(getMenuBoard(), Component.text("Rename preset..."), input -> {
                            settingsData.saveSettings(input, oldSettings);
                            context.close(player);
                        }, clickedOption.getCompareKey())
                                .open(player);
                    })
                    .addAction(new ItemTemplate(3, Material.GLOBE_BANNER_PATTERN, BasicMenu.applyTitleFormat("Overwrite"),
                            Component.text("This will overwrite the settings saved in "),
                            Component.text(clickedOption.getCompareKey() + " with the currently selected options!")), clickType -> {
                        settingsData.saveSettings(clickedOption.getCompareKey(), settingsBuilder.view());
                        context.close(player);
                    })
                    .addAction(new ItemTemplate(4, Material.AMETHYST_SHARD, BasicMenu.applyTitleFormat("Set As Default")), clickType -> {
                        settingsData.setDefaultSettings(clickedOption.getCompareKey());
                        context.close(player);
                    })
                    .addCloseAction(new ItemTemplate(8, Material.DIAMOND, BasicMenu.applyTitleFormat("Exit")))
                    .open(player);
        }
    }

    @Override
    public void beforeOpening(HumanEntity player) {
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
                name = name.append(Component.text("(default)").color(NamedTextColor.LIGHT_PURPLE));
            }
            ItemTemplate item = new ItemTemplate(Material.GLOBE_BANNER_PATTERN, name)
                    .addDescription("input", 5,
                            Menu.INPUT_LEFT_CLICK.append(Component.text("apply this preset")),
                            Menu.INPUT_RIGHT_CLICK.append(Component.text("more options")))
                    .setCompareKey(preset);
            items.add(item);
        }
        addItemsToSelect(items);

        super.beforeOpening(player);
    }

    @Override
    public void beforeClosing(HumanEntity player) {
        settingsBuilder.settingsUpdated();
        super.beforeClosing(player);
    }
}
