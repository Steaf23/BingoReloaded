package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.settings.BingoSettingsBuilder;
import io.github.steaf23.bingoreloaded.data.BingoSettingsData;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.gui.base.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.ArrayList;
import java.util.List;

public class ExtraBingoMenu extends MenuInventory
{
    private static final int DURATION_MAX = 60;
    private static final int TEAMSIZE_MAX = 64;
    private final BingoSettingsBuilder settings;
    private final ConfigData config;
    private final MenuItem exit = new MenuItem(36,
            Material.BARRIER, TITLE_PREFIX + BingoTranslation.MENU_PREV.translate());
    private final MenuItem teamSize = new MenuItem(2, 2,
            Material.ENDER_EYE, TITLE_PREFIX + "Maximum Team Size",
            ChatColor.GRAY + "(When changing this setting all currently",
            ChatColor.GRAY + "joined players will be kicked from their teams!)");
    private final MenuItem countdown = new MenuItem(4, 1,
            Material.CLOCK, TITLE_PREFIX + "Enable Countdown Timer");
    private final MenuItem gameDuration = new MenuItem(4, 3,
            Material.RECOVERY_COMPASS, TITLE_PREFIX + "Countdown Duration");

    private final MenuItem presets = new MenuItem(6, 2,
            Material.MINECART, TITLE_PREFIX + "Manage Presets");

    public ExtraBingoMenu(MenuInventory parent, BingoSettingsBuilder settings, ConfigData config)
    {
        super(45, BingoTranslation.OPTIONS_TITLE.translate(), parent);
        this.settings = settings;
        this.config = config;
        countdown.setGlowing(settings.view().enableCountdown());
    }

    @Override
    public void onItemClicked(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        BingoSettings view = settings.view();
        if (slotClicked == exit.getSlot())
        {
            close(player);
        }
        else if (slotClicked == teamSize.getSlot())
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
        else if (slotClicked == presets.getSlot())
        {
            showPresetMenu(player);
        }
    }

    @Override
    public void handleClose(final InventoryCloseEvent event)
    {
        settings.countdownGameDuration(gameDuration.getAmount());
        settings.maxTeamSize(teamSize.getAmount());
        super.handleClose(event);
    }

    @Override
    public void handleOpen(InventoryOpenEvent event)
    {
        super.handleOpen(event);
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

        presets.setDescription(
                ChatColor.GRAY + "Click to apply settings from saved presets"
        );
        addItems(exit, teamSize, gameDuration, countdown, presets);
    }

    public void showPresetMenu(Player player)
    {
        BingoSettingsData settingsData = new BingoSettingsData();

        new PaginatedPickerMenu(new ArrayList<>(), "Setting Presets", this, FilterType.DISPLAY_NAME)
        {
            private static final MenuItem SAVE_PRESET = new MenuItem(51, Material.EMERALD,
                    "" + ChatColor.GREEN + ChatColor.BOLD + "Add preset from current settings");

            @Override
            public void onOptionClickedDelegate(InventoryClickEvent event, MenuItem clickedOption, Player player)
            {
                if (event.isLeftClick())
                {
                    settings.fromOther(settingsData.getSettings(clickedOption.getCompareKey()));
                    close(player);
                }
                else if (event.isRightClick())
                {
                    new ContextMenu(clickedOption.getItemMeta().getDisplayName(), this)
                            .addAction("Remove", Material.BARRIER, clickType -> {
                                settingsData.removeSettings(clickedOption.getCompareKey());
                                return true;
                            })
                            .addAction("Duplicate", Material.SHULKER_SHELL, clickType -> {
                                BingoSettings oldSettings = settingsData.getSettings(clickedOption.getCompareKey());
                                settingsData.saveSettings(clickedOption.getCompareKey() + "_copy", oldSettings);
                                return true;
                            })
                            .addAction("Rename", Material.NAME_TAG, clickType -> {
                                BingoSettings oldSettings = settingsData.getSettings(clickedOption.getCompareKey());
                                settingsData.removeSettings(clickedOption.getCompareKey());
                                UserInputMenu.open("Rename preset...", input -> {
                                    settingsData.saveSettings(input, oldSettings);
                                }, player, this, clickedOption.getCompareKey());
                                return false;
                            })
                            .addAction(new MenuItem(Material.GLOBE_BANNER_PATTERN, TITLE_PREFIX + "Overwrite",
                                    "This will overwrite the settings saved in ",
                                    clickedOption.getCompareKey() + " with the currently selected options!"), clickType -> {
                                settingsData.saveSettings(clickedOption.getCompareKey(), settings.view());
                                return true;
                            })
                            .open(player);
                }
            }

            @Override
            public void handleOpen(InventoryOpenEvent event)
            {
                super.handleOpen(event);

                addItem(SAVE_PRESET);
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
                addPickerContents(items.toArray(new MenuItem[]{}));
            }

            @Override
            public void onItemClicked(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
            {
                if (slotClicked == SAVE_PRESET.getSlot())
                {
                    UserInputMenu.open("Rename preset...", input -> {
                        settingsData.saveSettings(input, settings.view());
                    }, player, this, "my_settings");
                }
                super.onItemClicked(event, slotClicked, player, clickType);
            }

            @Override
            public void handleClose(InventoryCloseEvent event)
            {
                super.handleClose(event);
                settings.settingsUpdated();
            }
        }.open(player);
    }
}
