package io.github.steaf23.bingoreloaded.command;

import io.github.steaf23.bingoreloaded.BingoGame;
import io.github.steaf23.bingoreloaded.BingoGameSettings;
import io.github.steaf23.bingoreloaded.BingoGamemode;
import io.github.steaf23.bingoreloaded.Message;
import io.github.steaf23.bingoreloaded.data.BingoCardsData;
import io.github.steaf23.bingoreloaded.event.BingoCardSlotCompleteEvent;
import io.github.steaf23.bingoreloaded.event.BingoGameEvent;
import io.github.steaf23.bingoreloaded.gui.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.gui.cards.CardSize;
import io.github.steaf23.bingoreloaded.player.PlayerKit;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class AutoBingoCommand implements CommandExecutor
{
    private BingoGameSettings settings;

    public AutoBingoCommand(BingoGameSettings settings)
    {
        this.settings = settings;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] args)
    {
        // AutoBingo should only work for admins or console.
        if (commandSender instanceof Player p && !p.hasPermission("bingo.admin"))
        {
            return true;
        }

        if (args.length > 1)
        {
            switch (args[0])
            {
                case "kit":
                    if (!setKit(args[1]))
                    {
                        sendFailed("Could not find Kit with name '" + args[1] + "'!", commandSender);
                        return false;
                    }
                    return true;

                case "effects":
                    // If argument count is only 1, enable all, none or just the single effect typed.
                    //     Else default enable effect unless the second argument is "false".
                    sendFailed("" + args.length, commandSender);
                    boolean enable = args.length > 2 && args[2].equals("false") ? false : true;
                    if (!setEffect(args[1], enable))
                    {
                        sendFailed("Invalid effect setting '" + args[1] + "' to '" + enable + "'!", commandSender);
                        return false;
                    }
                    return true;

                case "card":
                    if (!setCard(args[1]))
                    {
                        sendFailed("Invalid card name '" + args[1] + "'!", commandSender);
                        return false;
                    }
                    return true;

                case "team_max":
                    if (!setTeamMaximumMembers(args[1]))
                    {
                        sendFailed("Could not set maximum team members to " + args[1] + "!", commandSender);
                        return false;
                    }
                    return true;

                case "start":
                    if (!start(args[1], args.length > 2 ? args[2] : ""))
                    {
                        sendFailed("Invalid command, could not start game with gamemode '" + args[1] + "'!", commandSender);
                        return false;
                    }
                    return true;

                default:
                    sendFailed("Invalid command '" + args[0] + "'!", commandSender);
                    return false;
            }
        }
        else
        {
            if (!end())
            {
                sendFailed("Invalid command, can not end the game", commandSender);
            }
            else
            {
                return true;
            }

            sendFailed("Invalid number of arguments: " + args.length + "!", commandSender);
            return false;
        }
    }

    private boolean setKit(String kitName)
    {
        try
        {
            settings.setKit(PlayerKit.valueOf(kitName.toUpperCase()));
            return true;
        }
        catch (IllegalArgumentException e)
        {
            return false;
        }

    }

    public boolean setEffect(String effect, boolean enable)
    {
        if (effect.equals("all"))
        {
            settings.setEffects(EffectOptionFlags.ALL_ON);
            return true;
        }
        else if (effect.equals("none"))
        {
            settings.setEffects(EffectOptionFlags.ALL_OFF);
            return true;
        }

        try
        {
            if (enable)
            {
                settings.effects.add(EffectOptionFlags.valueOf(effect.toUpperCase()));
            }
            else
            {
                settings.effects.remove(EffectOptionFlags.valueOf(effect.toUpperCase()));
            }
            return true;
        }
        catch (IllegalArgumentException e)
        {
            return false;
        }
    }

    private boolean setCard(String cardName)
    {
        if (BingoCardsData.getCardNames().contains(cardName))
        {
            settings.card = cardName;
            return true;
        }
        return false;
    }

    public boolean setTeamMaximumMembers(String memberCount)
    {
        int members = toInt(memberCount, 0);
        if (members > 0)
        {
            settings.maxTeamSize = members;
            return true;
        }
        return false;
    }

    public boolean start(String gamemode, String cardSize)
    {
        try
        {
            settings.mode = BingoGamemode.fromDataString(gamemode);
            switch (cardSize)
            {
                case "3":
                    settings.cardSize = CardSize.X3;
                    break;
                default:
                    settings.cardSize = CardSize.X5;
                    break;
            }
        }
        catch (IllegalArgumentException e)
        {
            return false;
        }

        var startEvent = new BingoGameEvent("start_game");
        Bukkit.getPluginManager().callEvent(startEvent);
        return true;
    }

    public boolean end()
    {
        var endEvent = new BingoGameEvent("end_game");
        Bukkit.getPluginManager().callEvent(endEvent);
        return true;
    }

    /**
     * @param in
     * @param defaultValue
     * @return Integer the string represents or defaultValue if a conversion failed.
     */
    private int toInt(String in, int defaultValue)
    {
        try
        {
            return Integer.parseInt(in);
        }
        catch (NumberFormatException e)
        {
            return defaultValue;
        }
    }

    private void sendFailed(String message, CommandSender sender)
    {
        TextComponent text = new TextComponent(message);
        text.setColor(ChatColor.RED);
        if (sender instanceof Player)
        {
            Message.sendDebug(text, (Player)sender);
        }
        else
        {
            Message.log(text);
        }
    }
}
