package io.github.steaf23.bingoreloaded.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.api.BingoCommandSource;
import io.github.steaf23.bingoreloaded.cards.CardSize;
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
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.settings.BingoSettingsBuilder;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.bingoreloaded.settings.gamemode.BingoGamemode;
import io.github.steaf23.bingoreloaded.settings.gamemode.BingoGamemodes;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public class AutoBingoCommand<Source> extends MappedCommand<Source> {

	public AutoBingoCommand(Settings<Source> mappingSettings) {
		super("autobingo", List.of("bingo.admin"), mappingSettings);

		addGameManagerSubAction("create", this::create);

		addGameManagerSubAction("destroy", this::destroy);

		addGameManagerSubAction("start", this::start);

		addGameManagerSubAction("end", this::end);

		fromSession(literal("kit")
				.then(argument("kit_name", StringArgumentType.word())
						.suggests((ctx, builder) -> {
							return suggestAll(builder, PlayerKit.validKits(), PlayerKit::configName);
						})
						.executes(settingsExecutor((settings, args) -> {
							return setKit(settings, StringArgumentType.getString(args, "kit_name"));
						}))
				)
		);

		fromSession(literal("addplayer")
				.then(playerArg("player_name")
						.executes(sessionNameExecutor((source, worldName, args) -> {
							String playerName = playerArgValue("player_name", args);
							return addPlayerToSession(source, worldName, playerName);
						})))
		);

		fromSession(literal("kickplayer")
				.then(playerArg("player_name")
						.then(argument("target_world", keyType())
								.suggests(this::allWorlds)
								.executes(sessionNameExecutor((source, worldName, args) -> {
									String playerName = playerArgValue("player_name", args);
									Key targetWorld = getKey(args, "target_world");
									return removePlayerFromSession(source, worldName, playerName, targetWorld);
								}))))
		);

		fromSession(literal("kickplayers")
				.then(argument("target_world", keyType())
						.suggests(this::allWorlds)
						.executes(sessionNameExecutor((source, worldName, args) -> {
							Key targetWorld = getKey(args, "target_world");
							return removeAllPlayersFromSession(source, worldName, targetWorld);
						})))
		);

		fromSession(literal("team")
				.then(playerArg("player_name")
						.then(teamArg()
								.executes(sessionExecutor((context, session, args) -> {
									String playerName = playerArgValue("player_name", args);
									String teamName = teamArgValue(args);
									return setPlayerTeam(context, session, playerName, teamName);
								}))))
		);

		var effectsRoot = literal("effects")
				.then(literal("none")
						.executes(settingsExecutor((settings, args) -> {
							return setEffect(settings, "none", true);
						})))
				.then(literal("all")
						.executes(settingsExecutor((settings, args) -> {
							return setEffect(settings, "all", true);
						}))
				);

		for (EffectOptionFlags flag : EnumSet.allOf(EffectOptionFlags.class)) {
			String effect = flag.name().toLowerCase();
			effectsRoot.then(literal(effect)
					.executes(settingsExecutor((settings, args) -> {
						return setEffect(settings, effect, true);
					}))
					.then(argument("enable", BoolArgumentType.bool())
							.executes(settingsExecutor((settings, args) -> {
								boolean enable = BoolArgumentType.getBool(args, "enable");
								return setEffect(settings, effect, enable);
							})))
			);
		}
		fromSession(effectsRoot);

		fromSession(literal("card")
				.then(argument("card_name", StringArgumentType.word())
						.suggests((source, builder) -> {
							return suggestAll(builder, new BingoCardData().getCardNames());
						})
						.executes(settingsExecutor((settings, args) -> {
							String cardName = StringArgumentType.getString(args, "card_name");
							return setCard(settings, cardName, 0);
						}))
						.then(argument("seed", IntegerArgumentType.integer())
								.executes(settingsExecutor((settings, args) -> {
									String cardName = StringArgumentType.getString(args, "card_name");
									int seed = IntegerArgumentType.getInteger(args, "seed");
									return setCard(settings, cardName, seed);
								}))))
		);

		fromSession(literal("countdown")
				.then(literal("disabled").executes(settingsExecutor((settings, _) -> setCountdown(settings, "disabled"))))
				.then(literal("duration").executes(settingsExecutor((settings, _) -> setCountdown(settings, "duration"))))
				.then(literal("time_limit").executes(settingsExecutor((settings, _) -> setCountdown(settings, "time_limit"))))
		);

		basicSetting("duration", "duration_minutes", IntegerArgumentType.integer(), IntegerArgumentType::getInteger, this::setDuration);

		basicSetting("teamsize", "size", IntegerArgumentType.integer(1, 64), IntegerArgumentType::getInteger, this::setTeamSize);

		basicSetting("teamcount", "count", IntegerArgumentType.integer(1, 64), IntegerArgumentType::getInteger, this::setTeamCount);

		basicSetting("hotswap_goal", "goal", IntegerArgumentType.integer(1, 64), IntegerArgumentType::getInteger, this::setHotswapGoal);

		basicSetting("complete_goal", "goal", IntegerArgumentType.integer(1, 64), IntegerArgumentType::getInteger, this::setCompleteGoal);

		basicSetting("hotswap_expire", "expire", BoolArgumentType.bool(), BoolArgumentType::getBool, this::setHotswapExpire);

		basicSetting("blitz_headstart", "headstart", IntegerArgumentType.integer(1, 64), IntegerArgumentType::getInteger, this::setBlitzHeadstart);

		basicSetting("blitz_bonus", "bonus", IntegerArgumentType.integer(1, 64), IntegerArgumentType::getInteger, this::setBlitzBonus);

		basicSetting("blitz_recovery_delay", "recovery_delay", IntegerArgumentType.integer(1, 64), IntegerArgumentType::getInteger, this::setBlitzRecovery);

		basicSetting("separate_cards", "separate", BoolArgumentType.bool(), BoolArgumentType::getBool, this::setDifferentCardPerTeam);

		fromSession(literal("gamemode")
				.then(argument("mode", StringArgumentType.word())
						.suggests((source, builder) -> {
							return suggestAll(builder, BingoGamemodes.GAMEMODES.keySet());
						})
						.executes(settingsExecutor((settings, args) -> {
							String mode = StringArgumentType.getString(args, "mode");
							return setGamemode(settings, mode, 5);
						}))
						.then(literal("3")
								.executes(settingsExecutor((settings, args) -> {
									String mode = StringArgumentType.getString(args, "mode");
									return setGamemode(settings, mode, 3);
								})))
						.then(literal("5")
								.executes(settingsExecutor((settings, args) -> {
									String mode = StringArgumentType.getString(args, "mode");
									return setGamemode(settings, mode, 5);
								})))
				)
		);

		fromSession(literal("preset")
				.then(addPresetOption("save", (settingsBuilder, data, path) -> {
					data.saveSettings(path, settingsBuilder.view());
					return sendSuccess("Saved settings to '" + path + "'.");
				}))
				.then(addPresetOption("load", (settingsBuilder, data, path) -> {
					BingoSettings settings = data.getSettings(path);
					if (settings == null) {
						return sendFailed("Invalid settings path " + path);
					}
					settingsBuilder.fromOther(settings, path);
					return sendSuccess("Loaded settings from '" + path + "'.");
				}))
				.then(addPresetOption("remove", (settingsBuilder, data, path) -> {
					data.removeSettings(path);
					return sendSuccess("Removed settings preset '" + path + "'.");
				}))
				.then(addPresetOption("default", (settingsBuilder, data, path) -> {
					data.setDefaultSettings(path);
					return sendSuccess("Set '" + path + "' as default settings for new worlds.");
				}))
		);

		fromSession(literal("vote")
				.then(playerArg("player_name")
						.then(argument("category", StringArgumentType.word())
								.suggests((context, builder) -> {
									BingoCommandSource source = mapSource(context.getSource());
									return suggestAll(builder, source.context().getConfigOption(BingoOptions.VOTE_LIST).usedCategories());
								})
								.then(argument("vote_for", StringArgumentType.word())
										.suggests((args, builder) -> {
											BingoCommandSource source = mapSource(args.getSource());
											String category = StringArgumentType.getString(args, "category");
											return suggestAll(builder, source.context().getConfigOption(BingoOptions.VOTE_LIST).optionsPerCategory(category));
										})
										.executes(sessionExecutor((source, session, args) -> {
											String playerName = playerArgValue("player_name", args);
											String category = StringArgumentType.getString(args, "category");
											String voteFor = StringArgumentType.getString(args, "vote_for");
											return voteForPlayer(source, session, playerName, category, voteFor);
										})))))
		);

		fromSession(literal("playerdata")
				.then(addPlayerDataOption("load", (player, playerData, playerName) -> {
					SerializablePlayer data = playerData.loadPlayer(player);
					if (data == null) {
						return sendFailed("Cannot load player data, no data saved for " + playerName);
					}
					return sendSuccess("Loaded player data for " + playerName);
				}))
				.then(addPlayerDataOption("save", (player, playerData, playerName) -> {
					SerializablePlayer data = SerializablePlayer.fromPlayer(BingoReloaded.getMetaInfo().version(), player);
					playerData.savePlayer(data, true);
					return sendSuccess("Saved player data for " + playerName);
				}))
				.then(addPlayerDataOption("remove", (player, playerData, playerName) -> {
					playerData.removePlayer(player.uniqueId());
					return sendSuccess("Removed previously saved player data for " + playerName);
				}))
		);
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

	public AutoResult setTeamSize(BingoSettingsBuilder settings, int teamSize) {
		settings.maxTeamSize(teamSize);
		return sendSuccess("Set maximum team size to " + teamSize + " players");
	}

	public AutoResult setTeamCount(BingoSettingsBuilder settings, int newCount) {
		settings.maxTeamCount(newCount);
		return sendSuccess("Set maximum team count to " + newCount + " teams");
	}

	public AutoResult setGamemode(BingoSettingsBuilder settings, String gamemodeString, int cardSize) {
		BingoGamemode mode = BingoGamemodes.fromDataString(gamemodeString, true);
		if (mode == null) {
			return sendFailed("Unknown gamemode '" + gamemodeString + "'");
		}
		settings.mode(mode);

		if (cardSize == 3) {
			settings.cardSize(CardSize.X3);
		} else {
			settings.cardSize(CardSize.X5);
		}

		BingoSettings view = settings.view();
		return sendSuccess("Set gamemode to " + gamemodeString + " " + view.size().size + "x" + view.size().size);
	}

	public AutoResult setHotswapGoal(BingoSettingsBuilder settings, int goal) {
		settings.hotswapGoal(goal);
		return sendSuccess("Set hotswap goal to " + goal);
	}

	public AutoResult setHotswapExpire(BingoSettingsBuilder settings, boolean value) {
		settings.expireHotswapTasks(value);
		return sendSuccess((value ? "Enabled" : "Disabled") + " hotswap task expiration");
	}

	public AutoResult setCompleteGoal(BingoSettingsBuilder settings, int goal) {
		settings.completeGoal(goal);
		return sendSuccess("Set complete goal to " + goal);
	}

	public AutoResult setBlitzHeadstart(BingoSettingsBuilder settings, int headStart) {
		settings.blitzStartDuration(headStart);
		return sendSuccess("Set blitz head start to " + headStart);
	}

	public AutoResult setBlitzBonus(BingoSettingsBuilder settings, int bonus) {
		settings.blitzBonusDuration(bonus);
		return sendSuccess("Set blitz bonus to " + bonus);
	}

	public AutoResult setBlitzRecovery(BingoSettingsBuilder settings, int recoveryDelay) {
		settings.blitzRecoveryDelay(recoveryDelay);
		return sendSuccess("Set recovery delay goal to " + recoveryDelay);
	}

	public AutoResult setDifferentCardPerTeam(BingoSettingsBuilder settings, boolean value) {
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

	private AutoResult voteForPlayer(GameContext context, BingoSession session, String playerName, String category, String voteFor) {
		PlayerHandle player = context.server().getPlayerFromName(playerName);
		if (player == null) {
			return sendFailed("Player " + playerName + " could not be found.");
		}

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

	public void addGameManagerSubAction(String name, BiFunction<GameManager, String, AutoResult> action) {
		fromSession(literal(name)
				.executes(sessionNameExecutor((source, worldName, _) -> action.apply(source.gameManager(), worldName)))
		);
	}

//	public void addSessionSubAction(String name, BingoCommand.SessionActionExecutor<Source> action) {
//		then(literal(name)
//				.then(worldArg()
//						.executes(sessionExecutor(action)))
//		);
//	}

	private Command<Source> sessionExecutor(ActionExecutor2<Source, BingoSession> action) {
		return ctx -> {
			String sessionName = StringArgumentType.getString(ctx, "world");
			BingoCommandSource source = mapSource(ctx.getSource());
			Optional<BingoSession> session = source.getSessionByName(sessionName);
			if (session.isEmpty()) {
				sendFailed(source.user(), Component.text("Invalid world/ session name: " + sessionName), sessionName);
				return 0;
			}
			AutoResult result = action.execute(source.context(), session.get(), ctx);
			if (result.code() == Command.SINGLE_SUCCESS) {
				sendSuccess(source.user(), result.message(), sessionName);
			} else {
				sendFailed(source.user(), result.message(), sessionName);
			}
			return result.code();
		};
	}

	private Command<Source> settingsExecutor(ActionExecutor3<Source, BingoSettingsBuilder> action) {
		return ctx -> {
			String sessionName = StringArgumentType.getString(ctx, "world");
			BingoCommandSource source = mapSource(ctx.getSource());
			Optional<BingoSession> session = source.getSessionByName(sessionName);
			if (session.isEmpty()) {
				sendFailed(source.user(), Component.text("Invalid world/ session name: " + sessionName), sessionName);
				return 0;
			}
			AutoResult result = action.execute(session.get().settingsBuilder, ctx);
			if (result.code() == Command.SINGLE_SUCCESS) {
				sendSuccess(source.user(), result.message(), sessionName);
			} else {
				sendFailed(source.user(), result.message(), sessionName);
			}
			return result.code();
		};
	}

	private Command<Source> sessionNameExecutor(ActionExecutor2<Source, String> action) {
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

	private <Arg> void basicSetting(String settingName, String argName, ArgumentType<Arg> argType, BiFunction<CommandContext<Source>, String, Arg> argExtractor, SettingsActionExecutor<Arg> action) {
		fromSession(literal(settingName)
				.then(argument(argName, argType)
						.executes(settingsExecutor((settings, args) -> {
							Arg argument = argExtractor.apply(args, argName);
							return action.execute(settings, argument);
						}))
				)
		);
	}

	private ArgumentBuilder<Source, ?> addPresetOption(String optionName, PresetCallback callback) {
		return literal(optionName)
				.then(argument("preset", StringArgumentType.greedyString())
						.suggests((context, builder) -> {
							return suggestAll(builder, new BingoSettingsData().getPresetNames());
						})
						.executes(settingsExecutor((settings, args) -> {
							String preset = StringArgumentType.getString(args, "preset");
							return callback.execute(settings, new BingoSettingsData(), preset);
						})));
	}

	private ArgumentBuilder<Source, ?> addPlayerDataOption(String optionName, PlayerDataCallback callback) {
		return literal(optionName)
				.then(playerArg("player_name")
						.executes(sessionExecutor((context, session, args) -> {
							String playerName = playerArgValue("player_name", args);
							PlayerHandle player = context.server().getPlayerFromName(playerName);
							if (player == null) {
								return sendFailed("Cannot edit player data, player " + playerName + " not found");
							}
							return callback.execute(player, context.gameManager().getPlayerData(), playerName);
						})));
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

	private ArgumentBuilder<Source, ?> teamArg() {
		return argument("team_id", StringArgumentType.word())
				.suggests((context, builder) -> {
					BingoCommandSource source = mapSource(context.getSource());
					return suggestAll(builder, source.getSession()
							.map(s -> s.teamManager.getActiveTeams().getTeams())
							.orElse(Set.of()), BingoTeam::toString);
				});
	}

	private String teamArgValue(CommandContext<Source> context) {
		return StringArgumentType.getString(context, "team_id");
	}

	private CompletableFuture<Suggestions> allWorlds(final CommandContext<Source> context, final SuggestionsBuilder builder) {
		BingoCommandSource source = mapSource(context.getSource());
		return suggestAll(builder, source.context().server().getLoadedWorlds(), w -> w.key().asString());
	}

	public record AutoResult(int code, Component message) {

		public AutoResult(int code, String message) {
			this(code, Component.text(message));
		}
	}

	@FunctionalInterface
	public interface SettingsActionExecutor<ArgType> {

		AutoResult execute(BingoSettingsBuilder settings, ArgType arg);
	}

	@FunctionalInterface
	public interface ActionExecutor2<Source, ArgType> {

		AutoResult execute(GameContext context, ArgType arg, CommandContext<Source> args);
	}

	@FunctionalInterface
	public interface ActionExecutor3<Source, ArgType> {

		AutoResult execute(ArgType arg, CommandContext<Source> args);
	}

	@FunctionalInterface
	public interface PresetCallback {

		AutoResult execute(BingoSettingsBuilder builder, BingoSettingsData data, String presetPath);
	}

	@FunctionalInterface
	public interface PlayerDataCallback {

		AutoResult execute(PlayerHandle player, PlayerSerializationData playerData, String playerName);
	}

}
