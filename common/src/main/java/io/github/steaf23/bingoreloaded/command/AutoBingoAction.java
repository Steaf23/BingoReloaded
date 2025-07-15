package io.github.steaf23.bingoreloaded.command;

import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.lib.action.DeferredAction;
import io.github.steaf23.bingoreloaded.lib.action.ActionTree;
import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.BingoSettingsData;
import io.github.steaf23.bingoreloaded.data.PlayerSerializationData;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.data.helper.SerializablePlayer;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.GameManager;
import io.github.steaf23.bingoreloaded.gameloop.phase.PregameLobby;
import io.github.steaf23.bingoreloaded.lib.api.ServerSoftware;
import io.github.steaf23.bingoreloaded.lib.api.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.settings.BingoSettingsBuilder;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AutoBingoAction extends DeferredAction {

	private final ServerSoftware platform;
	private final GameManager manager;

	public AutoBingoAction(ServerSoftware platform, GameManager manager) {
		super("autobingo", "world", List.of("bingo.admin"));
		this.platform = platform;
		this.manager = manager;

		addTabCompletion(args -> manager.getSessionNames().stream().toList());

		this.addSubAction(new ActionTree("create", args -> create(args[0])));


		this.addSubAction(new ActionTree("destroy", args -> destroy(args[0])));


		this.addSubAction(new ActionTree("start", args -> start(args[0])));


		this.addSubAction(new ActionTree("kit", args -> {
			var settings = getSettingsBuilder(args[0]);
			if (settings == null) {
				sendFailed("Invalid world/ session name: " + args[0], args[0]);
				return false;
			}
			return setKit(settings, args[0], Arrays.copyOfRange(args, 1, args.length));
		}
		).addTabCompletion(args ->
				List.of("hardcore", "normal", "overpowered", "reloaded",
						"custom_1", "custom_2", "custom_3", "custom_4", "custom_5")
		).addUsage("<kit_name>"));


		this.addSubAction(new ActionTree("effects", args -> {
			var settings = getSettingsBuilder(args[0]);
			if (settings == null) {
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


		this.addSubAction(new ActionTree("card", args -> {
			var settings = getSettingsBuilder(args[0]);
			if (settings == null) {
				sendFailed("Invalid world/ session name: " + args[0], args[0]);
				return false;
			}
			return setCard(settings, args[0], Arrays.copyOfRange(args, 1, args.length));
		}).addUsage("<card_name>"));


		this.addSubAction(new ActionTree("countdown", args -> {
			var settings = getSettingsBuilder(args[0]);
			if (settings == null) {
				sendFailed("Invalid world/ session name: " + args[0], args[0]);
				return false;
			}
			return setCountdown(settings, args[0], Arrays.copyOfRange(args, 1, args.length));
		}).addUsage("<type>")
				.addTabCompletion(args -> args.length == 2 ? List.of("disabled", "duration", "time_limit") : List.of()));


		this.addSubAction(new ActionTree("duration", args -> {
			var settings = getSettingsBuilder(args[0]);
			if (settings == null) {
				sendFailed("Invalid world/ session name: " + args[0], args[0]);
				return false;
			}
			return setDuration(settings, args[0], Arrays.copyOfRange(args, 1, args.length));
		}).addUsage("<duration_minutes>"));


		this.addSubAction(new ActionTree("team", args -> {
			var settings = getSettingsBuilder(args[0]);
			if (settings == null) {
				sendFailed("Invalid world/ session name: " + args[0], args[0]);
				return false;
			}
			return setPlayerTeam(args[0], Arrays.copyOfRange(args, 1, args.length));
		}).addUsage("<player_name> <team_name>")
				.addTabCompletion(args -> args.length == 2 || args.length == 3 ? List.of("") : List.of()));


		this.addSubAction(new ActionTree("teamsize", args -> {
			var settings = getSettingsBuilder(args[0]);
			if (settings == null) {
				sendFailed("Invalid world/ session name: " + args[0], args[0]);
				return false;
			}
			return setTeamSize(settings, args[0], Arrays.copyOfRange(args, 1, args.length));
		}).addUsage("<size>"));


		this.addSubAction(new ActionTree("gamemode", args -> {
			var settings = getSettingsBuilder(args[0]);
			if (settings == null) {
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


		this.addSubAction(new ActionTree("hotswap_goal", args -> {
			var settings = getSettingsBuilder(args[0]);
			if (settings == null) {
				sendFailed("Invalid world/ session name: " + args[0], args[0]);
				return false;
			}
			return setHotswapGoal(settings, args[0], Arrays.copyOfRange(args, 1, args.length));
		})).addUsage("<win_goal>");


		this.addSubAction(new ActionTree("hotswap_expire", args -> {
			var settings = getSettingsBuilder(args[0]);
			if (settings == null) {
				sendFailed("Invalid world/ session name: " + args[0], args[0]);
				return false;
			}
			return setHotswapExpire(settings, args[0], Arrays.copyOfRange(args, 1, args.length));
		}).addUsage("<true | false>")
				.addTabCompletion(args -> args.length == 2 ? List.of("true", "false") : List.of()));


		this.addSubAction(new ActionTree("complete_goal", args -> {
			var settings = getSettingsBuilder(args[0]);
			if (settings == null) {
				sendFailed("Invalid world/ session name: " + args[0], args[0]);
				return false;
			}
			return setCompleteGoal(settings, args[0], Arrays.copyOfRange(args, 1, args.length));
		})).addUsage("<win_goal>");


		this.addSubAction(new ActionTree("separate_cards", args -> {
			var settings = getSettingsBuilder(args[0]);
			if (settings == null) {
				sendFailed("Invalid world/ session name: " + args[0], args[0]);
				return false;
			}
			return setDifferentCardPerTeam(settings, args[0], Arrays.copyOfRange(args, 1, args.length));
		}).addUsage("<true | false>")
				.addTabCompletion(args -> args.length == 2 ? List.of("true", "false") : List.of()));


		this.addSubAction(new ActionTree("end", args -> end(args[0])));


		this.addSubAction(new ActionTree("preset", args -> {
			var settings = getSettingsBuilder(args[0]);
			if (settings == null) {
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


		this.addSubAction(new ActionTree("addplayer", this::addPlayerToSession).addUsage("<player_name>").addTabCompletion(args -> {
			if (args.length == 2) {
				return null;
			} else {
				return List.of();
			}
		}));


		this.addSubAction(new ActionTree("kickplayer", this::removePlayerFromSession).addUsage("<player_name> <target_world_name>").addTabCompletion(args -> {
			if (args.length == 2) {
				return null;
			} else if (args.length == 3) {
				return platform.getLoadedWorlds().stream().map(WorldHandle::name).toList();
			} else {
				return List.of();
			}
		}));


		this.addSubAction(new ActionTree("kickplayers", this::removeAllPlayersFromSession).addUsage("<target_world_name>").addTabCompletion(args -> {
			if (args.length == 2) {
				return platform.getLoadedWorlds().stream().map(WorldHandle::name).toList();
			} else {
				return List.of();
			}
		}));


		this.addSubAction(new ActionTree("vote", this::voteForPlayer).addUsage("<player_name> <vote_category> <vote_for>").addTabCompletion(args -> {
			BingoConfigurationData.VoteList voteList = manager.getGameConfig().getOptionValue(BingoOptions.VOTE_LIST);
			if (args.length <= 2) {
				return null;
			} else if (args.length == 3) {
				return List.of("kits", "gamemodes", "cards", "cardsizes");
			} else if (args.length == 4) {
				return switch (args[2]) {
					case "kits" -> voteList.kits();
					case "gamemodes" -> voteList.gamemodes();
					case "cards" -> voteList.cards();
					case "cardsizes" -> voteList.cardSizes();
					default -> List.of();
				};
			}
			return List.of();
		}));

		this.addSubAction(new ActionTree("playerdata", this::playerDataCommand)
				.addUsage("<save | load | remove> <player_name>")
				.addTabCompletion(args -> {
					if (args.length <= 2) {
						return List.of("save", "load", "remove");
					} else if (args.length == 3) {
						return null;
					}
					return List.of();
				}));
	}

//    @Override
//    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command autobingoCommand, @NonNull String alias, @NonNull String[] args) {
//        // AutoBingo should only work for admins or console.
//        if (commandSender instanceof Player p && !p.hasPermission("bingo.admin")) {
//            return false;
//        }
//
//        currentSender = commandSender;
//
//        if (!command.execute(args)) {
//            commandSender.sendMessage(PlayerDisplay.MINI_BUILDER.deserialize("<dark_gray> - <red>Usage: " + command.usage(args)));
//        }
//        return true;
//    }

	private BingoSettingsBuilder getSettingsBuilder(String sessionName) {
		BingoSession session = manager.getSession(sessionName);
		return session == null ? null : session.settingsBuilder;
	}

	public boolean create(String worldName) {
		if (manager.createSession(worldName)) {
			sendSuccess("Connected Bingo Reloaded to this world!", worldName);
			return true;
		}

		sendFailed("Could not create session, see console for details.", worldName);
		return false;
	}

	public boolean destroy(String worldName) {
		if (manager.destroySession(worldName)) {
			sendSuccess("Disconnected Bingo Reloaded from this world!", worldName);
			return true;
		}

		sendFailed("Could not destroy session, see console for details.", worldName);
		return false;
	}

	public boolean start(String worldName) {
		if (manager.startGame(worldName)) {
			sendSuccess("The game has started!", worldName);
			return true;
		}

		sendFailed("Could not start game, see console for details.", worldName);
		return false;
	}

	public boolean setKit(BingoSettingsBuilder settings, String worldName, String[] extraArguments) {
		if (extraArguments.length != 1) {
			sendFailed("Expected 3 arguments!", worldName);
			return false;
		}

		PlayerKit kit = PlayerKit.fromConfig(extraArguments[0]);

		if (!kit.isValid()) {
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
			settings.effects(EnumSet.allOf(EffectOptionFlags.class));
			sendSuccess("Updated active effects to " + EnumSet.allOf(EffectOptionFlags.class), worldName);
			return true;
		} else if (effect.equals("none")) {
			settings.effects(EnumSet.noneOf(EffectOptionFlags.class));
			sendSuccess("Updated active effects to " + EnumSet.noneOf(EffectOptionFlags.class), worldName);
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
		int seed = extraArguments.length > 1 ? BingoAction.toInt(extraArguments[1], 0) : 0;

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

		switch (extraArguments[0]) {
			case "true", "duration" -> settings.countdownType(BingoSettings.CountdownType.DURATION);
			case "false", "disabled" -> settings.countdownType(BingoSettings.CountdownType.DISABLED);
			case "time_limit" -> settings.countdownType(BingoSettings.CountdownType.TIME_LIMIT);
			default -> {
				sendFailed("Invalid countdown type '" + extraArguments[0] + "'", worldName);
				return false;
			}
		}
		sendSuccess("Set countdown type to " + extraArguments[0], worldName);
		return true;
	}

	public boolean setDuration(BingoSettingsBuilder settings, String worldName, String[] extraArguments) {
		if (extraArguments.length != 1) {
			sendFailed("Expected 3 arguments!", worldName);
			return false;
		}

		int gameDuration = BingoAction.toInt(extraArguments[0], 0);
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

		PlayerHandle player = platform.getPlayerFromName(playerName);
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
		if (participant == null) {
			participant = new BingoPlayer(player, session);
		}
		if (!session.teamManager.addMemberToTeam(participant, teamName)) {
			sendFailed("Player " + playerName + " could not be added to team " + teamName, sessionName);
			return false;
		}
		sendSuccess("Player " + playerName + " added to team " + teamName, sessionName);
		return true;
	}

	public boolean setTeamSize(BingoSettingsBuilder settings, String worldName, String[] extraArguments) {
		if (extraArguments.length != 1) {
			sendFailed("Expected 3 arguments!", worldName);
			return false;
		}

		int teamSize = Math.min(64, Math.max(1, BingoAction.toInt(extraArguments[0], 1)));

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
		if (mode == null) {
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
		sendSuccess("Set gamemode to " + view.mode() + " " + view.size().size + "x" + view.size().size, worldName);
		return true;
	}

	public boolean setHotswapGoal(BingoSettingsBuilder settings, String worldName, String[] extraArguments) {
		if (extraArguments.length == 0) {
			sendFailed("Expected at least 3 arguments!", worldName);
			return false;
		}

		int goal = 10;
		try {
			goal = Integer.parseInt(extraArguments[0]);
		} catch (NumberFormatException exception) {
			sendFailed("Invalid win goal amount '" + extraArguments[0] + "'", worldName);
			return false;
		}

		settings.hotswapGoal(goal);

		sendSuccess("Set hotswap goal to " + goal, worldName);
		return true;
	}

	public boolean setHotswapExpire(BingoSettingsBuilder settings, String worldName, String[] extraArguments) {
		if (extraArguments.length != 1) {
			sendFailed("Expected 3 arguments!", worldName);
			return false;
		}

		boolean value = extraArguments[0].equals("true");
		settings.expireHotswapTasks(value);

		sendSuccess((value ? "Enabled" : "Disabled") + " hotswap task expiration", worldName);
		return true;
	}

	public boolean setCompleteGoal(BingoSettingsBuilder settings, String worldName, String[] extraArguments) {
		if (extraArguments.length == 0) {
			sendFailed("Expected at least 3 arguments!", worldName);
			return false;
		}

		int goal;
		try {
			goal = Integer.parseInt(extraArguments[0]);
		} catch (NumberFormatException exception) {
			sendFailed("Invalid win goal amount '" + extraArguments[0] + "'", worldName);
			return false;
		}

		settings.completeGoal(goal);

		sendSuccess("Set complete goal to " + goal, worldName);
		return true;
	}

	public boolean setDifferentCardPerTeam(BingoSettingsBuilder settings, String worldName, String[] extraArguments) {
		if (extraArguments.length != 1) {
			sendFailed("Expected 3 arguments!", worldName);
			return false;
		}

		boolean value = extraArguments[0].equals("true");
		settings.differentCardPerTeam(value);

		sendSuccess((value ? "Enabled" : "Disabled") + " separate cards per team", worldName);
		return true;
	}

	public boolean end(String worldName) {
		if (manager.endGame(worldName)) {
			sendSuccess("Game forcefully ended!", worldName);
			return true;
		} else {
			sendFailed("Could not end the game, see console for details.", worldName);
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
				BingoSettings settings = settingsData.getSettings(path);
				if (settings == null) {
					sendFailed("Invalid settings path " + path, sessionName);
					return false;
				}
				settingsBuilder.fromOther(settings);
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

	private boolean addPlayerToSession(String... args) {
		String worldName = args[0];
		if (args.length != 2) {
			sendFailed("Expected 3 arguments!", worldName);
			return false;
		}

		String playerName = args[1];
		PlayerHandle player = platform.getPlayerFromName(playerName);
		if (player == null) {
			sendFailed("Player " + playerName + " could not be found.", worldName);
			return false;
		}
		if (!manager.teleportPlayerToSession(player, worldName)) {
			sendFailed("Could not teleport player to invalid world.", worldName);
			return false;
		}
		sendSuccess("Teleported " + playerName + " to " + worldName, worldName);
		return true;
	}

	private boolean removePlayerFromSession(String... args) {
		String worldName = args[0];
		if (args.length != 3) {
			sendFailed("Expected 4 arguments!", worldName);
			return false;
		}

		String playerName = args[1];
		PlayerHandle player = platform.getPlayerFromName(playerName);
		if (player == null) {
			sendFailed("Player " + playerName + " could not be found.", worldName);
			return false;
		}

		BingoSession session = manager.getSession(worldName);
		if (session == null || !session.ownsWorld(player.world())) {
			sendFailed("Player cannot be teleported. " + playerName + " is not in " + worldName, worldName);
			return false;
		}

		String targetWorldName = args[2];
		WorldHandle world = platform.getWorld(targetWorldName);
		if (world == null) {
			sendFailed("Could not teleport player to invalid world " + targetWorldName + ".", worldName);
			return false;
		}

		if (!player.teleportBlocking(world.spawnPoint())) {
			sendFailed("Could not teleport player to " + targetWorldName + ".", worldName);
			return false;
		}
		sendSuccess("Teleported " + playerName + " to " + targetWorldName, worldName);
		return true;
	}

	private boolean removeAllPlayersFromSession(String... args) {
		String worldName = args[0];
		if (args.length != 2) {
			sendFailed("Expected 3 arguments!", worldName);
			return false;
		}

		BingoSession session = manager.getSession(worldName);
		if (session == null) {
			sendFailed("Could not remove players from this world, invalid session", worldName);
			return false;
		}

		String targetWorldName = args[1];
		WorldHandle world = platform.getWorld(targetWorldName);
		if (world == null) {
			sendFailed("Could not teleport players to invalid world " + targetWorldName + ".", worldName);
			return false;
		}

		Set<PlayerHandle> allPlayers = session.getPlayersInWorld();
		int playerCount = allPlayers.size();
		int playersLeft = playerCount;
		for (PlayerHandle player : session.getPlayersInWorld()) {
			if (!session.ownsWorld(player.world())) {
				ConsoleMessenger.log("Player '" + player.playerName() + "' cannot be kicked from the session " + targetWorldName);
				continue;
			}

			if (!player.teleportBlocking(world.spawnPoint())) {
				ConsoleMessenger.bug("Could not teleport player '" + player.playerName() + "'(" + player.uniqueId() + ") for some reason", this);
				continue;
			}

			playersLeft--;
		}

		sendSuccess("Teleported " + (playerCount - playersLeft) + " out of " + playerCount + " players in " + worldName + " to " + targetWorldName, worldName);
		return true;
	}

	private Boolean voteForPlayer(String[] args) {
		String sessionName = args[0];
		if (args.length != 4) {
			sendFailed("Expected 5 arguments!", sessionName);
			return false;
		}

		BingoSession session = manager.getSession(sessionName);
		if (session == null) {
			sendFailed("Cannot cast a vote in this world (bingo is not being played here!).", sessionName);
			return false;
		}

		PlayerHandle player = platform.getPlayerFromName(args[1]);
		if (player == null) {
			sendFailed("Player '" + args[1] + "' does not exist!", sessionName);
			return false;
		}

		String category = args[2];
		String voteFor = args[3];
		if (!(session.phase() instanceof PregameLobby lobby)) {
			sendFailed("Cannot vote for player, game is not in lobby phase.", sessionName);
			return false;
		}

		BingoConfigurationData.VoteList voteList = manager.getGameConfig().getOptionValue(BingoOptions.VOTE_LIST);

		switch (category) {
			case "kits" -> {
				if (!voteList.kits().contains(voteFor)) {
					sendFailed("Cannot vote for kit " + voteFor + ", kit does not appear in vote list.", sessionName);
					return false;
				}

				if (!PlayerKit.fromConfig(voteFor).isValid()) {
					sendFailed("Cannot vote for kit " + voteFor + ", because it does not exist.", sessionName);
					return false;
				}
				lobby.voteKit(voteFor, player);
			}
			case "gamemodes" -> {
				if (!voteList.gamemodes().contains(voteFor)) {
					sendFailed("Cannot vote for gamemode " + voteFor + ", gamemode does not appear in vote list.", sessionName);
					return false;
				}
				lobby.voteGamemode(voteFor, player);
			}
			case "cards" -> {
				if (!voteList.cards().contains(voteFor)) {
					sendFailed("Cannot vote for card " + voteFor + ", card does not appear in vote list.", sessionName);
					return false;
				}
				lobby.voteCard(voteFor, player);
			}
			case "cardsizes" -> {
				if (!voteList.cardSizes().contains(voteFor)) {
					sendFailed("Cannot vote for card size " + voteFor + ", card size does not appear in vote list.", sessionName);
					return false;
				}
				lobby.voteCardsize(voteFor, player);
			}
			default -> {
				sendFailed("Cannot vote for '" + category + "', category does not exist in the vote list!", sessionName);
				return false;
			}
		}
		sendSuccess(player.displayName().append(Component.text(" voted for " + category + " " + voteFor)), sessionName);
		return true;
	}

	private boolean playerDataCommand(String... args) {
		String sessionName = args[0];
		if (args.length != 3) {
			return false;
		}

		PlayerSerializationData playerData = manager.getPlayerData();

		String playerName = args[2];
		PlayerHandle player = platform.getPlayerFromName(args[1]);
		if (player == null) {
			sendFailed("Cannot edit player data, player " + playerName + " not found", sessionName);
			return false;
		}

		return switch (args[1]) {
			case "load" -> {
				SerializablePlayer data = playerData.loadPlayer(player);
				if (data == null) {
					sendFailed("Cannot load player data, no data saved for " + playerName, sessionName);
					yield false;
				}
				sendSuccess("Loaded player data for " + playerName, sessionName);
				yield true;
			}
			case "save" -> {
				SerializablePlayer data = SerializablePlayer.fromPlayer(platform, player);
				playerData.savePlayer(data, true);
				sendSuccess("Saved player data for " + playerName, sessionName);
				yield true;
			}
			case "remove" -> {
				playerData.removePlayer(player.uniqueId());
				sendSuccess("Removed previously saved player data for " + playerName, sessionName);
				yield true;
			}
			default -> false;
		};
	}

	private void sendSuccess(String message, String sessionName) {
		sendSuccess(Component.text(message), sessionName);
	}

	private void sendFailed(String message, String sessionName) {
		sendFailed(Component.text(message), sessionName);
	}

	private void sendSuccess(Component message, String sessionName) {
		getLastUser().sendMessage(Component.text("(" + sessionName + ") ").append(message.color(NamedTextColor.GREEN)));
	}

	private void sendFailed(Component message, String sessionName) {
		getLastUser().sendMessage(Component.text("(" + sessionName + ") ").append(message.color(NamedTextColor.RED)));
	}
}
