package io.github.steaf23.bingoreloaded.gui.inventory;

import io.github.steaf23.bingoreloaded.data.BingoSettingsData;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.settings.BingoSettingsBuilder;
import io.github.steaf23.easymenulib.inventory.*;
import io.github.steaf23.easymenulib.inventory.item.ItemTemplate;
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
        super(board, "Setting Presets", new ArrayList<>(), FilterType.ITEM_KEY);

        this.settingsData = new BingoSettingsData();
        this.settingsBuilder = settingsBuilder;
    }

    private static final ItemTemplate SAVE_PRESET = new ItemTemplate(51, Material.EMERALD,
            "" + ChatColor.GREEN + ChatColor.BOLD + "Add preset from current settings");

    @Override
    public void onOptionClickedDelegate(InventoryClickEvent event, ItemTemplate clickedOption, HumanEntity player)
    {
        if (event.isLeftClick())
        {
            settingsBuilder.fromOther(settingsData.getSettings(clickedOption.getCompareKey()));
            close(player);
        }
        else if (event.isRightClick())
        {
            BasicMenu context = new BasicMenu(getMenuBoard(), clickedOption.getName(), 1);
            context.addAction(new ItemTemplate(0, Material.BARRIER, TITLE_PREFIX + "Remove"), clickType -> {
                        settingsData.removeSettings(clickedOption.getCompareKey());
                        context.close(player);
                    })
                    .addAction(new ItemTemplate(1, Material.SHULKER_SHELL, TITLE_PREFIX + "Duplicate"), clickType -> {
                        BingoSettings oldSettings = settingsData.getSettings(clickedOption.getCompareKey());
                        settingsData.saveSettings(clickedOption.getCompareKey() + "_copy", oldSettings);
                        context.close(player);
                    })
                    .addAction(new ItemTemplate(2, Material.NAME_TAG, TITLE_PREFIX + "Rename"), clickType -> {
                        BingoSettings oldSettings = settingsData.getSettings(clickedOption.getCompareKey());
                        settingsData.removeSettings(clickedOption.getCompareKey());
                        new UserInputMenu(getMenuBoard(), "Rename preset...", input -> {
                            settingsData.saveSettings(input, oldSettings);
                            context.close(player);
                        }, player, clickedOption.getCompareKey());
                    })
                    .addAction(new ItemTemplate(3, Material.GLOBE_BANNER_PATTERN, TITLE_PREFIX + "Overwrite",
                            "This will overwrite the settings saved in ",
                            clickedOption.getCompareKey() + " with the currently selected options!"), clickType -> {
                        settingsData.saveSettings(clickedOption.getCompareKey(), settingsBuilder.view());
                        context.close(player);
                    })
                    .addAction(new ItemTemplate(4, Material.AMETHYST_SHARD, TITLE_PREFIX + "Set As Default"), clickType -> {
                        settingsData.setDefaultSettings(clickedOption.getCompareKey());
                        context.close(player);
                    })
                    .addCloseAction(new ItemTemplate(8, Material.DIAMOND, TITLE_PREFIX + "Exit"))
                    .open(player);
        }
    }

    @Override
    public void beforeOpening(HumanEntity player) {
        addAction(SAVE_PRESET, arguments -> {
            new UserInputMenu(getMenuBoard(), "Rename preset...", input -> {
                settingsData.saveSettings(input, settingsBuilder.view());
                beforeOpening(arguments.player());
            }, player, "my_settings");
        });
        clearItems();

        List<ItemTemplate> items = new ArrayList<>();
        for (String preset : settingsData.getPresetNames())
        {
            boolean def = preset.equals(settingsData.getDefaultSettingsName());
            ItemTemplate item = new ItemTemplate(Material.GLOBE_BANNER_PATTERN,
                    preset + (def ? ChatColor.LIGHT_PURPLE + " (default)" : ""));
            item.addDescription("input", 5,
                    Menu.INPUT_LEFT_CLICK + "apply this preset",
                    Menu.INPUT_RIGHT_CLICK + "more options");
            item.setCompareKey(preset);
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
