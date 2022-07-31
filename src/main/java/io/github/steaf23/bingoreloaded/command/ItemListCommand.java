package io.github.steaf23.bingoreloaded.command;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.AdvancementData;
import io.github.steaf23.bingoreloaded.data.BingoSlotsData;
import io.github.steaf23.bingoreloaded.gui.FilterType;
import io.github.steaf23.bingoreloaded.gui.ListPickerUI;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.item.ItemCardSlot;
import io.github.steaf23.bingoreloaded.util.FlexibleColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class ItemListCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String name, String[] args)
    {
        if (commandSender instanceof Player p && !p.hasPermission("bingo.manager"))
        {
            return false;
        }

        if (args.length > 0)
        {
            switch (args[0])
            {
                case "list":
                    if (commandSender instanceof Player p)
                        BingoReloaded.print("These are all existing lists: " + ChatColor.GOLD + BingoSlotsData.getListNames(), p);
                    else if (commandSender instanceof ConsoleCommandSender cmd)
                    {
                        BingoReloaded.print("These are all existing lists: " + BingoSlotsData.getListNames());
                    }
                    break;

                case "create":
                    if (args.length < 2)
                    {
                        BingoReloaded.broadcast(ChatColor.RED + "Please provide item list name: /itemlist create <list_name>");
                        break;
                    }
                    if (commandSender instanceof Player p)
                        editList(args[1], p);
                    break;

                case "edit":
                    if (args.length < 2)
                    {
                        BingoReloaded.broadcast(ChatColor.RED + "Please provide item list name: /itemlist edit <list_name>");
                        break;
                    }
                    if (commandSender instanceof Player p)
                        editList(args[1], p);
                    break;

                case "adv":
                    if (commandSender instanceof Player p)
                        editAdvancementList(args[1], p);
                    break;

                case "remove":
                    if (commandSender instanceof Player p)
                    {
                        if (args.length < 2)
                        {
                            BingoReloaded.print(ChatColor.RED + "Please provide item list name: /itemlist remove <list_name>", p);
                            break;
                        }

                        if (BingoSlotsData.removeList(args[1]))
                            BingoReloaded.print("Item list '" + args[1] + "' successfully removed!", p);
                        else
                            BingoReloaded.print("Item list couldn't be found, make sure its spelled correctly!", p);
                        break;
                    }
                    else if (commandSender instanceof ConsoleCommandSender)
                    {
                        if (args.length < 2)
                        {
                            BingoReloaded.print(ChatColor.RED + "Please provide item list name: /itemlist remove <item_list>");
                            break;
                        }

                        if (BingoSlotsData.removeList(args[1]))
                            BingoReloaded.print("Item list '" + args[1] + "' successfully removed!");
                        else
                            BingoReloaded.print("Item list couldn't be found, make sure its spelled correctly!");
                        break;
                    }

                    break;

                default:
                    if (commandSender instanceof Player player)
                        BingoReloaded.print(ChatColor.RED + "Usage: /itemlist [list | create | edit | remove]", player);
                    else
                        BingoReloaded.print(ChatColor.RED + "Usage: /itemlist [list | create | edit | remove]");
                    break;
            }
        }
        return false;
    }

    public void editList(String listName, Player player)
    {
        List<Material> glassPanes = new ArrayList<>();
        for (FlexibleColor flexColor : FlexibleColor.values())
        {
            glassPanes.add(flexColor.glassPane);
        }

        List<InventoryItem> items = new ArrayList<>();
        for (Material m : Material.values())
        {
            if (!m.name().contains("LEGACY_") && m.isItem() && !m.isAir() && !glassPanes.contains(m))
            {
                InventoryItem item = new InventoryItem(m, "", ChatColor.GRAY + "Click to make this item", "appear on bingo cards");
                ItemMeta meta = item.getItemMeta();
                if (meta == null) continue;

                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

                item.setItemMeta(meta);
                items.add(item);
            }
        }

        ListPickerUI itemPicker = new ListPickerUI(items,"Editing '" + listName + "'", null, FilterType.MATERIAL)
        {
            private final String itemListName = listName;

            @Override
            public void onOptionClickedDelegate(InventoryClickEvent event, InventoryItem clickedOption, Player player)
            {
                if (event.getClick().isLeftClick())
                {
                    incrementItemCount(clickedOption);
                }
                else
                {
                    decrementItemCount(clickedOption);
                }
            }

            @Override
            public void open(HumanEntity player)
            {
                List<ItemCardSlot> items = BingoSlotsData.getItemSlots(itemListName);
                BingoReloaded.print("Size: " + items.size());
                List<InventoryItem> allItems = getItems();

                items.forEach(slot -> {
                    String mat = slot.item.getType().name();
                    Optional<InventoryItem> item = allItems.stream().filter((i) -> i.getType().name() == mat).findFirst();
                    item.ifPresent(inventoryItem -> {
                        selectItem(inventoryItem, true);
                        inventoryItem.setAmount(slot.count);
                    });
                });

                updatePage();

                super.open(player);
            }

            @Override
            public void close(HumanEntity player)
            {
                List<ItemCardSlot> slots = new ArrayList<>();
                getSelectedItems().forEach((item) -> {
                    ItemCardSlot newItem = new ItemCardSlot(item.getType());
                    newItem.count = item.getAmount();
                    slots.add(newItem);
                });
                BingoSlotsData.saveItemSlots(itemListName, slots.toArray(ItemCardSlot[]::new));
                super.close(player);
            }

            public void incrementItemCount(InventoryItem item)
            {
                boolean select = false;
                if (!getSelectedItems().contains(item))
                {
                    select = true;
                }

                if (!select)
                {
                    item.setAmount(Math.min(item.getMaxStackSize(), item.getAmount() + 1));
                }


                if (select)
                {
                    selectItem(item,true);
                }
                updatePage();
            }

            public void decrementItemCount(InventoryItem item)
            {
                boolean deselect = false;
                if (getSelectedItems().contains(item))
                {
                    if (item.getAmount() == 1)
                    {
                        deselect = true;
                    }
                }
                item.setAmount(Math.max(1, item.getAmount() - 1));

                if (deselect)
                {
                    selectItem(item, false);
                }
                updatePage();
            }
        };
        itemPicker.open(player);
    }

    public void editAdvancementList(String listName, Player player)
    {
        List<InventoryItem> options = new ArrayList<>();
        for (Iterator<Advancement> it = Bukkit.advancementIterator(); it.hasNext(); )
        {
            Advancement a = it.next();
            String key = a.getKey().getKey();
            if (key.startsWith("recipes/") || key.endsWith("/root"))
            {
                continue;
            }
            InventoryItem item = new InventoryItem(Material.PAPER, AdvancementData.getAdvancementTitle(key));
            options.add(item);
        }

        ListPickerUI advancementPicker = new ListPickerUI(options, "Add Advancements", null, FilterType.DISPLAY_NAME)
        {
            @Override
            public void onOptionClickedDelegate(InventoryClickEvent event, InventoryItem clickedOption, Player player)
            {
                selectItem(clickedOption, !getSelectedItems().contains(clickedOption));
            }
        };
        advancementPicker.open(player);
    }
}
