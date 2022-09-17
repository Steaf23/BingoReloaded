package io.github.steaf23.bingoreloaded.command;

import io.github.steaf23.bingoreloaded.Message;
import io.github.steaf23.bingoreloaded.data.BingoCardsData;
import io.github.steaf23.bingoreloaded.gui.creator.CardEditorUI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

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
                        new Message("command.card.no_name").arg("/card create <card_name>").send(p);
                        break;
                    }

                    editCard(args[1], p);
                    break;

                case "remove":
                    if (commandSender instanceof Player p)
                    {
                        if (args.length < 2)
                        {
                            new Message("command.card.no_name").arg("/card remove <card_name>").send(p);
                            break;
                        }

                        if (BingoCardsData.removeCard(args[1]))
                            new Message("command.card.removed").arg(args[1]).send(p);
                        else
                            new Message("command.card.no_remove").arg(args[1]).send(p);
                        break;
                    }
                    else if (commandSender instanceof ConsoleCommandSender)
                    {
                        if (args.length < 2)
                        {
                            Message.log(ChatColor.RED + "Please provide card name: /card remove <card_name>");
                            break;
                        }

                        if (BingoCardsData.removeCard(args[1]))
                            Message.log("Card '" + args[1] + "' successfully removed!");
                        else
                            Message.log("Card couldn't be found, make sure its spelled correctly!");
                        break;
                    }
                    break;

                default:
                    if (commandSender instanceof Player player)
                        new Message("command.usage").color(ChatColor.RED).arg("/card [create | remove]");
                    else
                        Message.log(ChatColor.RED + "Usage: /card [create | remove]");
                    break;
            }
        }
        return false;
    }

    public static void editCard(String cardName, Player player)
    {
        CardEditorUI cardEditor = new CardEditorUI(cardName, null);
        cardEditor.open(player);
    }
}
