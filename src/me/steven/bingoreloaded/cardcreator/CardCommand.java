package me.steven.bingoreloaded.cardcreator;

import me.steven.bingoreloaded.BingoReloaded;
import me.steven.bingoreloaded.cardcreator.GUI.ItemListEditorUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class CardCommand implements CommandExecutor
{
    public CardCommand()
    {

    }

    @Override
    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, String[] args)
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
                    BingoReloaded.broadcast("Creating '" + args[1] + "'");
                    CardCreator.createCard(args[1]);
                    break;

                case "edit":
                    if (args.length < 2)
                    {
                        BingoReloaded.broadcast(ChatColor.RED + "Please provide card name: /card edit <card_name>");
                        break;
                    }

                    BingoReloaded.broadcast("Editing '" + args[1] + "'");
                    CardCreator.editCard(args[1]);
                    break;

                case "remove":
                    if (args.length < 2)
                    {
                        BingoReloaded.broadcast(ChatColor.RED + "Please provide card name: /card remove <card_name>");
                        break;
                    }

                    BingoReloaded.broadcast("Removing '" + args[1] + "'");
                    CardCreator.removeCard(args[1]);
                    break;

                case "items":
                    ItemListEditorUI itemPicker = new ItemListEditorUI(null);
                    if (commandSender instanceof Player p)
                        itemPicker.open(p);
                    break;

                default:
                    if (commandSender instanceof Player player)
                        BingoReloaded.print(ChatColor.RED + "Usage: /card [create|edit|remove|items]", player);
                    else
                        BingoReloaded.print(ChatColor.RED + "Usage: /card [create|edit|remove|items]");
                    break;
            }
        }
        return false;
    }
}
