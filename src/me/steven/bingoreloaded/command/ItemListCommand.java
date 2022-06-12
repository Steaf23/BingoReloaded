package me.steven.bingoreloaded.command;

import me.steven.bingoreloaded.BingoReloaded;
import me.steven.bingoreloaded.gui.ItemPickerUI;
import me.steven.bingoreloaded.item.InventoryItem;
import me.steven.bingoreloaded.data.BingoItemData;
import me.steven.bingoreloaded.util.FlexibleColor;
import org.bukkit.ChatColor;
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

                case "view":
                    break;

                case "remove":
                    if (commandSender instanceof Player p)
                    {
                        if (args.length < 2)
                        {
                            BingoReloaded.print(ChatColor.RED + "Please provide item list name: /itemlist remove <list_name>", p);
                            break;
                        }

                        if (BingoItemData.removeItemList(args[1]))
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

                        if (BingoItemData.removeItemList(args[1]))
                            BingoReloaded.print("Item list '" + args[1] + "' successfully removed!");
                        else
                            BingoReloaded.print("Item list couldn't be found, make sure its spelled correctly!");
                        break;
                    }

                    break;

                default:
                    if (commandSender instanceof Player player)
                        BingoReloaded.print(ChatColor.RED + "Usage: /itemlist [create|edit|remove]", player);
                    else
                        BingoReloaded.print(ChatColor.RED + "Usage: /itemlist [create|edit|remove]");
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

        ItemPickerUI itemPicker = new ItemPickerUI(items,"Editing '" + listName + "'", null)
        {
            private final String itemListName = listName;

            @Override
            public void onOptionClickedDelegate(InventoryClickEvent event, InventoryItem clickedOption, Player player)
            {
                selectItem(clickedOption, !getSelectedItems().contains(clickedOption));
            }

            @Override
            public void open(HumanEntity player)
            {
                List<Material> items = BingoItemData.getItems(itemListName);
                
                List<InventoryItem> allItems = getItems();

                allItems.forEach(item -> {
                    ItemMeta meta = item.getItemMeta();
                    if (meta == null) return;

                    selectItem(item, items.contains(item.getType()));
                });

                super.open(player);
            }

            @Override
            public void close(HumanEntity player)
            {
                addItemsToList(itemListName, getSelectedItems());
                super.close(player);
            }
        };
        itemPicker.open(player);
    }

    public void addItemsToList(String listName, List<InventoryItem> selectedItems)
    {
        List<Material> materials = new ArrayList<>();
        for (InventoryItem item : selectedItems)
        {
            materials.add(item.getType());
        }
        BingoItemData.removeItemList(listName);
        BingoItemData.saveItems(listName, materials.toArray(new Material[0]));
    }
}
