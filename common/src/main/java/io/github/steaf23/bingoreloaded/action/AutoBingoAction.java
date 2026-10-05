package io.github.steaf23.bingoreloaded.action;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.api.BingoCommandSource;
import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.command.BingoCommand;
import io.github.steaf23.bingoreloaded.command.MappedCommand;
import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.BingoSettingsData;
import io.github.steaf23.bingoreloaded.data.PlayerSerializationData;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.data.helper.SerializablePlayer;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.GameManager;
import io.github.steaf23.bingoreloaded.gameloop.phase.PregameLobby;
import io.github.steaf23.bingoreloaded.lib.api.ActionUser;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.platform.GameContext;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.settings.BingoSettingsBuilder;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.bingoreloaded.settings.gamemode.BingoGamemode;
import io.github.steaf23.bingoreloaded.settings.gamemode.BingoGamemodes;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

public class AutoBingoAction<Source> extends MappedCommand<Source> {

	public AutoBingoAction(Settings<Source> mappingSettings) {
		super("autobingo", List.of("bingo.admin"), mappingSettings);

		this.addSessionNameSubAction("create", (context, name) -> create(context.gameManager(), name));

		this.addSessionNameSubAction("destroy", (context, name) -> destroy(context.gameManager(), name));

		this.addSessionNameSubAction("start", (context, name) -> start(context.gameManager(), name));

		fromSession(literal("kit")
				.then(argument("kit_name", StringArgumentType.word())
						.suggests((ctx, builder) -> {
							return suggestAll(builder, PlayerKit.validKits(), PlayerKit::configName);
						})
						.executes(settingsExecutor((context, settings, args) -> {
							return setKit(settings, StringArgumentType.getString(args, "kit_name"));
						}))
				)
		);

		fromSession(literal("addplayer")
				.then(playerArg("player_name")
						.executes(sessionNameExecutor((source, worldName, args) -> {
							String playerName = playerArgValue("player_name", args);
							return addPlayerToSession(source, worldName, playerName);
						}))));

		fromSession(literal("kickplayer")
				.then(playerArg("player_name")
						.then(argument("target_world", keyType())
								.suggests(this::allWorlds)
								.executes(sessionNameExecutor((source, worldName, args) -> {
									String playerName = playerArgValue("player_name", args);
									Key targetWorld = getKey(args, "target_world");
									return removePlayerFromSession(source, worldName, playerName, targetWorld);
								})))));

		fromSession(literal("kickplayers")
				.then(argument("target_world", keyType())
						.suggests(this::allWorlds)
						.executes(sessionNameExecutor((source, worldName, args) -> {
							Key targetWorld = getKey(args, "target_world");
							return removeAllPlayersFromSession(source, worldName, targetWorld);
						}))));

		var effectsRoot = literal("effects")
				.then(literal("none")
						.executes(settingsExecutor((context, settings, args) -> {
							return setEffect(settings, "none", true);
						})))
				.then(literal("all")
						.executes(settingsExecutor((context, settings, args) -> {
							return setEffect(settings, "all", true);
						}))
				);

		for (EffectOptionFlags flag : EnumSet.allOf(EffectOptionFlags.class)) {
			String effect = flag.name().toLowerCase();
			effectsRoot.then(literal(effect)
					.executes(settingsExecutor((context, settings, args) -> {
						return setEffect(settings, effect, true);
					}))
					.then(argument("enable", BoolArgumentType.bool())
							.executes(settingsExecutor((context, settings, args) -> {
								return setEffect(settings, effect, BoolArgumentType.getBool(args, "enable"));
							}))));
		}
		fromSession(effectsRoot);

		fromSession(literal("card")
				.then(argument("card_name", StringArgumentType.word())
						.suggests((source, builder) -> {
							return suggestAll(builder, new BingoCardData().getCardNames());
						})
						.executes()));
//
//		this.addSubAction(new ActionTree("card", (context, args) -> {
//			var settings = getSettingsBuilder(context, args[0]);
//			if (settings == null) {
//				sendFailed("Invalid world/ session name: " + args[0], args[0]);
//				return ActionResult.INCORRECT_USE;
//			}
//			return setCard(settings, args[0], Arrays.copyOfRange(args, 1, args.length));
//		}).addUsage("<card_name>"));
//
//		this.addSubAction(new ActionTree("countdown", (context, args) -> {
//			var settings = getSettingsBuilder(context, args[0]);
//			if (settings == null) {
//				sendFailed("Invalid world/ session name: " + args[0], args[0]);
//				return ActionResult.INCORRECT_USE;
//			}
//			return setCountdown(settings, args[0], Arrays.copyOfRange(args, 1, args.length));
//		}).addUsage("<type>")
//				.addTabCompletion(args -> args.length == 2 ? List.of("disabled", "duration", "time_limit") : List.of()));
//
//
//		this.addSubAction(new ActionTree("duration", (context, args) -> {
//			var settings = getSettingsBuilder(context, args[0]);
//			if (settings == null) {
//				sendFailed("Invalid world/ session name: " + args[0], args[0]);
//				return ActionResult.INCORRECT_USE;
//			}
//			return setDuration(settings, args[0], Arrays.copyOfRange(args, 1, args.length));
//		}).addUsage("<duration_minutes>"));
//
//
//		this.addSubAction(new ActionTree("team", (context, args) -> {
//			var settings = getSettingsBuilder(context, args[0]);
//			if (settings == null) {
//				sendFailed("Invalid world/ session name: " + args[0], args[0]);
//				return ActionResult.INCORRECT_USE;
//			}
//			return setPlayerTeam(context, args[0], Arrays.copyOfRange(args, 1, args.length));
//		}).addUsage("<player_name> <team_name>")
//				.addTabCompletion(args -> args.length == 2 || args.length == 3 ? List.of("") : List.of()));
//
//
//		this.addSubAction(new ActionTree("teamsize", (context, args) -> {
//			var settings = getSettingsBuilder(context, args[0]);
//			if (settings == null) {
//				sendFailed("Invalid world/ session name: " + args[0], args[0]);
//				return ActionResult.INCORRECT_USE;
//			}
//			return setTeamSize(settings, args[0], Arrays.copyOfRange(args, 1, args.length));
//		}).addUsage("<size>"));
//
//		this.addSubAction(new ActionTree("teamcount", (context, args) -> {
//			var settings = getSettingsBuilder(context, args[0]);
//			if (settings == null) {
//				sendFailed("Invalid world/ session name: " + args[0], args[0]);
//				return ActionResult.INCORRECT_USE;
//			}
//			return setTeamCount(settings, args[0], Arrays.copyOfRange(args, 1, args.length));
//		}).addUsage("<count>"));
//
//
//		this.addSubAction(new ActionTree("gamemode", (context, args) -> {
//			var settings = getSettingsBuilder(context, args[0]);
//			if (settings == null) {
//				sendFailed("Invalid world/ session name: " + args[0], args[0]);
//				return ActionResult.INCORRECT_USE;
//			}
//			return setGamemode(settings, args[0], Arrays.copyOfRange(args, 1, args.length));
//		}).addUsage("<regular | lockout | complete | hotswap | blitz> [3 | 5]")
//				.addTabCompletion(args -> switch (args.length) {
//					case 2 -> List.of("regular", "lockout", "complete", "hotswap", "blitz");
//					case 3 -> List.of("3", "5");
//					default -> COMPLETE_NOTHING;
//				}));
//
//
//		this.addSubAction(new ActionTree("hotswap_goal", (context, args) -> {
//			var settings = getSettingsBuilder(context, args[0]);
//			if (settings == null) {
//				sendFailed("Invalid world/ session name: " + args[0], args[0]);
//				return ActionResult.INCORRECT_USE;
//			}
//			return setHotswapGoal(settings, args[0], Arrays.copyOfRange(args, 1, args.length));
//		})).addUsage("<win_goal>");
//
//
//		this.addSubAction(new ActionTree("hotswap_expire", (context, args) -> {
//			var settings = getSettingsBuilder(context, args[0]);
//			if (settings == null) {
//				sendFailed("Invalid world/ session name: " + args[0], args[0]);
//				return ActionResult.INCORRECT_USE;
//			}
//			return setHotswapExpire(settings, args[0], Arrays.copyOfRange(args, 1, args.length));
//		}).addUsage("<true | false>")
//				.addTabCompletion(args -> args.length == 2 ? List.of("true", "false") : COMPLETE_NOTHING));
//
//
//		this.addSubAction(new ActionTree("complete_goal", (context, args) -> {
//			var settings = getSettingsBuilder(context, args[0]);
//			if (settings == null) {
//				sendFailed("Invalid world/ session name: " + args[0], args[0]);
//				return ActionResult.INCORRECT_USE;
//			}
//			return setCompleteGoal(settings, args[0], Arrays.copyOfRange(args, 1, args.length));
//		})).addUsage("<win_goal>");
//
//
//		this.addSubAction(new ActionTree("blitz_headstart", (context, args) -> {
//			var settings = getSettingsBuilder(context, args[0]);
//			if (settings == null) {
//				sendFailed("Invalid world/ session name: " + args[0], args[0]);
//				return ActionResult.INCORRECT_USE;
//			}
//			return setBlitzHeadstart(settings, args[0], Arrays.copyOfRange(args, 1, args.length));
//		})).addUsage("<duration_seconds>");
//
//
//		this.addSubAction(new ActionTree("blitz_bonus", (context, args) -> {
//			var settings = getSettingsBuilder(context, args[0]);
//			if (settings == null) {
//				sendFailed("Invalid world/ session name: " + args[0], args[0]);
//				return ActionResult.INCORRECT_USE;
//			}
//			return setBlitzBonus(settings, args[0], Arrays.copyOfRange(args, 1, args.length));
//		})).addUsage("<duration_seconds>");
//
//
//		this.addSubAction(new ActionTree("blitz_recovery_delay", (context, args) -> {
//			var settings = getSettingsBuilder(context, args[0]);
//			if (settings == null) {
//				sendFailed("Invalid world/ session name: " + args[0], args[0]);
//				return ActionResult.INCORRECT_USE;
//			}
//			return setBlitzRecovery(settings, args[0], Arrays.copyOfRange(args, 1, args.length));
//		})).addUsage("<amount of items>");
//
//
//		this.addSubAction(new ActionTree("separate_cards", (context, args) -> {
//			var settings = getSettingsBuilder(context, args[0]);
//			if (settings == null) {
//				sendFailed("Invalid world/ session name: " + args[0], args[0]);
//				return ActionResult.INCORRECT_USE;
//			}
//			return setDifferentCardPerTeam(settings, args[0], Arrays.copyOfRange(args, 1, args.length));
//		}).addUsage("<true | false>")
//				.addTabCompletion(args -> args.length == 2 ? List.of("true", "false") : COMPLETE_NOTHING));
//
//
//		this.addSubAction(new ActionTree("end", (context, args) -> end(context.gameManager(), args[0])));
//
//
//		this.addSubAction(new ActionTree("preset", (context, args) -> {
//			var settings = getSettingsBuilder(context, args[0]);
//			if (settings == null) {
//				sendFailed("Invalid world/ session name: " + args[0], args[0]);
//				return ActionResult.INCORRECT_USE;
//			}
//			return preset(settings, args[0], Arrays.copyOfRange(args, 1, args.length));
//		}).addUsage("<save | load | remove | default> <preset_name>")
//				.addTabCompletion(args -> {
//					BingoSettingsData settingsData = new BingoSettingsData();
//					return switch (args.length) {
//						case 2 -> List.of("save", "load", "remove", "default");
//						case 3 -> new ArrayList<>(settingsData.getPresetNames());
//						default -> COMPLETE_NOTHING;
//					};
//				}));

//
//		this.addSubAction(new ActionTree("vote", this::voteForPlayer).addUsage("<player_name> <vote_category> <vote_for>").addTabCompletion((context, args) -> {
//			BingoConfigurationData.VoteList voteList = context.gameManager().getGameConfig().getOptionValue(BingoOptions.VOTE_LIST);
//			if (args.length <= 2) {
//				return null;
//			} else if (args.length == 3) {
//				return List.of("kits", "gamemodes", "cards", "cardsizes");
//			} else if (args.length == 4) {
//				return switch (args[2]) {
//					case "kits" -> voteList.kits();
//					case "gamemodes" -> voteList.gamemodes();
//					case "cards" -> voteList.cards();
//					case "cardsizes" -> voteList.cardSizes();
//					default -> COMPLETE_NOTHING;
//				};
//			}
//			return COMPLETE_NOTHING;
//		}));
//
//		this.addSubAction(new ActionTree("playerdata", this::playerDataCommand)
//				.addUsage("<save | load | remove> <player_name>")
//				.addTabCompletion(args -> {
//					if (args.length <= 2) {
//						return List.of("save", "load", "remove");
//					} else if (args.length == 3) {
//						return COMPLETE_PLAYER;
//					}
//					return COMPLETE_NOTHING;
//				}));
	}

