package io.github.steaf23.bingoreloaded.command;

import io.github.steaf23.bingoreloaded.*;
import io.github.steaf23.bingoreloaded.data.BingoCardsData;
import io.github.steaf23.bingoreloaded.gui.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.gui.cards.CardSize;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.PlayerKit;
import io.github.steaf23.bingoreloaded.util.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AutoBingoCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] args)
    {
        // AutoBingo should only work for admins or console.
        if (commandSender instanceof Player p && !p.hasPermission("bingo.admin"))
        {
            return false;
        }

        if (args.length == 0)
        {
            return false;
        }
        String worldName = args[0];
        BingoSettings settings = GameWorldManager.get().getGameSettings(worldName);

        // Create the actual game with settings in the world.
        if (settings == null)
        {
            if (args.length > 1 && args[1].equals("create"))
            {
                if (args.length == 2)
                {
                    sendFailed("Usage: /autobingo <world_name> create <max_team_size>", worldName);
                    return false;
                }
                create(worldName, args[2]);
                sendSuccess("Connected Bingo Reloaded to this world!", worldName);
                return true;
            }
            sendFailed("Cannot perform command on a world that has not been created yet!", worldName);
            return false;
        }
        else
        {
            if (args.length > 1 && args[1].equals("destroy"))
            {
                destroy(worldName);
                sendSuccess("Disconnected Bingo Reloaded from this world!", worldName);
                return true;
            }
        }

        // All of these commands can only be executed if settings != null (i.e. if a game exists in the given world)
        if (args.length > 2)
        {
            switch (args[1])
            {
                case "start":
                    if (!start(settings, worldName, args[2], args.length > 3 ? args[3] : ""))
                    {
                        sendFailed("Invalid command, could not start game with gamemode '" + args[2] + "'!", worldName);
                        return false;
                    }
                    sendSuccess("Started bingo!", worldName);
                    return true;

                case "kit":
                    if (!setKit(settings,args[2]))
                    {
                        sendFailed("Could not find Kit with name '" + args[2] + "'!", worldName);
                        return false;
                    }
                    sendSuccess("Kit set to " + settings.kit.displayName, worldName);
                    return true;

                case "effects":
                    // autobingo world effect <effect_name> [true | false]
                    // If argument count is only 1, enable all, none or just the single effect typed.
                    //     Else default enable effect unless the second argument is "false".
                    boolean enable = args.length > 3 && args[3].equals("false") ? false : true;
                    if (!setEffect(settings, args[2], enable))
                    {
                        sendFailed("Invalid effect setting '" + args[2] + "' to '" + enable + "'!", worldName);
                        return false;
                    }
                    sendSuccess("Updated active effects to " + settings.effects, worldName);
                    return true;

                case "card":
                    if (!setCard(settings, args[2], args.length > 3 ? args[3] : "0"))
                    {
                        sendFailed("Invalid card name '" + args[2] + "'!", worldName);
                        return false;
                    }
                    sendSuccess("Playing card set to " + settings.card + " with " +
                            (settings.cardSeed == 0 ? " no seed" : " seed " + settings.cardSeed), worldName);
                    return true;

                case "countdown":
                    if (!setCountdownGameDuration(settings, args[2], args.length > 3 ? args[3] : settings.countdownGameDuration + ""))
                    {
                        sendFailed("Could not set Countdown game duration to " + args[2] + "!", worldName);
                        return false;
                    }
                    sendSuccess("Set duration for countdown mode to " + settings.countdownGameDuration, worldName);
                    return true;

                case "team":
                    // autobingo world team <player_name> <team_name | none>
                    if (args.length == 3)
                    {
                        sendFailed("Invalid number of arguments: " + args.length + "!", worldName);
                        return false;
                    }
                    return setPlayerTeam(worldName, args[2], args[3]);

                default:
                    sendFailed("Invalid command '" + args[2] + "'!", worldName);
                    return false;
            }
        }
        else
        {
            if (args[1].equals("end"))
            {
                if (!end(settings, worldName))
                {
                    sendFailed("Invalid command, can not end the game", worldName);
                }
                else
                {
                    sendSuccess("Game forcefully ended!", worldName);
                    return true;
                }
            }

            sendFailed("Invalid number of arguments: " + args.length + "!", worldName);
            return false;
        }
    }

    public void create(String worldName, String maxTeamMembers)
    {
        int max = toInt(maxTeamMembers, 1);
        GameWorldManager.get().createGame(worldName, Math.max(1, max));
    }

    public void destroy(String worldName)
    {
        GameWorldManager.get().destroyGame(worldName);
    }

    public boolean start(BingoSettings settings, String worldName, String gamemode, String cardSize)
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

        return GameWorldManager.get().startGame(worldName);
    }

    private boolean setKit(BingoSettings settings, String kitName)
    {
        try
        {
            settings.setKit(PlayerKit.fromConfig(kitName));
            return true;
        }
        catch (IllegalArgumentException e)
        {
            return false;
        }

    }

    public boolean setEffect(BingoSettings settings, String effect, boolean enable)
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

    private boolean setCard(BingoSettings settings, String cardName, String cardSeed)
    {
        int seed = toInt(cardSeed, 0);
        if (BingoCardsData.getCardNames().contains(cardName))
        {
            settings.card = cardName;
            settings.cardSeed = seed;
            return true;
        }
        return false;
    }

    public boolean setTeamMaximumMembers(BingoSettings settings, String memberCount)
    {
        int members = toInt(memberCount, 0);
        if (members > 0)
        {
            settings.maxTeamSize = members;
            return true;
        }
        return false;
    }

    public boolean setCountdownGameDuration(BingoSettings settings, String enableCountdown, String duration)
    {
        if (enableCountdown.equals("true"))
        {
            settings.enableCountdown = true;
        }
        else
        {
            settings.enableCountdown = false;
        }

        int gameDuration = toInt(duration, 0);
        if (gameDuration > 0)
        {
            settings.countdownGameDuration = gameDuration;
            return true;
        }
        return false;
    }

    public boolean setPlayerTeam(String worldName, String playerName, String teamName)
    {
        if (!GameWorldManager.get().doesGameWorldExist(worldName))
        {
            sendFailed("Cannot add player to team, world '" + worldName + "' is not a bingo world!", worldName);
            return false;
        }

        BingoGame game = GameWorldManager.get().getGame(worldName);

        Player player = Bukkit.getPlayer(playerName);
        if (player == null)
        {
            sendFailed("Cannot add " + playerName + " to team, player does not exist/ is not online!", worldName);
            return false;
        }

        if (teamName.toLowerCase().equals("none"))
        {
            BingoPlayer bPlayer = game.getTeamManager().getBingoPlayer(player);
            if (bPlayer == null)
            {
                sendFailed(playerName + " did not join any teams!", worldName);
                return false;
            }

            game.getTeamManager().removePlayerFromTeam(bPlayer);
            sendSuccess("Player " + playerName + " removed from all teams", worldName);
        }
        else
        {
            if (!game.getTeamManager().addPlayerToTeam(player, teamName))
            {
                return false;
            }
            sendSuccess("Player " + playerName + " added to team " + teamName + "", worldName);
        }
        return true;
    }

    public boolean end(BingoSettings settings, String worldName)
    {
        return GameWorldManager.get().endGame(worldName);
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

    private void sendFailed(String message, String worldName)
    {
        Message.log(ChatColor.RED + message, worldName);
    }

    private void sendSuccess(String message, String worldName)
    {
        Message.log(ChatColor.GREEN + message, worldName);
    }
}
