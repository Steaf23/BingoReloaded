package io.github.steaf23.bingoreloaded.command;

import io.github.steaf23.bingoreloaded.BingoGame;
import io.github.steaf23.bingoreloaded.BingoGameSettings;
import io.github.steaf23.bingoreloaded.BingoGamemode;
import io.github.steaf23.bingoreloaded.Message;
import io.github.steaf23.bingoreloaded.data.BingoCardsData;
import io.github.steaf23.bingoreloaded.gui.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.gui.cards.CardSize;
import io.github.steaf23.bingoreloaded.player.PlayerKit;
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
        if (!(commandSender instanceof Player p) || p.hasPermission("bingo.admin"))
            return false;

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
                    if (!setEffect(args[1], args.length > 2 && args[2] == "false" ? false : true))
                    {
                        sendFailed("Invalid effect setting '" + args[1] + " to '" + args[2] + "'!", commandSender);
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
        if (effect == "all")
        {
            settings.setEffects(EffectOptionFlags.ALL_ON);
            return true;
        }
        else if (effect == "none")
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
            settings.mode = BingoGamemode.valueOf(gamemode);
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
        if (sender instanceof Player)
        {
            Message.sendDebug(message, (Player)sender);
        }
        else
        {
            Message.log(message);
        }
    }
}
