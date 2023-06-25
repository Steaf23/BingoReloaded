package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.gui.base.BasicMenu;
import io.github.steaf23.bingoreloaded.gui.base.MenuManager;
import io.github.steaf23.bingoreloaded.gui.base.PaginatedSelectionMenu;
import io.github.steaf23.bingoreloaded.gui.base.UserInputMenu;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.settings.BingoSettingsBuilder;
import io.github.steaf23.bingoreloaded.data.BingoSettingsData;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.gui.base.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

public class ExtraBingoMenu extends BasicMenu
{
    private static final int DURATION_MAX = 60;
    private static final int TEAMSIZE_MAX = 64;
    private final BingoSettingsBuilder settings;
    private final ConfigData config;
    private static final MenuItem EXIT = new MenuItem(0, 5,
            Material.BARRIER, TITLE_PREFIX + BingoTranslation.MENU_PREV.translate());
    private static final MenuItem PRESETS = new MenuItem(6, 2,
            Material.MINECART, TITLE_PREFIX + "Manage Presets",
            ChatColor.GRAY + "Click to apply settings from saved presets");
    private final MenuItem teamSize = new MenuItem(2, 2,
            Material.ENDER_EYE, TITLE_PREFIX + "Maximum Team Size",
            ChatColor.GRAY + "(When changing this setting all currently",
            ChatColor.GRAY + "joined players will be kicked from their teams!)");
    private final MenuItem countdown = new MenuItem(4, 1,
            Material.CLOCK, TITLE_PREFIX + "Enable Countdown Timer");
    private final MenuItem gameDuration = new MenuItem(4, 3,
            Material.RECOVERY_COMPASS, TITLE_PREFIX + "Countdown Duration");

    public ExtraBingoMenu(MenuManager menuManager, BingoSettingsBuilder settings, ConfigData config)
    {
        super(menuManager, BingoTranslation.OPTIONS_TITLE.translate(), 6);
        this.settings = settings;
        this.config = config;
        countdown.setGlowing(settings.view().enableCountdown());

        for (int i = 1; i < 9; i++) {
            addItem(BLANK.copyToSlot(i, 5));
        }

        addCloseAction(EXIT);
        addAction(PRESETS, this::showPresetMenu);
    }

    @Override
    public boolean onClick(InventoryClickEvent event, HumanEntity player, MenuItem clickedItem, ClickType clickType) {
        BingoSettings view = settings.view();

        int slotClicked = event.getRawSlot();

        if (slotClicked == teamSize.getSlot())
        {
            if (clickType == ClickType.LEFT)
            {
                teamSize.setAmount(Math.min(TEAMSIZE_MAX, teamSize.getAmount() + 1));
            }
            else if (clickType == ClickType.RIGHT)
            {
                teamSize.setAmount(Math.max(1, teamSize.getAmount() - 1));
            }

            teamSize.setDescription(
                    ChatColor.GRAY + "(When changing this setting all currently",
                    ChatColor.GRAY + "joined players will be kicked from their teams!)",
                    "§7Maximum team size set to " + teamSize.getAmount() + " players.",
                    "§rUse the mouse buttons to increase/ decrease",
                    "the amount of players a team can have.");
            addItem(teamSize);
        }
        else if (slotClicked == countdown.getSlot())
        {
            settings.enableCountdown(!view.enableCountdown());
            countdown.setGlowing(!view.enableCountdown());
            addItem(countdown);
        }
        else if (slotClicked == gameDuration.getSlot())
        {
            if (clickType == ClickType.LEFT)
            {
                gameDuration.setAmount(Math.min(DURATION_MAX, gameDuration.getAmount() + 1));
            }
            else if (clickType == ClickType.RIGHT)
            {
                gameDuration.setAmount(Math.max(1, gameDuration.getAmount() - 1));
            }

            gameDuration.setDescription(
                    "§7Timer set to " + gameDuration.getAmount() + " minute(s) for Countdown Bingo.",
                    "§rUse the mouse buttons to increase/ decrease",
                    "the amount of minutes that Countdown bingo will last.");
            addItem(gameDuration);
        }
        return super.onClick(event, player, clickedItem, clickType);
    }

    @Override
    public void beforeClosing(HumanEntity player) {
        super.beforeClosing(player);
        settings.countdownGameDuration(gameDuration.getAmount());
        settings.maxTeamSize(teamSize.getAmount());
    }