	private BingoSettingsBuilder getSettingsBuilder(GameContext context, String sessionName) {
		BingoSession session = context.gameManager().getSession(sessionName);
		return session == null ? null : session.settingsBuilder;
	}

	public AutoResult create(GameManager manager, String worldName) {
		if (manager.createSession(worldName)) {
			return sendSuccess("Connected Bingo Reloaded to this world!");
		}

		return sendFailed("Could not create session, see console for details.");
	}

	public AutoResult destroy(GameManager manager, String worldName) {
		if (manager.destroySession(worldName)) {
			return sendSuccess("Disconnected Bingo Reloaded from this world!");
		}

		return sendFailed("Could not destroy session, see console for details.");
	}

	public AutoResult start(GameManager manager, String worldName) {
		if (manager.startGame(worldName)) {
			return sendSuccess("The game has started!");
		}

		return sendFailed("Could not start game, see console for details.");
	}

	public AutoResult setKit(BingoSettingsBuilder settings, String kitName) {
		PlayerKit kit = PlayerKit.fromConfig(kitName);

		if (!kit.isValid()) {
			// Invalid custom kit selected, not possible!
			return sendFailed(Component.empty().append(Component.text("Cannot set kit to ")).append(kit.getDisplayName()).append(Component.text(". This custom kit is not defined. To create custom kits first, use /bingo kit.")));
		}
		settings.kit(kit);
		return sendSuccess(Component.empty().append(Component.text("Kit set to ")).append(kit.getDisplayName()));
	}

