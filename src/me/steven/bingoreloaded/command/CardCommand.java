package me.steven.bingoreloaded.command;

import me.steven.bingoreloaded.BingoReloaded;
import me.steven.bingoreloaded.data.MessageSender;
import me.steven.bingoreloaded.gui.CardEditorUI;
import me.steven.bingoreloaded.data.BingoCardsData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class CardCommand implements CommandExecutor
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
                        MessageSender.send("command.card.no_name", p, List.of("/card create <card_name>"), ChatColor.RED);
                        break;
                    }

                    editCard(args[1], p);
                    break;

                case "edit":
                    if (!(commandSender instanceof Player p))
                        break;
                    if (args.length < 2)
                    {
                        MessageSender.send("command.card.no_name", p, List.of("/card edit <card_name>"), ChatColor.RED);
                        break;
                    }

                    editCard(args[1], p);
                    break;

                case "remove":
                    if (commandSender instanceof Player p)
                    {
                        if (args.length < 2)
                        {
                            MessageSender.send("command.card.no_name", p, List.of("/card remove <card_name>"), ChatColor.RED);
                            break;
                        }

                        if (BingoCardsData.removeCard(args[1]))
                            MessageSender.send("command.card.removed", p, List.of(args[1]), ChatColor.RED);
                        else
                            MessageSender.send("command.send.no_remove", p, List.of(args[1]), ChatColor.RED);
                        break;
                    }
                    else if (commandSender instanceof ConsoleCommandSender)
                    {
                        if (args.length < 2)
                        {
                            MessageSender.log(ChatColor.RED + "Please provide card name: /card remove <card_name>");
                            break;
                        }

                        if (BingoCardsData.removeCard(args[1]))
                            MessageSender.log("Card '" + args[1] + "' successfully removed!");
                        else
                            MessageSender.log("Card couldn't be found, make sure its spelled correctly!");
                        break;
                    }
                    break;

                default:
                    if (commandSender instanceof Player player)
                        MessageSender.send("command.usage", player, List.of("/card [create|edit|remove]"), ChatColor.RED);
                    else
                        MessageSender.log(ChatColor.RED + "Usage: /card [create|edit|remove]");
                    break;
            }
        }
        return false;
    }

    public static void editCard(String cardName, Player player)
    {
        CardEditorUI cardEditor = new CardEditorUI(BingoCardsData.getOrCreateCard(cardName));
        cardEditor.open(player);
    }
}