    @Override
    public void beforeOpening(HumanEntity player) {
        super.beforeOpening(player);
        teamSize.setAmount(Math.max(0, Math.min(TEAMSIZE_MAX, settings.view().maxTeamSize())));
        teamSize.setDescription(
                ChatColor.GRAY + "(When changing this setting all currently",
                ChatColor.GRAY + "joined players will be kicked from their teams!)",
                "§7Maximum team size set to " + teamSize.getAmount() + " players.",
                "§rUse the mouse buttons to increase/ decrease",
                "the amount of players a team can have.");

        gameDuration.setAmount(Math.max(0, Math.min(DURATION_MAX, settings.view().countdownDuration())));
        gameDuration.setDescription(
                ChatColor.GRAY + "Timer set to " + gameDuration.getAmount() + " minute(s) for Countdown Bingo",
                "" + ChatColor.RESET + ChatColor.DARK_PURPLE + "Use the mouse buttons to increase/ decrease",
                "the amount of minutes that Countdown bingo will last.");

        addItems(teamSize, gameDuration, countdown);
    }

    public void showPresetMenu(HumanEntity player)
    {
        BingoSettingsData settingsData = new BingoSettingsData();

        new PaginatedSelectionMenu(getMenuManager(), "Setting Presets", new ArrayList<>(), FilterType.DISPLAY_NAME)
        {
            private static final MenuItem SAVE_PRESET = new MenuItem(51, Material.EMERALD,
                    "" + ChatColor.GREEN + ChatColor.BOLD + "Add preset from current settings");

            @Override
            public void onOptionClickedDelegate(InventoryClickEvent event, MenuItem clickedOption, HumanEntity player)
            {
                if (event.isLeftClick())
                {
                    settings.fromOther(settingsData.getSettings(clickedOption.getCompareKey()));
                    close(player);
                }
                else if (event.isRightClick())
                {
                    BasicMenu context = new BasicMenu(getMenuManager(), clickedOption.getItemMeta().getDisplayName(), 1);
                    context.addAction(new MenuItem(Material.BARRIER, TITLE_PREFIX + "Remove"), clickType -> {
                                settingsData.removeSettings(clickedOption.getCompareKey());
                                context.close(player);
                            })
                            .addAction(new MenuItem(Material.SHULKER_SHELL, TITLE_PREFIX + "Duplicate"), clickType -> {
                                BingoSettings oldSettings = settingsData.getSettings(clickedOption.getCompareKey());
                                settingsData.saveSettings(clickedOption.getCompareKey() + "_copy", oldSettings);
                                context.close(player);
                            })
                            .addAction(new MenuItem(Material.NAME_TAG, TITLE_PREFIX + "Rename"), clickType -> {
                                BingoSettings oldSettings = settingsData.getSettings(clickedOption.getCompareKey());
                                settingsData.removeSettings(clickedOption.getCompareKey());
                                new UserInputMenu(getMenuManager(), "Rename preset...", input -> {
                                    settingsData.saveSettings(input, oldSettings);
                                    context.close(player);
                                }, player, clickedOption.getCompareKey());
                            })
                            .addAction(new MenuItem(Material.GLOBE_BANNER_PATTERN, TITLE_PREFIX + "Overwrite",
                                    "This will overwrite the settings saved in ",
                                    clickedOption.getCompareKey() + " with the currently selected options!"), clickType -> {
                                settingsData.saveSettings(clickedOption.getCompareKey(), settings.view());
                                context.close(player);
                            })
                            .addCloseAction(new MenuItem(8, Material.DIAMOND, TITLE_PREFIX + "Exit"))
                            .open(player);
                }
            }

            @Override
            public void beforeOpening(HumanEntity player) {
                addAction(SAVE_PRESET, p -> {
                    new UserInputMenu(getMenuManager(), "Rename preset...", input -> {
                        settingsData.saveSettings(input, settings.view());
                    }, player, "my_settings");
                });
                clearItems();

                List<MenuItem> items = new ArrayList<>();
                for (String preset : settingsData.getPresetNames())
                {
                    boolean def = preset.equals(config.defaultSettingsPreset);
                    MenuItem item = new MenuItem(Material.GLOBE_BANNER_PATTERN,
                            preset + (def ? ChatColor.LIGHT_PURPLE + " (default)" : ""),
                            ChatColor.GRAY + "Left-click to apply these settings",
                            ChatColor.GRAY + "Right-click for more options",
                            def ? ChatColor.LIGHT_PURPLE + "The default preset can be changed in the general config file" : "");
                    item.setCompareKey(preset);
                    items.add(item);
                }
                addItemsToSelect(items.toArray(new MenuItem[]{}));

                super.beforeOpening(player);
            }

            @Override
            public void beforeClosing(HumanEntity player) {
                settings.settingsUpdated();
                super.beforeClosing(player);
            }
        }.open(player);
    }
}