	public AutoResult setEffect(BingoSettingsBuilder settings, String effect, boolean enable) {
		// autobingo world effect <effect_name> [true | false]
		// If argument count is only 1, enable all, none or just the single effect typed.
		//     Else default enable effect unless the second argument is "false".

		if (effect.equals("all")) {
			settings.effects(EnumSet.allOf(EffectOptionFlags.class));
			return sendSuccess("Updated active effects to " + EnumSet.allOf(EffectOptionFlags.class));
		} else if (effect.equals("none")) {
			settings.effects(EnumSet.noneOf(EffectOptionFlags.class));
			return sendSuccess("Updated active effects to " + EnumSet.noneOf(EffectOptionFlags.class));
		}

		try {
			settings.toggleEffect(EffectOptionFlags.valueOf(effect.toUpperCase()), enable);
			return sendSuccess("Updated active effects to " + settings.view().effects());
		} catch (IllegalArgumentException e) {
			return sendFailed("Invalid effect: " + effect);
		}
	}

	public AutoResult setCard(BingoSettingsBuilder settings, String cardName, int seed) {
		BingoCardData cardsData = new BingoCardData();
		if (cardsData.getCardNames().contains(cardName)) {
			settings.cardName(cardName).cardSeed(seed);
			return sendSuccess("Playing card set to " + cardName + " with" +
					(seed == 0 ? " no seed" : " seed " + seed));
		}
		return sendFailed("No card named '" + cardName + "' was found!");
	}

