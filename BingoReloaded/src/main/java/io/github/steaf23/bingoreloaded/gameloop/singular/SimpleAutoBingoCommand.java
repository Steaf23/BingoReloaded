package io.github.steaf23.bingoreloaded.gameloop.singular;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.command.BingoCommand;
import io.github.steaf23.bingoreloaded.command.SubCommand;
import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.BingoSettingsData;
import io.github.steaf23.bingoreloaded.data.PlayerData;
import io.github.steaf23.bingoreloaded.data.helper.SerializablePlayer;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.PregameLobby;
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
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleAutoBingoCommand implements TabExecutor
{
    private final SingularGameManager manager;
    private final SubCommand command;

    private CommandSender currentSender;

    public SimpleAutoBingoCommand(SingularGameManager manager) {
        this.manager = manager;

        this.command = new SubCommand("autobingo");

        command.addSubCommand(new SubCommand("start", args -> {
            return start();
        }));

        command.addSubCommand(new SubCommand("kit", args -> {
            BingoSettingsBuilder settings = manager.getSession().settingsBuilder;
            return setKit(settings, args);
        }
        ).addTabCompletion(args ->
                List.of("hardcore", "normal", "overpowered", "reloaded",
                        "custom_1", "custom_2", "custom_3", "custom_4", "custom_5")
        ).addUsage("<kit_name>"));

        command.addSubCommand(new SubCommand("effects", args -> {
            BingoSettingsBuilder settings = manager.getSession().settingsBuilder;
            return setEffect(settings, args);
        }
        ).addTabCompletion(args -> {
            if (args.length <= 1) {
                List<String> effects = Arrays.stream(EffectOptionFlags.values())
                        .map(v -> v.toString().toLowerCase())
                        .collect(Collectors.toList());
                effects.add("none");
                effects.add("all");
                return effects;
            } else {
                if (!args[0].equals("none") && !args[0].equals("all")) {
                    return List.of("true", "false");
                }
            }
            return List.of();
        }
        ).addUsage("<effect_name> [true | false]"));

        command.addSubCommand(new SubCommand("card", args -> {
            BingoSettingsBuilder settings = manager.getSession().settingsBuilder;
            return setCard(settings, args);
        }
        ).addTabCompletion(args -> {
            return new BingoCardData().getCardNames().stream().collect(Collectors.toList());
        }
        ).addUsage("<card_name>"));

        command.addSubCommand(new SubCommand("countdown", args -> {
            BingoSettingsBuilder settings = manager.getSession().settingsBuilder;
            return setCountdown(settings, args);
        }
        ).addTabCompletion(args -> {
            return List.of("true", "false");
        }
        ).addUsage("<true | false>"));

        command.addSubCommand(new SubCommand("duration", args -> {
            BingoSettingsBuilder settings = manager.getSession().settingsBuilder;
            return setDuration(settings, args);
        }
        ).addUsage("<minutes>"));

        command.addSubCommand(new SubCommand("team", args -> {
            return setPlayerTeam(args);
        }
        ).addUsage("<player_name> <team_name | none>"));

        command.addSubCommand(new SubCommand("teamsize", args -> {
            BingoSettingsBuilder settings = manager.getSession().settingsBuilder;
            return setTeamSize(settings, args);
        }
        ).addUsage("<size>"));

        command.addSubCommand(new SubCommand("gamemode", args -> {
            BingoSettingsBuilder settings = manager.getSession().settingsBuilder;
            return setGamemode(settings, args);
        }
        ).addUsage("<gamemode>"));

        command.addSubCommand(new SubCommand("end", args -> {
            return end();
        }));

        BingoSettingsData settingsData = new BingoSettingsData();

        command.addSubCommand(new SubCommand("preset")
                .addSubCommand(new SubCommand("save", args -> {
                    BingoSettingsBuilder settings = manager.getSession().settingsBuilder;
                    String path = String.join(" ", args);
                    if (path.isBlank()) {
                        sendFailed("Please enter a valid preset name");
                        return false;
                    }
                    settingsData.saveSettings(path, settings.view());
                    return true;
                }
                ).addUsage("autobingo preset save <preset_name>"))
                .addSubCommand(new SubCommand("load", args -> {
                    BingoSettingsBuilder settings = manager.getSession().settingsBuilder;
                    String path = String.join(" ", args);
                    if (path.isBlank()) {
                        sendFailed("Please enter a valid preset name");
                        return false;
                    }
                    settings.fromOther(settingsData.getSettings(path));
                    return true;
                }
                ).addTabCompletion(args -> {
                    return settingsData.getPresetNames().stream().collect(Collectors.toList());
                }
                ).addUsage("autobingo preset load <preset_name>"))
                .addSubCommand(new SubCommand("remove", args -> {
                    String path = String.join(" ", args);
                    if (path.isBlank()) {
                        sendFailed("Please enter a valid preset name");
                        return false;
                    }
                    settingsData.removeSettings(path);
                    return true;
                }
                ).addTabCompletion(args -> {
                    return settingsData.getPresetNames().stream().collect(Collectors.toList());
                }).addUsage("autobingo preset remove <preset_name>"))
        );

        command.addSubCommand(new SubCommand("playerdata", args -> {
            return playerData(manager.getSession().getPlayerData(), args);
        }
        ).addTabCompletion(args -> {
            return List.of("load", "save", "remove");
        }
        ).addUsage("<load | save | remove> player_name"));

        command.addSubCommand(new SubCommand("vote", args -> {
            return voteForPlayer(args);
        }
        ).addTabCompletion(args -> {
            var voteList = manager.getConfig().voteList;
            if (args.length <= 1) {
                return null;
            } else if (args.length == 2) {
                return List.of("kits", "gamemodes", "cards");
            } else if (args.length == 3) {
                return switch (args[1]) {
                    case "kits" -> voteList.kits;
                    case "gamemodes" -> voteList.gamemodes;
                    case "cards" -> voteList.cards;
                    default -> List.of();
                };
            }
            return List.of();
        }
        ).addUsage("<player_name> <vote_category> <vote_for>"));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command autobingoCommand, @NotNull String alias, @NotNull String[] args) {
        // AutoBingo should only work for admins or console.
        currentSender = commandSender;
        if (commandSender instanceof Player p && !p.hasPermission("bingo.admin")) {
            return false;
        }

        if (!command.execute(args)) {
            commandSender.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.RED + "Usage: " + command.usage(args));
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return this.command.tabComplete(args);
    }

    private boolean start() {
        manager.getSession().startGame();
        sendSuccess("The game has started!");
        return true;
    }

    private boolean setKit(BingoSettingsBuilder settings, String[] extraArguments) {
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

    private boolean setEffect(BingoSettingsBuilder settings, String[] extraArguments) {
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

    private boolean setCard(BingoSettingsBuilder settings, String[] extraArguments) {
        if (extraArguments.length == 0) {
            sendFailed("Expected at least 2 arguments!");
            return false;
        }

        String cardName = extraArguments[0];
        int seed = extraArguments.length > 1 ? BingoCommand.toInt(extraArguments[1], 0) : 0;

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

    private boolean setCountdown(BingoSettingsBuilder settings, String[] extraArguments) {
        if (extraArguments.length != 1) {
            sendFailed("Expected 2 arguments!");
            return false;
        }

        boolean enableCountdown = extraArguments[0].equals("true");
        settings.enableCountdown(enableCountdown);
        sendSuccess((enableCountdown ? "Enabled" : "Disabled") + " countdown mode");
        return true;
    }

    private boolean setDuration(BingoSettingsBuilder settings, String[] extraArguments) {
        if (extraArguments.length != 1) {
            sendFailed("Expected 2 arguments!");
            return false;
        }

        int gameDuration = BingoCommand.toInt(extraArguments[0], 0);
        if (gameDuration > 0) {
            settings.countdownGameDuration(gameDuration);
            sendSuccess("Set game duration for countdown mode to " + gameDuration);
            return true;
        }
        sendFailed("Cannot set duration to " + gameDuration);
        return true;
    }

    private boolean setPlayerTeam(String[] extraArguments) {
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

        if (teamName.equalsIgnoreCase("none")) {
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

    private boolean setTeamSize(BingoSettingsBuilder settings, String[] extraArguments) {
        if (extraArguments.length != 1) {
            sendFailed("Expected 2 arguments!");
            return false;
        }

        int teamSize = Math.min(64, Math.max(1, BingoCommand.toInt(extraArguments[0], 1)));

        settings.maxTeamSize(teamSize);
        sendSuccess("Set maximum team size to " + teamSize + " players");
        return true;
    }

    private boolean setGamemode(BingoSettingsBuilder settings, String[] extraArguments) {
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

    private boolean end() {
        manager.getSession().endGame();
        sendSuccess("Game forcefully ended!");
        return true;
    }

    private boolean playerData(PlayerData playerData, String[] extraArguments) {
        if (extraArguments.length != 2)
        {
            sendFailed("Expected 3 arguments!");
            return false;
        }

        String subCommand = extraArguments[0];
        String playerName = extraArguments[1];

        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            sendFailed("Player '" + playerName + "' not found");
            return false;
        }

        switch (subCommand)
        {
            case "save" -> {
                sendSuccess("Saved player data for " + playerName);
                playerData.savePlayer(SerializablePlayer.fromPlayer(BingoReloaded.getInstance(), player), true);
            }
            case "load" -> {
                sendSuccess("Loaded player data for " + playerName);
                playerData.loadPlayer(player);
            }
            case "remove" -> {
                sendSuccess("Removed player data for " + playerName);
                playerData.removePlayer(player.getUniqueId());
            }
            default -> {
                sendFailed("Unrecognized sub command '" + subCommand + "'");
                return false;
            }
        }

        return true;
    }

    public boolean voteForPlayer(String... args) {
        if (args.length != 3) {
            return false;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sendFailed("Player '" + args[0] + "' does not exist!");
            return false;
        }

        String category = args[1];
        String voteFor = args[2];
        if (manager.getSession().phase() instanceof PregameLobby lobby) {
            switch (category) {
                case "kits" -> lobby.voteKit(voteFor, player);
                case "gamemodes" -> lobby.voteGamemode(voteFor, player);
                case "cards" -> lobby.voteCard(voteFor, player);
                default -> {
                    sendFailed("Cannot vote for '" + category + "' category does not exist!");
                    return false;
                }
            }
        }
        return true;
    }

    private void sendFailed(String message) {
        currentSender.sendMessage(ChatColor.RED + message);
    }

    private void sendSuccess(String message) {
        currentSender.sendMessage(ChatColor.GREEN + message);
    }
}
