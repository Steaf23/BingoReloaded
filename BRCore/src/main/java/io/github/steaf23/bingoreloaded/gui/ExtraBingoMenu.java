package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.BingoSession;
import io.github.steaf23.bingoreloaded.BingoSettings;
import io.github.steaf23.bingoreloaded.BingoSettingsBuilder;
import io.github.steaf23.bingoreloaded.data.BingoSettingsData;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.gui.base.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import javax.naming.InvalidNameException;
import java.net.InetSocketAddress;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class ExtraBingoMenu extends MenuInventory
{
    private static final int DURATION_MAX = 60;
    private final BingoSettingsBuilder settings;
    private final ConfigData config;
    private final InventoryItem exit = new InventoryItem(36,
            Material.BARRIER, TITLE_PREFIX + BingoTranslation.MENU_PREV.translate());
    private final InventoryItem countdown = new InventoryItem(2, 2,
            Material.CLOCK, TITLE_PREFIX + "Enable Countdown Timer");
    private final InventoryItem gameDuration = new InventoryItem(4, 2,
            Material.RECOVERY_COMPASS, TITLE_PREFIX + "Countdown Duration");

    private final InventoryItem presets = new InventoryItem(6, 2,
            Material.MINECART, TITLE_PREFIX + "Manage Presets");

    public ExtraBingoMenu(MenuInventory parent, BingoSettingsBuilder settings, ConfigData config)
    {
        super(45, BingoTranslation.OPTIONS_TITLE.translate(), parent);
        this.settings = settings;
        this.config = config;
        countdown.highlight(settings.view().enableCountdown());
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        BingoSettings view = settings.view();
        if (slotClicked == exit.getSlot())
        {
            close(player);
        }
        else if (slotClicked == countdown.getSlot())
        {
            settings.enableCountdown(!view.enableCountdown());
            countdown.highlight(!view.enableCountdown());
            addOption(countdown);
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
                    "§7Timer set to " + gameDuration.getAmount() + " minute(s) for Countdown Bingo",
                    "§rUse the mouse buttons to increase/ decrease",
                    "the amount of minutes that Countdown bingo will last.");
            addOption(gameDuration);
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
        super.handleClose(event);
    }

    @Override
    public void handleOpen(InventoryOpenEvent event)
    {
        super.handleOpen(event);
        gameDuration.setAmount(Math.max(0, Math.min(DURATION_MAX, settings.view().countdownDuration())));
        gameDuration.setDescription(
                ChatColor.GRAY + "Timer set to " + gameDuration.getAmount() + " minute(s) for Countdown Bingo",
                "" + ChatColor.RESET + ChatColor.DARK_PURPLE + "Use the mouse buttons to increase/ decrease",
                "the amount of minutes that Countdown bingo will last.");

        presets.setDescription(
                ChatColor.GRAY + "Click to apply settings from saved presets"
        );
        fillOptions(exit, gameDuration, countdown, presets);
    }

    public void showPresetMenu(Player player)
    {
        BingoSettingsData settingsData = new BingoSettingsData();

        new PaginatedPickerMenu(new ArrayList<>(), "Setting Presets", this, FilterType.DISPLAY_NAME)
        {
            private static final InventoryItem SAVE_PRESET = new InventoryItem(51, Material.EMERALD,
                    "" + ChatColor.GREEN + ChatColor.BOLD + "Add preset from current settings");

            @Override
            public void onOptionClickedDelegate(InventoryClickEvent event, InventoryItem clickedOption, Player player)
            {
                if (event.isLeftClick())
                {
                    settings.fromOther(settingsData.getSettings(clickedOption.getKey()));
                    close(player);
                }
                else if (event.isRightClick())
                {
                    new ContextMenu("What to do with '" + clickedOption.getItemMeta().getDisplayName() + "'", this)
                            .addAction("Remove", Material.BARRIER, clickType -> {
                                settingsData.removeSettings(clickedOption.getKey());
                                return true;
                            })
                            .addAction("Duplicate", Material.SHULKER_SHELL, clickType -> {
                                BingoSettings oldSettings = settingsData.getSettings(clickedOption.getKey());
                                settingsData.saveSettings(clickedOption.getKey() + "_copy", oldSettings);
                                return true;
                            })
                            .addAction("Rename", Material.NAME_TAG, clickType -> {
                                BingoSettings oldSettings = settingsData.getSettings(clickedOption.getKey());
                                settingsData.removeSettings(clickedOption.getKey());
                                UserInputMenu.open("Rename preset...", input -> {
                                    settingsData.saveSettings(input, oldSettings);
                                }, player, this, clickedOption.getKey());
                                return false;
                            })
                            .open(player);
                }
            }

            @Override
            public void handleOpen(InventoryOpenEvent event)
            {
                super.handleOpen(event);

                addOption(SAVE_PRESET);
                clearItems();

                List<InventoryItem> items = new ArrayList<>();
                for (String preset : settingsData.getPresetNames())
                {
                    boolean def = preset.equals(config.defaultSettings);
                    InventoryItem item = new InventoryItem(Material.GLOBE_BANNER_PATTERN,
                            preset + (def ? ChatColor.LIGHT_PURPLE + " (default)" : ""),
                            ChatColor.GRAY + "Left-click to apply these settings",
                            ChatColor.GRAY + "Right-click for more options",
                            def ? ChatColor.LIGHT_PURPLE + "The default preset can be changed in the general config file" : "");
                    item.setKey(preset);
                    items.add(item);
                }
                addItems(items.toArray(new InventoryItem[]{}));
            }

            @Override
            public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
            {
                if (slotClicked == SAVE_PRESET.getSlot())
                {
                    UserInputMenu.open("Rename preset...", input -> {
                        settingsData.saveSettings(input, settings.view());
                    }, player, this, "my_settings");
                }
                super.delegateClick(event, slotClicked, player, clickType);
            }
        }.open(player);
    }
}