	public AutoResult setCountdown(BingoSettingsBuilder settings, String type) {
		switch (type) {
			case "true", "duration" -> settings.countdownType(BingoSettings.CountdownType.DURATION);
			case "false", "disabled" -> settings.countdownType(BingoSettings.CountdownType.DISABLED);
			case "time_limit" -> settings.countdownType(BingoSettings.CountdownType.TIME_LIMIT);
			default -> {
				return sendFailed("Invalid countdown type '" + type + "'");
			}
		}
		return sendSuccess("Set countdown type to " + type);
	}

	public AutoResult setDuration(BingoSettingsBuilder settings, int gameDuration) {

		if (gameDuration > 0) {
			settings.countdownGameDuration(gameDuration);
			return sendSuccess("Set game duration for countdown mode to " + gameDuration);
		}

		return sendFailed("Cannot set duration to " + gameDuration);
	}

	public AutoResult setPlayerTeam(GameContext context, BingoSession session, String playerName, String teamName) {
		PlayerHandle player = context.server().getPlayerFromName(playerName);
		if (player == null) {
			return sendFailed("Cannot add " + playerName + " to team, player does not exist/ is not online!");
		}

		if (teamName.equalsIgnoreCase("none")) {
			BingoParticipant participant = session.teamManager.getPlayerAsParticipant(player);
			if (participant == null) {
				return sendFailed(playerName + " did not join any teams!");
			}

			session.teamManager.removeMemberFromTeam(participant);
			return sendSuccess("Player " + playerName + " removed from all teams");
		}
		BingoParticipant participant = session.teamManager.getPlayerAsParticipant(player);
		if (participant == null) {
			participant = new BingoPlayer(player, session);
		}
		if (!session.teamManager.addMemberToTeam(participant, teamName)) {
			return sendFailed("Player " + playerName + " could not be added to team " + teamName);
		}
		return sendSuccess("Player " + playerName + " added to team " + teamName);
	}

