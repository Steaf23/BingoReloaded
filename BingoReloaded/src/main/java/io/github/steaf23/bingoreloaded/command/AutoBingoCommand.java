package io.github.steaf23.bingoreloaded.command;

import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.BingoSettingsData;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.GameManager;
import io.github.steaf23.bingoreloaded.gui.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.settings.BingoSettingsBuilder;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AutoBingoCommand implements TabExecutor
{
    private final GameManager manager;
    private final SubCommand command;

    private CommandSender currentSender;

    public AutoBingoCommand(GameManager manager) {
        this.manager = manager;

        command = new DeferredCommand("autobingo", "world")
                .addTabCompletion(args -> manager.getSessionNames().stream().toList());


        command.addSubCommand(new SubCommand("create", args -> create(args[0])));


        command.addSubCommand(new SubCommand("destroy", args -> destroy(args[0])));


        command.addSubCommand(new SubCommand("start", args -> start(args[0])));


        command.addSubCommand(new SubCommand("kit", args -> {
            var settings = getSettingsBuilder(args[0]);
            if (settings == null)
            {
                sendFailed("Invalid world/ session name: " + args[0], args[0]);
                return false;
            }
            return setKit(settings, args[0], Arrays.copyOfRange(args, 1, args.length));
        }
        ).addTabCompletion(args ->
                List.of("hardcore", "normal", "overpowered", "reloaded",
                        "custom_1", "custom_2", "custom_3", "custom_4", "custom_5")
        ).addUsage("<kit_name>"));


        command.addSubCommand(new SubCommand("effects", args -> {
            var settings = getSettingsBuilder(args[0]);
            if (settings == null)
            {
                sendFailed("Invalid world/ session name: " + args[0], args[0]);
                return false;
            }
            return setEffect(settings, args[0], Arrays.copyOfRange(args, 1, args.length));
        }).addUsage("<effect_name> [true | false]")
                .addTabCompletion(args -> {
                    if (args.length <= 2) {
                        List<String> effects = Arrays.stream(EffectOptionFlags.values())
                                .map(v -> v.toString().toLowerCase())
                                .collect(Collectors.toList());
                        effects.add("none");
                        effects.add("all");
                        return effects;
                    } else if (args.length == 3) {
                        if (!args[0].equals("none") && !args[0].equals("all")) {
                            return List.of("true", "false");
                        }
                    }
                    return List.of();
                }));


        command.addSubCommand(new SubCommand("card", args -> {
            var settings = getSettingsBuilder(args[0]);
            if (settings == null)
            {
                sendFailed("Invalid world/ session name: " + args[0], args[0]);
                return false;
            }
            return setCard(settings, args[0], Arrays.copyOfRange(args, 1, args.length));
        }).addUsage("<card_name>"));


        command.addSubCommand(new SubCommand("countdown", args -> {
            var settings = getSettingsBuilder(args[0]);
            if (settings == null)
            {
                sendFailed("Invalid world/ session name: " + args[0], args[0]);
                return false;
            }
            return setCountdown(settings, args[0], Arrays.copyOfRange(args, 1, args.length));
        }).addUsage("<countdown_seconds>"));


        command.addSubCommand(new SubCommand("duration", args -> {
            var settings = getSettingsBuilder(args[0]);
            if (settings == null)
            {
                sendFailed("Invalid world/ session name: " + args[0], args[0]);
                return false;
            }
            return setDuration(settings, args[0], Arrays.copyOfRange(args, 1, args.length));
        }).addUsage("<duration_minutes>"));


        command.addSubCommand(new SubCommand("team", args -> {
            var settings = getSettingsBuilder(args[0]);
            if (settings == null)
            {
                sendFailed("Invalid world/ session name: " + args[0], args[0]);
                return false;
            }
            return setPlayerTeam(args[0], Arrays.copyOfRange(args, 1, args.length));
        }).addUsage("<player_name> <team_name>")
                        .addTabCompletion(args -> args.length == 2 || args.length == 3 ? List.of("") : List.of()));


        command.addSubCommand(new SubCommand("teamsize", args -> {
            var settings = getSettingsBuilder(args[0]);
            if (settings == null)
            {
                sendFailed("Invalid world/ session name: " + args[0], args[0]);
                return false;
            }
            return setTeamSize(settings, args[0], Arrays.copyOfRange(args, 1, args.length));
        }).addUsage("<size>"));


        command.addSubCommand(new SubCommand("gamemode", args -> {
            var settings = getSettingsBuilder(args[0]);
            if (settings == null)
            {
                sendFailed("Invalid world/ session name: " + args[0], args[0]);
                return false;
            }
            return setGamemode(settings, args[0], Arrays.copyOfRange(args, 1, args.length));
        }).addUsage("<regular | lockout | complete | hotswap> [3 | 5]")
                .addTabCompletion(args -> switch (args.length) {
                    case 2 -> List.of("regular", "lockout", "complete", "hotswap");
                    case 3 -> List.of("3", "5");
                    default -> List.of();
                }));


        command.addSubCommand(new SubCommand("end", args -> end(args[0])));


        command.addSubCommand(new SubCommand("preset", args -> {
            var settings = getSettingsBuilder(args[0]);
            if (settings == null)
            {
                sendFailed("Invalid world/ session name: " + args[0], args[0]);
                return false;
            }
            return preset(settings, args[0], Arrays.copyOfRange(args, 1, args.length));
        }).addUsage("<save | load | remove | default> <preset_name>")
                .addTabCompletion(args -> {
                    BingoSettingsData settingsData = new BingoSettingsData();
                    return switch (args.length) {
                        case 2 -> List.of("save", "load", "remove", "default");
                        case 3 -> new ArrayList<>(settingsData.getPresetNames());
                        default -> List.of();
                    };
                }));
    }

    @Override
    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command autobingoCommand, @NonNull String alias, @NonNull String[] args) {
        // AutoBingo should only work for admins or console.
        if (commandSender instanceof Player p && !p.hasPermission("bingo.admin")) {
            return false;
        }

        currentSender = commandSender;

        if (!command.execute(args)) {
            commandSender.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.RED + "Usage: " + command.usage(args));
        }
        return true;
    }

    private BingoSettingsBuilder getSettingsBuilder(String sessionName) {
        BingoSession session = manager.getSession(sessionName);
        return session == null ? null : session.settingsBuilder;
    }

    public boolean create(String worldName) {
        if (manager.createSession(worldName)) {
            sendSuccess("Connected Bingo Reloaded to this world!", worldName);
            return true;
        }

        sendFailed("Could not create session", worldName);
        return false;
    }

    public boolean destroy(String worldName) {
        if (manager.destroySession(worldName)) {
            sendSuccess("Disconnected Bingo Reloaded from this world!", worldName);
            return true;
        }

        sendFailed("Could not destroy session", worldName);
        return false;
    }

    public boolean start(String worldName) {
        if (manager.startGame(worldName)) {
            sendSuccess("The game has started!", worldName);
            return true;
        }

        sendFailed("Could not start game", worldName);
        return false;
    }

    public boolean setKit(BingoSettingsBuilder settings, String worldName, String[] extraArguments) {
        if (extraArguments.length != 1) {
            sendFailed("Expected 3 arguments!", worldName);
            return false;
        }

        PlayerKit kit = PlayerKit.fromConfig(extraArguments[0]);

        if (PlayerKit.customKits().contains(kit) && PlayerKit.getCustomKit(kit) == null)
        {
            // Invalid custom kit selected, not possible!
            sendFailed("Cannot set kit to " + kit.getDisplayName() + ". This custom kit is not defined. To create custom kits first, use /bingo kit.", worldName);
            return false;
        }
        settings.kit(kit);
        sendSuccess("Kit set to " + kit.getDisplayName(), worldName);
        return true;
    }

    public boolean setEffect(BingoSettingsBuilder settings, String worldName, String[] extraArguments) {
        // autobingo world effect <effect_name> [true | false]
        // If argument count is only 1, enable all, none or just the single effect typed.
        //     Else default enable effect unless the second argument is "false".

        if (extraArguments.length == 0) {
            sendFailed("Expected at least 3 arguments!", worldName);
            return false;
        }
        String effect = extraArguments[0];
        boolean enable = extraArguments.length == 1 || !extraArguments[1].equals("false");

        if (effect.equals("all")) {
            settings.effects(EffectOptionFlags.ALL_ON);
            sendSuccess("Updated active effects to " + EffectOptionFlags.ALL_ON, worldName);
            return true;
        } else if (effect.equals("none")) {
            settings.effects(EffectOptionFlags.ALL_OFF);
            sendSuccess("Updated active effects to " + EffectOptionFlags.ALL_OFF, worldName);
            return true;
        }

        try {
            settings.toggleEffect(EffectOptionFlags.valueOf(effect.toUpperCase()), enable);
            sendSuccess("Updated active effects to " + settings.view().effects(), worldName);
            return true;
        } catch (IllegalArgumentException e) {
            sendFailed("Invalid effect: " + effect, worldName);
            return false;
        }
    }

    public boolean setCard(BingoSettingsBuilder settings, String worldName, String[] extraArguments) {
        if (extraArguments.length == 0) {
            sendFailed("Expected at least 3 arguments!", worldName);
            return false;
        }

        String cardName = extraArguments[0];
        int seed = extraArguments.length > 1 ? BingoCommand.toInt(extraArguments[1], 0) : 0;

        BingoCardData cardsData = new BingoCardData();
        if (cardsData.getCardNames().contains(cardName)) {
            settings.card(cardName).cardSeed(seed);
            sendSuccess("Playing card set to " + cardName + " with" +
                    (seed == 0 ? " no seed" : " seed " + seed), worldName);
            return true;
        }
        sendFailed("No card named '" + cardName + "' was found!", worldName);
        return false;
    }

    public boolean setCountdown(BingoSettingsBuilder settings, String worldName, String[] extraArguments) {
        if (extraArguments.length != 1) {
            sendFailed("Expected 3 arguments!", worldName);
            return false;
        }

        boolean enableCountdown = extraArguments[0].equals("true");
        settings.enableCountdown(enableCountdown);
        sendSuccess((enableCountdown ? "Enabled" : "Disabled") + " countdown mode", worldName);
        return true;
    }

    public boolean setDuration(BingoSettingsBuilder settings, String worldName, String[] extraArguments) {
        if (extraArguments.length != 1) {
            sendFailed("Expected 3 arguments!", worldName);
            return false;
        }

        int gameDuration = BingoCommand.toInt(extraArguments[0], 0);
        if (gameDuration > 0) {
            settings.countdownGameDuration(gameDuration);
            sendSuccess("Set game duration for countdown mode to " + gameDuration, worldName);
            return true;
        }

        sendFailed("Cannot set duration to " + gameDuration, worldName);
        return false;
    }

    public boolean setPlayerTeam(String sessionName, String[] extraArguments) {
        if (extraArguments.length != 2) {
            sendFailed("Expected 4 arguments!", sessionName);
            return false;
        }

        if (manager.getSession(sessionName) == null) {
            sendFailed("Cannot add player to team, world '" + sessionName + "' is not a bingo world!", sessionName);
            return false;
        }

        BingoSession session = manager.getSession(sessionName);
        String playerName = extraArguments[0];
        String teamName = extraArguments[1];

        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            sendFailed("Cannot add " + playerName + " to team, player does not exist/ is not online!", sessionName);
            return false;
        }

        if (teamName.equalsIgnoreCase("none")) {
            BingoParticipant participant = session.teamManager.getPlayerAsParticipant(player);
            if (participant == null) {
                sendFailed(playerName + " did not join any teams!", sessionName);
                return false;
            }

            session.teamManager.removeMemberFromTeam(participant);
            sendSuccess("Player " + playerName + " removed from all teams", sessionName);
            return true;
        }
        BingoParticipant participant = session.teamManager.getPlayerAsParticipant(player);
        if (participant == null)
        {
            participant = new BingoPlayer(player, session);
        }
        if (!session.teamManager.addMemberToTeam(participant, teamName)) {
            sendFailed("Player " + player + " could not be added to team " + teamName, sessionName);
            return false;
        }
        sendSuccess("Player " + playerName + " added to team " + teamName + "", sessionName);
        return true;
    }

    public boolean setTeamSize(BingoSettingsBuilder settings, String worldName, String[] extraArguments) {
        if (extraArguments.length != 1) {
            sendFailed("Expected 3 arguments!", worldName);
            return false;
        }

        int teamSize = Math.min(64, Math.max(1, BingoCommand.toInt(extraArguments[0], 1)));

        settings.maxTeamSize(teamSize);
        sendSuccess("Set maximum team size to " + teamSize + " players", worldName);
        return true;
    }

    public boolean setGamemode(BingoSettingsBuilder settings, String worldName, String[] extraArguments) {
        if (extraArguments.length == 0) {
            sendFailed("Expected at least 3 arguments!", worldName);
            return false;
        }

        BingoGamemode mode = BingoGamemode.fromDataString(extraArguments[0], true);
        if (mode == null)
        {
            sendFailed("Unknown gamemode '" + extraArguments[0] + "'", worldName);
            return false;
        }
        settings.mode(mode);

        if (extraArguments.length == 2 && extraArguments[1].equals("3")) {
            settings.cardSize(CardSize.X3);
        } else {
            settings.cardSize(CardSize.X5);
        }

        BingoSettings view = settings.view();
        sendSuccess("Set gamemode to " + view.mode().displayName + " " + view.size().size + "x" + view.size().size, worldName);
        return true;
    }

    public boolean end(String worldName) {
        if (manager.endGame(worldName)) {
            sendSuccess("Game forcefully ended!", worldName);
            return true;
        } else {
            sendFailed("Could not end the game", worldName);
            return false;
        }
    }

    public boolean preset(BingoSettingsBuilder settingsBuilder, String sessionName, String[] extraArguments) {
        if (extraArguments.length != 2) {
            sendFailed("Expected 4 arguments!", sessionName);
            return false;
        }

        BingoSettingsData settingsData = new BingoSettingsData();

        String path = extraArguments[1];
        if (path.isBlank()) {
            sendFailed("Please enter a valid preset name", sessionName);
            return false;
        }

        switch (extraArguments[0]) {
            case "save" -> {
                settingsData.saveSettings(path, settingsBuilder.view());
                sendSuccess("Saved settings to '" + path + "'.", sessionName);
            }
            case "load" -> {
                settingsBuilder.fromOther(settingsData.getSettings(path));
                sendSuccess("Loaded settings from '" + path + "'.", sessionName);
            }
            case "remove" -> {
                settingsData.removeSettings(path);
                sendSuccess("Removed settings preset '" + path + "'.", sessionName);
            }
            case "default" -> {
                settingsData.setDefaultSettings(path);
                sendSuccess("Set '" + path + "' as default settings for new worlds.", sessionName);
            }
        }

        return true;
    }

    private void sendFailed(String message, String sessionName) {
        currentSender.sendMessage("(" + sessionName + ") " + ChatColor.RED + message);
    }

    private void sendSuccess(String message, String sessionName) {
        currentSender.sendMessage("(" + sessionName + ") " + ChatColor.GREEN + message);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return this.command.tabComplete(args);
    }
}
