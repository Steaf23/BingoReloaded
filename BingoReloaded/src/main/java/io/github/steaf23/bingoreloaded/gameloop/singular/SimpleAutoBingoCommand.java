package io.github.steaf23.bingoreloaded.gameloop.singular;

import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.command.AutoBingoCommand;
import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.BingoSettingsData;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gui.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.settings.BingoSettingsBuilder;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.bingoreloaded.util.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class SimpleAutoBingoCommand implements CommandExecutor
{
    private final SingularGameManager manager;

    public SimpleAutoBingoCommand(SingularGameManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command autobingoCommand, @NotNull String alias, @NotNull String[] args) {
        // AutoBingo should only work for admins or console.
        if (commandSender instanceof Player p && !p.hasPermission("bingo.admin")) {
            return false;
        }

        if (args.length < 1) {
            return false;
        }
        String command = args[0];
        String[] extraArguments = args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[]{};
        BingoSettingsBuilder settings = manager.getSession().settingsBuilder;

        boolean success = switch (command) {
            case "start" -> start();
            case "kit" -> setKit(settings, extraArguments);
            case "effects" -> setEffect(settings, extraArguments);
            case "card" -> setCard(settings, extraArguments);
            case "countdown" -> setCountdown(settings, extraArguments);
            case "duration" -> setDuration(settings, extraArguments);
            case "team" -> setPlayerTeam(extraArguments);
            case "teamsize" -> setTeamSize(settings, extraArguments);
            case "gamemode" -> setGamemode(settings, extraArguments);
            case "end" -> end();
            case "preset" -> preset(settings, extraArguments);
            default -> {
                Message.log(ChatColor.RED + "Invalid command: '" + command + "' not recognized");
                yield false;
            }
        };

        return success;
    }

    public boolean start() {
        manager.getSession().startGame();
        sendSuccess("The game has started!");
        return true;
    }

    public boolean setKit(BingoSettingsBuilder settings, String[] extraArguments) {
        if (extraArguments.length != 1) {
            sendFailed("Expected 2 arguments!");
            return false;
        }

        BingoSession session = manager.getSession();
        PlayerKit kit = PlayerKit.fromConfig(extraArguments[0]);
        settings.kit(kit, session);
        sendSuccess("Kit set to " + kit.displayName);
        return true;
    }

    public boolean setEffect(BingoSettingsBuilder settings, String[] extraArguments) {
        // autobingo effect <effect_name> [true | false]
        // If argument count is only 1, enable all, none or just the single effect typed.
        //     Else default enable effect unless the second argument is "false".

        BingoSession session = manager.getSession();
        if (extraArguments.length == 0) {
            sendFailed("Expected at least 2 arguments!");
            return false;
        }
        String effect = extraArguments[0];
        boolean enable = extraArguments.length > 1 && extraArguments[1].equals("false") ? false : true;

        if (effect.equals("all")) {
            settings.effects(EffectOptionFlags.ALL_ON, session);
            sendSuccess("Updated active effects to " + EffectOptionFlags.ALL_ON);
            return true;
        } else if (effect.equals("none")) {
            settings.effects(EffectOptionFlags.ALL_OFF, session);
            sendSuccess("Updated active effects to " + EffectOptionFlags.ALL_OFF);
            return true;
        }

        try {
            settings.toggleEffect(EffectOptionFlags.valueOf(effect.toUpperCase()), enable);
            sendSuccess("Updated active effects to " + settings.view().effects());
            return true;
        } catch (IllegalArgumentException e) {
            sendFailed("Invalid effect: " + effect);
            return false;
        }
    }

    public boolean setCard(BingoSettingsBuilder settings, String[] extraArguments) {
        if (extraArguments.length == 0) {
            sendFailed("Expected at least 2 arguments!");
            return false;
        }

        String cardName = extraArguments[0];
        int seed = extraArguments.length > 1 ? AutoBingoCommand.toInt(extraArguments[1], 0) : 0;

        BingoCardData cardsData = new BingoCardData();
        if (cardsData.getCardNames().contains(cardName)) {
            settings.card(cardName).cardSeed(seed);
            sendSuccess("Playing card set to " + cardName + " with" +
                    (seed == 0 ? " no seed" : " seed " + seed));
            return true;
        }
        sendFailed("No card named '" + cardName + "' was found!");
        return false;
    }

    public boolean setCountdown(BingoSettingsBuilder settings, String[] extraArguments) {
        if (extraArguments.length != 1) {
            sendFailed("Expected 2 arguments!");
            return false;
        }

        boolean enableCountdown = extraArguments[0].equals("true");
        settings.enableCountdown(enableCountdown);
        sendSuccess((enableCountdown ? "Enabled" : "Disabled") + " countdown mode");
        return true;
    }

    public boolean setDuration(BingoSettingsBuilder settings, String[] extraArguments) {
        if (extraArguments.length != 1) {
            sendFailed("Expected 2 arguments!");
            return false;
        }

        int gameDuration = AutoBingoCommand.toInt(extraArguments[0], 0);
        if (gameDuration > 0) {
            settings.countdownGameDuration(gameDuration);
            sendSuccess("Set game duration for countdown mode to " + gameDuration);
            return true;
        }
        sendFailed("Cannot set duration to " + gameDuration);
        return true;
    }

    public boolean setPlayerTeam(String[] extraArguments) {
        BingoSession session = manager.getSession();
        if (extraArguments.length != 2) {
            sendFailed("Expected 3 arguments!");
            return false;
        }

        String playerName = extraArguments[0];
        String teamName = extraArguments[1];

        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            sendFailed("Cannot add " + playerName + " to team, player does not exist/ is not online!");
            return false;
        }

        if (teamName.toLowerCase().equals("none")) {
            BingoParticipant participant = session.teamManager.getBingoParticipant(player);
            if (participant == null) {
                sendFailed(playerName + " did not join any teams!");
                return false;
            }

            session.teamManager.removeMemberFromTeam(participant);
            sendSuccess("Player " + playerName + " removed from all teams");
            return true;
        }

        if (!session.teamManager.addPlayerToTeam(player, teamName)) {
            sendFailed("Player " + player + " could not be added to team " + teamName);
            return false;
        }
        sendSuccess("Player " + playerName + " added to team " + teamName + "");
        return true;
    }

    public boolean setTeamSize(BingoSettingsBuilder settings, String[] extraArguments) {
        if (extraArguments.length != 1) {
            sendFailed("Expected 2 arguments!");
            return false;
        }

        int teamSize = Math.min(64, Math.max(1, AutoBingoCommand.toInt(extraArguments[0], 1)));

        settings.maxTeamSize(teamSize);
        sendSuccess("Set maximum team size to " + teamSize + " players");
        return true;
    }

    public boolean setGamemode(BingoSettingsBuilder settings, String[] extraArguments) {
        if (extraArguments.length < 2) {
            sendFailed("Expected at least 2 arguments!");
            return false;
        }

        try {
            settings.mode(BingoGamemode.fromDataString(extraArguments[0]));
            if (extraArguments[1].equals("3")) {
                settings.cardSize(CardSize.X3);
            } else {
                settings.cardSize(CardSize.X5);
            }
        } catch (IllegalArgumentException e) {
            sendFailed("Cannot set gamemode to '" + extraArguments[0] + "', unknown gamemode!");
            return false;
        }
        BingoSettings view = settings.view();
        sendSuccess("Set gamemode to " + view.mode().name + " " + view.size().size + "x" + view.size().size);
        return true;
    }

    public boolean end() {
        manager.getSession().endGame();
        sendSuccess("Game forcefully ended!");
        return true;
    }

    public boolean preset(BingoSettingsBuilder settingsBuilder, String[] extraArguments) {
        if (extraArguments.length != 2) {
            sendFailed("Expected 3 arguments!");
            return false;
        }

        BingoSettingsData settingsData = new BingoSettingsData();

        String path = extraArguments[1];
        if (path.isBlank()) {
            sendFailed("Please enter a valid preset name");
            return false;
        }
        if (extraArguments[0].equals("save")) {
            settingsData.saveSettings(path, settingsBuilder.view());
        } else if (extraArguments[0].equals("load")) {
            settingsBuilder.fromOther(settingsData.getSettings(path));
        } else if (extraArguments[0].equals("remove")) {
            settingsData.removeSettings(path);
        } else {
            sendFailed("command " + extraArguments[0] + " not recognized!");
        }

        return true;
    }

    private void sendFailed(String message) {
        Message.log(ChatColor.RED + message);
    }

    private void sendSuccess(String message) {
        Message.log(ChatColor.GREEN + message);
    }
}