	public AutoResult setTeamSize(BingoSettingsBuilder settings, String[] extraArguments) {
		if (extraArguments.length != 1) {
			return sendFailed("Expected 3 arguments!");
		}

		int teamSize = Math.min(64, Math.max(1, BingoCommand.toInt(extraArguments[0], 1)));

		settings.maxTeamSize(teamSize);
		return sendSuccess("Set maximum team size to " + teamSize + " players");
	}

	public AutoResult setTeamCount(BingoSettingsBuilder settings, String[] extraArguments) {
		if (extraArguments.length != 1) {
			return sendFailed("Expected 3 arguments!");
		}

		int newCount = Math.min(64, Math.max(1, BingoCommand.toInt(extraArguments[0], 1)));

		settings.maxTeamCount(newCount);
		return sendSuccess("Set maximum team count to " + newCount + " teams");
	}

	public AutoResult setGamemode(BingoSettingsBuilder settings, String[] extraArguments) {
		if (extraArguments.length == 0) {
			return sendFailed("Expected at least 3 arguments!");
		}

		BingoGamemode mode = BingoGamemodes.fromDataString(extraArguments[0], true);
		if (mode == null) {
			return sendFailed("Unknown gamemode '" + extraArguments[0] + "'");
		}
		settings.mode(mode);

		if (extraArguments.length == 2 && extraArguments[1].equals("3")) {
			settings.cardSize(CardSize.X3);
		} else {
			settings.cardSize(CardSize.X5);
		}

		BingoSettings view = settings.view();
		return sendSuccess("Set gamemode to " + extraArguments[0] + " " + view.size().size + "x" + view.size().size);
	}

	public AutoResult setHotswapGoal(BingoSettingsBuilder settings, String[] extraArguments) {
		if (extraArguments.length == 0) {
			return sendFailed("Expected at least 3 arguments!");
		}

		int goal = 10;
		try {
			goal = Integer.parseInt(extraArguments[0]);
		} catch (NumberFormatException exception) {
			return sendFailed("Invalid win goal amount '" + extraArguments[0] + "'");
		}

		settings.hotswapGoal(goal);

		return sendSuccess("Set hotswap goal to " + goal);
	}

	public AutoResult setHotswapExpire(BingoSettingsBuilder settings, String[] extraArguments) {
		if (extraArguments.length != 1) {
			return sendFailed("Expected 3 arguments!");
		}

		boolean value = extraArguments[0].equals("true");
		settings.expireHotswapTasks(value);

		return sendSuccess((value ? "Enabled" : "Disabled") + " hotswap task expiration");
	}

	public AutoResult setCompleteGoal(BingoSettingsBuilder settings, String[] extraArguments) {
		if (extraArguments.length == 0) {
			return sendFailed("Expected at least 3 arguments!");
		}

		int goal;
		try {
			goal = Integer.parseInt(extraArguments[0]);
		} catch (NumberFormatException exception) {
			return sendFailed("Invalid win goal amount '" + extraArguments[0] + "'");
		}

		settings.completeGoal(goal);

		return sendSuccess("Set complete goal to " + goal);
	}

	public AutoResult setBlitzHeadstart(BingoSettingsBuilder settings, String[] extraArguments) {
		if (extraArguments.length == 0) {
			return sendFailed("Expected at least 3 arguments!");
		}

		int goal;
		try {
			goal = Integer.parseInt(extraArguments[0]);
		} catch (NumberFormatException exception) {
			return sendFailed("Invalid duration '" + extraArguments[0] + "'");
		}

		settings.blitzStartDuration(goal);

		return sendSuccess("Set blitz head start to " + goal);
	}

	public AutoResult setBlitzBonus(BingoSettingsBuilder settings, String[] extraArguments) {
		if (extraArguments.length == 0) {
			return sendFailed("Expected at least 3 arguments!");
		}

		int goal;
		try {
			goal = Integer.parseInt(extraArguments[0]);
		} catch (NumberFormatException exception) {
			return sendFailed("Invalid duration '" + extraArguments[0] + "'");
		}

		settings.blitzBonusDuration(goal);

		return sendSuccess("Set blitz bonus to " + goal);
	}

	public AutoResult setBlitzRecovery(BingoSettingsBuilder settings, String[] extraArguments) {
		if (extraArguments.length == 0) {
			return sendFailed("Expected at least 3 arguments!");
		}

		int goal;
		try {
			goal = Integer.parseInt(extraArguments[0]);
		} catch (NumberFormatException exception) {
			return sendFailed("Invalid task amount '" + extraArguments[0] + "'");
		}

		settings.blitzRecoveryDelay(goal);

		return sendSuccess("Set recovery delay goal to " + goal);
	}

