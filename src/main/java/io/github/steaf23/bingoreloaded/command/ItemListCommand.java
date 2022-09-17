package io.github.steaf23.bingoreloaded.command;

import io.github.steaf23.bingoreloaded.Message;
import io.github.steaf23.bingoreloaded.data.BingoTasksData;
import io.github.steaf23.bingoreloaded.gui.FilterType;
import io.github.steaf23.bingoreloaded.gui.ListPickerUI;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.item.tasks.ItemTask;
import io.github.steaf23.bingoreloaded.util.FlexibleColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
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
                case "create":
                    if (!(commandSender instanceof Player p))
                        break;
                    if (args.length < 2)
                    {
                        new Message("command.list.no_name").color(ChatColor.DARK_RED).arg("/itemlist create <list_name>").send(p);
                        break;
                    }

                    editList(args[1], p);
                    break;

                case "remove":
                    if (commandSender instanceof Player p)
                    {
                        if (args.length < 2)
                        {
                            new Message("command.list.no_name").arg("/itemlist remove <list_name>").send(p);
                            break;
                        }
                        if (BingoTasksData.removeList(args[1]))
                            new Message("command.itemlist.removed").arg(args[1]).send(p);
                        else
                            new Message("command.itemlist.no_remove").arg(args[1]).send(p);
                        break;
                    }
                    else if (commandSender instanceof ConsoleCommandSender)
                    {
                        if (args.length < 2)
                        {
                            new Message("Please provide item list name: /itemlist remove <item_list>").color(ChatColor.RED);
                            break;
                        }

                        if (BingoTasksData.removeList(args[1]))
                            Message.log("Item list '" + args[1] + "' successfully removed!");
                        else
                            Message.log("Item list couldn't be found, make sure its spelled correctly!");
                        break;
                    }
                    break;

                default:
                    if (commandSender instanceof Player player)
                        new Message("command.usage").color(ChatColor.RED).arg("/itemlist [list | create | remove]").send(player);
                    else
                        Message.log(ChatColor.RED + "Usage: /itemlist [list | create | remove]");
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
                List<ItemTask> items = BingoTasksData.getItemTasks(itemListName);
                List<InventoryItem> allItems = getItems();

                items.forEach(slot -> {
                    String mat = slot.item.getType().name();
                    Optional<InventoryItem> item = allItems.stream().filter((i) -> i.getType().name() == mat).findFirst();
                    item.ifPresent(inventoryItem -> {
                        selectItem(inventoryItem, true);
                        inventoryItem.setAmount(slot.getCount());
                    });
                });

                updatePage();

                super.open(player);
            }

            @Override
            public void close(HumanEntity player)
            {
                List<ItemTask> slots = new ArrayList<>();
                getSelectedItems().forEach((item) -> {
                    ItemTask newItem = new ItemTask(item.getType(), item.getAmount());
                    slots.add(newItem);
                });
                BingoTasksData.saveItemTasks(itemListName, slots.toArray(ItemTask[]::new));
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
}
