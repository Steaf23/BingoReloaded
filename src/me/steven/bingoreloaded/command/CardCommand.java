package me.steven.bingoreloaded.command;

import me.steven.bingoreloaded.BingoReloaded;
import me.steven.bingoreloaded.gui.CardEditorUI;
import me.steven.bingoreloaded.data.BingoCardsData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class CardCommand implements CommandExecutor
{
    public CardCommand()
    {

    }

    @Override
    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String name, String[] args)
    {
        if (args.length > 0)
        {
            switch (args[0])
            {
                case "create":
                    if (args.length < 2)
                    {
                        BingoReloaded.broadcast(ChatColor.RED + "Please provide card name: /card create <card_name>");
                        break;
                    }
                    if (commandSender instanceof Player p)
                        editCard(args[1], p);
                    break;

                case "edit":
                    if (args.length < 2)
                    {
                        BingoReloaded.broadcast(ChatColor.RED + "Please provide card name: /card edit <card_name>");
                        break;
                    }
                    if (commandSender instanceof Player p)
                        editCard(args[1], p);
                    break;

                case "remove":
                    if (commandSender instanceof Player p)
                    {
                        if (args.length < 2)
                        {
                            BingoReloaded.print(ChatColor.RED + "Please provide card name: /card remove <card_name>", p);
                            break;
                        }

                        if (BingoCardsData.removeCard(args[1]))
                            BingoReloaded.print("Card '" + args[1] + "' successfully removed!", p);
                        else
                            BingoReloaded.print("Card couldn't be found, make sure its spelled correctly!", p);
                        break;
                    }
                    else if (commandSender instanceof ConsoleCommandSender)
                    {
                        if (args.length < 2)
                        {
                            BingoReloaded.print(ChatColor.RED + "Please provide card name: /card remove <card_name>");
                            break;
                        }

                        if (BingoCardsData.removeCard(args[1]))
                            BingoReloaded.print("Card '" + args[1] + "' successfully removed!");
                        else
                            BingoReloaded.print("Card couldn't be found, make sure its spelled correctly!");
                        break;
                    }

                    break;

//                case "items":
//                    if (commandSender instanceof Player p)
//                        openItemPicker(p);
//                    break;

                default:
                    if (commandSender instanceof Player player)
                        BingoReloaded.print(ChatColor.RED + "Usage: /card [create|edit|remove]", player);
                    else
                        BingoReloaded.print(ChatColor.RED + "Usage: /card [create|edit|remove]");
                    break;
            }
        }
        return false;
    }

//    public static void openItemPicker(Player player)
//    {
//        List<Material> glassPanes = new ArrayList<>();
//        for (FlexibleColor flexColor : FlexibleColor.values())
//        {
//            glassPanes.add(flexColor.glassPane);
//        }
//
//        List<InventoryItem> items = new ArrayList<>();
//        for (Material m : Material.values())
//        {
//            if (!m.name().contains("LEGACY_") && m.isItem() && !m.isAir() && !glassPanes.contains(m))
//            {
//                items.add(new InventoryItem(m, "", "Click to make this item appear", "on bingo cards"));
//            }
//        }
//
//        ItemPickerUI itemPicker = new ItemPickerUI(items,"Pick Items", null)
//        {
//            @Override
//            public void onOptionClickedDelegate(InventoryClickEvent event, InventoryItem clickedOption, Player player)
//            {
//                ItemDifficultySelectionUI difficultySelector = new ItemDifficultySelectionUI(clickedOption.getType(), this);
//                difficultySelector.open(player);
//            }
//        };
//
//        itemPicker.open(player);
//    }

    public static void editCard(String cardName, Player player)
    {
        CardEditorUI cardEditor = new CardEditorUI(BingoCardsData.getOrCreateCard(cardName));
        cardEditor.open(player);
    }
}