	public AutoResult setDifferentCardPerTeam(BingoSettingsBuilder settings, String[] extraArguments) {
		if (extraArguments.length != 1) {
			return sendFailed("Expected 3 arguments!");
		}

		boolean value = extraArguments[0].equals("true");
		settings.differentCardPerTeam(value);

		return sendSuccess((value ? "Enabled" : "Disabled") + " separate cards per team");
	}

	public AutoResult end(GameManager manager, String worldName) {
		if (manager.endGame(worldName)) {
			return sendSuccess("Game forcefully ended!");
		} else {
			return sendFailed("Could not end the game, see console for details.");
		}
	}

	public AutoResult preset(BingoSettingsBuilder settingsBuilder, String sessionName, String[] extraArguments) {
		if (extraArguments.length != 2) {
			return sendFailed("Expected 4 arguments!");
		}

		BingoSettingsData settingsData = new BingoSettingsData();

		String path = extraArguments[1];
		if (path.isBlank()) {
			return sendFailed("Please enter a valid preset name");
		}

		switch (extraArguments[0]) {
			case "save" -> {
				settingsData.saveSettings(path, settingsBuilder.view());
				return sendSuccess("Saved settings to '" + path + "'.");
			}
			case "load" -> {
				BingoSettings settings = settingsData.getSettings(path);
				if (settings == null) {
					return sendFailed("Invalid settings path " + path);
				}
				settingsBuilder.fromOther(settings, path);
				return sendSuccess("Loaded settings from '" + path + "'.");
			}
			case "remove" -> {
				settingsData.removeSettings(path);
				return sendSuccess("Removed settings preset '" + path + "'.");
			}
			case "default" -> {
				settingsData.setDefaultSettings(path);
				return sendSuccess("Set '" + path + "' as default settings for new worlds.");
			}
			default -> {
				return sendFailed("Unknown error");
			}
		}
	}

	private AutoResult addPlayerToSession(GameContext context, String worldName, String playerName) {
		PlayerHandle player = context.server().getPlayerFromName(playerName);
		if (player == null) {
			return sendFailed("Player " + playerName + " could not be found.");
		}
		if (!context.gameManager().teleportPlayerToSession(player, worldName)) {
			return sendFailed("Could not teleport player to invalid world.");
		}
		return sendSuccess("Teleported " + playerName + " to " + worldName);
	}

	private AutoResult removePlayerFromSession(GameContext context, String worldName, String playerName, Key targetWorld) {

		var server = context.server();

		PlayerHandle player = server.getPlayerFromName(playerName);
		if (player == null) {
			return sendFailed("Player " + playerName + " could not be found.");
		}

		Optional<BingoSession> session = Optional.ofNullable(context.getSession(worldName));
		if (session.isEmpty() || !session.get().ownsWorld(player.world())) {
			return sendFailed("Player cannot be teleported. " + playerName + " is not in " + worldName);
		}

		WorldHandle world = server.getWorld(targetWorld);
		if (world == null) {
			return sendFailed("Could not teleport " + playerName + " to invalid world " + targetWorld + ".");
		}

		boolean teleportSucceeded = player.teleportBlocking(world.spawnPoint());
		if (!context.getConfigOption(BingoOptions.SAVE_PLAYER_INFORMATION) && !teleportSucceeded) {
			return sendFailed("Could not teleport " + playerName + " to " + targetWorld + " because of some error.");
		}
		return sendSuccess("Teleported " + playerName + " to " + targetWorld);
	}

	private AutoResult removeAllPlayersFromSession(GameContext context, String worldName, Key targetWorld) {
		BingoSession session = context.getSession(worldName);
		if (session == null) {
			return sendFailed("Could not remove players from this world, invalid session");
		}

		WorldHandle world = context.server().getWorld(targetWorld);
		if (world == null) {
			return sendFailed("Could not teleport players to invalid world " + targetWorld + ".");
		}

		Set<PlayerHandle> allPlayers = session.getPlayersInWorld();
		int playerCount = allPlayers.size();
		int playersLeft = playerCount;
		for (PlayerHandle player : session.getPlayersInWorld()) {
			if (!session.ownsWorld(player.world())) {
				ConsoleMessenger.log("Player '" + player.playerName() + "' cannot be kicked from the session " + targetWorld);
				continue;
			}

			boolean teleportSucceeded = player.teleportBlocking(world.spawnPoint());
			if (!context.getConfigOption(BingoOptions.SAVE_PLAYER_INFORMATION) && !teleportSucceeded) {
				ConsoleMessenger.bug("Could not teleport player '" + player.playerName() + "'(" + player.uniqueId() + ") for some reason", this);
				continue;
			}

			playersLeft--;
		}

		return sendSuccess("Teleported " + (playerCount - playersLeft) + " out of " + playerCount + " players in " + worldName + " to " + targetWorld);
	}

	private AutoResult voteForPlayer(GameContext context, String[] args) {
		String sessionName = args[0];
		if (args.length != 4) {
			return sendFailed("Expected 5 arguments!");
		}

		BingoSession session = context.getSession(sessionName);
		if (session == null) {
			return sendFailed("Cannot cast a vote in this world (bingo is not being played here!).");
		}

		PlayerHandle player = context.server().getPlayerFromName(args[1]);
		if (player == null) {
			return sendFailed("Player '" + args[1] + "' does not exist!");
		}

		String category = args[2];
		String voteFor = args[3];
		if (!(session.phase() instanceof PregameLobby lobby)) {
			return sendFailed("Cannot vote for player, game is not in lobby phase.");
		}

		BingoConfigurationData.VoteList voteList = context.getConfigOption(BingoOptions.VOTE_LIST);

		switch (category) {
			case "kits" -> {
				if (!voteList.kits().contains(voteFor)) {
					return sendFailed("Cannot vote for kit " + voteFor + ", kit does not appear in vote list.");
				}

				if (!PlayerKit.fromConfig(voteFor).isValid()) {
					return sendFailed("Cannot vote for kit " + voteFor + ", because it does not exist.");
				}
				lobby.voteKit(voteFor, player);
			}
			case "gamemodes" -> {
				if (!voteList.gamemodes().contains(voteFor)) {
					return sendFailed("Cannot vote for gamemode " + voteFor + ", gamemode does not appear in vote list.");
				}
				lobby.voteGamemode(voteFor, player);
			}
			case "cards" -> {
				if (!voteList.cards().contains(voteFor)) {
					return sendFailed("Cannot vote for card " + voteFor + ", card does not appear in vote list.");
				}
				lobby.voteCard(voteFor, player);
			}
			case "cardsizes" -> {
				if (!voteList.cardSizes().contains(voteFor)) {
					return sendFailed("Cannot vote for card size " + voteFor + ", card size does not appear in vote list.");
				}
				lobby.voteCardsize(voteFor, player);
			}
			default -> {
				return sendFailed("Cannot vote for '" + category + "', category does not exist in the vote list!");
			}
		}
		return sendSuccess(player.displayName().append(Component.text(" voted for " + category + " " + voteFor)));
	}

	private AutoResult playerDataCommand(GameContext context, String[] args) {
		String sessionName = args[0];
		if (args.length != 3) {
			return sendFailed("Unknown error");
		}

		PlayerSerializationData playerData = context.gameManager().getPlayerData();
		var server = context.server();

		String playerName = args[2];
		PlayerHandle player = server.getPlayerFromName(args[1]);
		if (player == null) {
			return sendFailed("Cannot edit player data, player " + playerName + " not found");
		}

		return switch (args[1]) {
			case "load" -> {
				SerializablePlayer data = playerData.loadPlayer(player);
				if (data == null) {
					yield sendFailed("Cannot load player data, no data saved for " + playerName);
				}
				yield sendSuccess("Loaded player data for " + playerName);
			}
			case "save" -> {
				SerializablePlayer data = SerializablePlayer.fromPlayer(BingoReloaded.getMetaInfo().version(), player);
				playerData.savePlayer(data, true);
				yield sendSuccess("Saved player data for " + playerName);
			}
			case "remove" -> {
				playerData.removePlayer(player.uniqueId());
				yield sendSuccess("Removed previously saved player data for " + playerName);
			}
			default -> sendFailed("Unknown error");
		};
	}

	private AutoResult sendSuccess(String message) {
		return new AutoResult(Command.SINGLE_SUCCESS, message);
	}

	private AutoResult sendFailed(String message) {
		return new AutoResult(0, message);
	}

	private AutoResult sendSuccess(Component message) {
		return new AutoResult(Command.SINGLE_SUCCESS, message);
	}

	private AutoResult sendFailed(Component message) {
		return new AutoResult(0, message);
	}

	private void sendSuccess(ActionUser user, Component message, String sessionName) {
		user.sendMessage(Component.text("(" + sessionName + ") ").append(message.color(NamedTextColor.GREEN)));
	}

	private void sendFailed(ActionUser user, Component message, String sessionName) {
		user.sendMessage(Component.text("(" + sessionName + ") ").append(message.color(NamedTextColor.RED)));
	}

	public void addSessionNameSubAction(String name, BiFunction<GameContext, String, AutoResult> action) {
		fromSession(literal(name)
				.executes(sessionNameExecutor((source, worldName, _) -> action.apply(source, worldName)))
		);
	}

//	public void addSessionSubAction(String name, BingoCommand.SessionActionExecutor<Source> action) {
//		then(literal(name)
//				.then(worldArg()
//						.executes(sessionExecutor(action)))
//		);
//	}

	public void addSettingsSubAction(String name, SettingsActionExecutor<Source> action) {
		fromSession(literal(name)
				.executes(settingsExecutor(action)));
	}

//	private Command<Source> sessionExecutor(BingoCommand.SessionActionExecutor<Source> executor) {
//		return ctx -> {
//			String sessionName = StringArgumentType.getString(ctx, "world");
//			BingoCommandSource source = mapSource(ctx.getSource());
//			Optional<BingoSession> session = source.getSessionByName(sessionName);
//			if (session.isEmpty()) {
//				return 0;
//			}
//			return executor.execute(source, ctx, session.orElseThrow());
//		};
//	}

	private Command<Source> settingsExecutor(SettingsActionExecutor<Source> action) {
		return ctx -> {
			String sessionName = StringArgumentType.getString(ctx, "world");
			BingoCommandSource source = mapSource(ctx.getSource());
			Optional<BingoSession> session = source.getSessionByName(sessionName);
			if (session.isEmpty()) {
				sendFailed(source.user(), Component.text("Invalid world/ session name: " + sessionName), sessionName);
				return 0;
			}
			AutoResult result = action.execute(source.context(), session.get().settingsBuilder, ctx);
			if (result.code() == Command.SINGLE_SUCCESS) {
				sendSuccess(source.user(), result.message(), sessionName);
			} else {
				sendFailed(source.user(), result.message(), sessionName);
			}
			return result.code();
		};
	}

	private Command<Source> sessionNameExecutor(SessionNameActionExecuter<Source> action) {
		return ctx -> {
			String sessionName = StringArgumentType.getString(ctx, "world");
			BingoCommandSource source = mapSource(ctx.getSource());
			AutoResult result = action.execute(source.context(), sessionName, ctx);
			if (result.code() == Command.SINGLE_SUCCESS) {
				sendSuccess(source.user(), result.message(), sessionName);
			} else {
				sendFailed(source.user(), result.message(), sessionName);
			}
			return result.code();
		};
	}

	private void fromSession(LiteralArgumentBuilder<Source> subCommand) {
		then(sessionArg("world").then(subCommand));
	}

	private ArgumentBuilder<Source, ?> sessionArg(String argName) {
		return argument(argName, StringArgumentType.string())
				.suggests((ctx, builder) -> {
					BingoCommandSource source = mapSource(ctx.getSource());
					return suggestAll(builder, source.gameManager().getSessionNames());
				});
	}

	private ArgumentBuilder<Source, ?> playerArg(String name) {
		return argument(name, StringArgumentType.word())
				.suggests((context, builder) -> {
					BingoCommandSource source = mapSource(context.getSource());
					var server = source.context().server();
					return suggestAll(builder, server.getOnlinePlayers(), PlayerHandle::playerName);
				});
	}

	private String playerArgValue(String name, CommandContext<Source> context) {
		return StringArgumentType.getString(context, name);
	}

	private CompletableFuture<Suggestions> allWorlds(final CommandContext<Source> context, final SuggestionsBuilder builder) {
		BingoCommandSource source = mapSource(context.getSource());
		return suggestAll(builder, source.context().server().getLoadedWorlds(), w -> w.key().asString());
	}

	private <E> CompletableFuture<Suggestions> suggestAll(SuggestionsBuilder builder, Collection<E> collection, Function<E, String> map) {
		for (E e : collection) {
			builder.suggest(map.apply(e));
		}
		return builder.buildFuture();
	}

	private CompletableFuture<Suggestions> suggestAll(SuggestionsBuilder builder, Collection<String> collection) {
		for (String e : collection) {
			builder.suggest(e);
		}
		return builder.buildFuture();
	}

	public record AutoResult(int code, Component message) {

		public AutoResult(int code, String message) {
			this(code, Component.text(message));
		}
	}

	@FunctionalInterface
	public interface SettingsActionExecutor<Source> {

		AutoResult execute(GameContext context, BingoSettingsBuilder settings, CommandContext<Source> args);
	}

	@FunctionalInterface
	public interface SessionNameActionExecuter<Source> {

		AutoResult execute(GameContext context, String sessionName, CommandContext<Source> args);
	}

}
