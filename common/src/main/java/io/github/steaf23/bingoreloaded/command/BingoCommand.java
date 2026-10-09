package io.github.steaf23.bingoreloaded.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.api.BingoCommandSource;
import io.github.steaf23.bingoreloaded.data.BingoLobbyData;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.BingoStatData;
import io.github.steaf23.bingoreloaded.data.CustomKitData;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.data.teleportgrid.TeleportGridData;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.GameManager;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.gameloop.phase.PregameLobby;
import io.github.steaf23.bingoreloaded.item.EndlessPearl;
import io.github.steaf23.bingoreloaded.item.GoUpWand;
import io.github.steaf23.bingoreloaded.item.TeamPouch;
import io.github.steaf23.bingoreloaded.item.TeamTeleporter;
import io.github.steaf23.bingoreloaded.lib.api.ActionUser;
import io.github.steaf23.bingoreloaded.lib.api.BingoReloadedRuntime;
import io.github.steaf23.bingoreloaded.lib.api.GlobalPosition;
import io.github.steaf23.bingoreloaded.lib.api.platform.GameContext;
import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformServer;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.protocol.message.MessageParser;
import io.github.steaf23.bingoreloaded.settings.CustomKit;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.bingoreloaded.tasks.data.ItemTask;
import io.github.steaf23.bingoreloaded.util.BingoPlayerSender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiFunction;

public class BingoCommand<Source> extends MappedCommand<Source> {

	private final BingoConfigurationData config;
	private final BingoLobbyData lobbyData;

	public BingoCommand(BingoConfigurationData config, Settings<Source> settings) {
		super("bingo", List.of("bingo.player"), settings);
		this.config = config;
		this.lobbyData = new BingoLobbyData();

		execute(this::bingo);

		then(literal("vote").executes(sessionExecutor(this::vote)));

		this.addSessionSubAction("join", (source, session) -> {
			if (!(source.user() instanceof PlayerHandle player)) {
				return 0;
			}

			source.context().runtime().openTeamSelector(player, session);
			return Command.SINGLE_SUCCESS;
		});


		this.addSessionSubAction("leave", (source, session) -> {
			if (!(source.user() instanceof PlayerHandle player)) {
				return 0;
			}

			BingoParticipant participant = session.teamManager.getPlayerAsParticipant(player);
			if (participant != null) {
				session.removeParticipant(participant);
				return Command.SINGLE_SUCCESS;
			}
			return 0;
		});


		this.addSessionSubAction("getcard", (source, session) -> {
			if (!(source.user() instanceof PlayerHandle player)) {
				return 0;
			}

			if (session.canPlayersViewCard()) {
				BingoParticipant participant = session.teamManager.getPlayerAsParticipant(player);
				if (participant instanceof BingoPlayer bingoPlayer) {
					int cardSlot = session.settingsBuilder.view().kit().getCardSlot();
					BingoGame game = (BingoGame) session.phase();
					game.returnCardToPlayer(cardSlot, bingoPlayer);
				}
				return Command.SINGLE_SUCCESS;
			} else {
				return 0;
			}
		});


		this.addSessionSubAction("back", (source, session) -> {
			if (!(source.user() instanceof PlayerHandle player)) {
				return 0;
			}

			if (session.isRunning()) {
				if (this.config.getOptionValue(BingoOptions.TELEPORT_AFTER_DEATH)) {
					((BingoGame) session.phase()).teleportPlayerAfterDeath(player);
					return Command.SINGLE_SUCCESS;
				}
			}
			return 0;
		});


		this.addSessionSubAction("view", (source, session) -> {
			if (source.user() instanceof PlayerHandle player) {
				BingoParticipant participant = session.teamManager.getPlayerAsParticipant(player);
				if (participant != null) {
					if (session.phase() instanceof BingoGame game) {
						if (game.getDeathMatchTask() != null && game.getDeathMatchTask().data() instanceof ItemTask itemTask) {
							participant.showCard(itemTask);
							return Command.SINGLE_SUCCESS;
						}
						participant.showCard(null);
					}
					return Command.SINGLE_SUCCESS;
				}
			}
			if (!BingoReloaded.isAdmin(source.user()) && !this.config.getOptionValue(BingoOptions.ALLOW_VIEWING_ALL_CARDS)) {
				return 0;
			}

			showTeamCardsToUser(source.context().runtime(), source.user(), session);
			return Command.SINGLE_SUCCESS;
		});


		then(literal("about").executes(executor((source) -> {
			source.user().sendMessage(Component.text("\nBingo Reloaded Version: " + BingoReloaded.getMetaInfo().version() +
					" Created by: " + BingoReloaded.getMetaInfo().authors()));
			source.user().sendMessage(BingoMessage.createInfoUrlComponent(Component.text("\nJoin the bingo reloaded discord server here to stay up to date!"), "https://discord.gg/AzZNxPRNPf"));
			source.user().sendMessage(BingoMessage.createInfoUrlComponent(Component.text("\nClick here to download the Bingo Reloaded Companion mod if you play bingo!").color(NamedTextColor.DARK_GREEN), "https://modrinth.com/mod/bingo-reloaded-companion"));
			return Command.SINGLE_SUCCESS;
		})));

		then(literal("reload")
				.then(reloadOption("all"))
				.then(reloadOption("worlds"))
				.then(reloadOption("placeholders"))
				.then(reloadOption("scoreboards"))
				.then(reloadOption("data"))
				.then(reloadOption("language"))
		);

		then(literal("leaderboard").executes(executor(source -> {
			if (!(source.user() instanceof PlayerHandle player)) {
				return 0;
			}

			if (this.config.getOptionValue(BingoOptions.LEADERBOARD_ENABLED)) {
				source.context().runtime().openLeaderboard(
						player,
						source.gameManager().getLeaderboard(),
						this.config.getOptionValue(BingoOptions.LEADERBOARD_USE_PRESETS)
				);
				return Command.SINGLE_SUCCESS;
			}
			return 0;
		})));

		then(literal("start")
				.requires(permissionRequirement(List.of("bingo.host")))
				.executes(sessionExecutor((source, args, session) -> {
					return start(source, session, false, 0);
				}))
				.then(literal("here")
						.executes(sessionExecutor(((source, args, session) -> {
							return start(source, session, true, 0);
						})))
						.then(argument("seed", IntegerArgumentType.integer())
								.executes(sessionExecutor(((source, args, session) -> {
									int seed = IntegerArgumentType.getInteger(args, "seed");
									return start(source, session, true, seed);
								})))))
				.then(argument("seed", IntegerArgumentType.integer())
						.executes(sessionExecutor(((source, args, session) -> {
							int seed = IntegerArgumentType.getInteger(args, "seed");
							return start(source, session, false, seed);
						}))))
		);

		this.addSessionSubAction("end", List.of("bingo.host"), (args, session) -> {
			session.endGame();
			return Command.SINGLE_SUCCESS;
		});

		this.addSessionSubAction("wait", List.of("bingo.host"), (source, session) -> {
			session.pauseAutomaticStart();
			BingoPlayerSender.sendMessage(Component.text("Toggled automatic starting timer"), source.user());
			return Command.SINGLE_SUCCESS;
		});

		this.addSessionSubAction("deathmatch", List.of("bingo.host"), (source, session) -> {

			if (!session.isRunning()) {
				BingoMessage.NO_DEATHMATCH.sendToAudience(source.user(), NamedTextColor.RED);
				return 0;
			}

			((BingoGame) session.phase()).startDeathMatch(3);
			return Command.SINGLE_SUCCESS;
		});

		this.addSessionSubAction("creator", List.of("bingo.admin"), (source, session) -> {
			if (!(source.user() instanceof PlayerHandle player)) {
				return 0;
			}

			source.context().runtime().openBingoCreator(player);
			return Command.SINGLE_SUCCESS;
		});

		then(literal("stats")
				.executes(executor(source -> stats(source, "")))
				.then(argument("player_name", StringArgumentType.word())
						.requires(permissionRequirement(List.of("bingo.admin")))
						.executes(ctx -> {
							BingoCommandSource source = mapSource(ctx.getSource());
							String player = StringArgumentType.getString(ctx, "player_name");
							return stats(source, player);
						}))
		);

		then(List.of("bingo.admin"), literal("kit")
				.then(literal("add")
						.then(argument("slot", IntegerArgumentType.integer(1, 5))
								.then(argument("name", StringArgumentType.greedyString())
										.executes(ctx -> {
											int slot = IntegerArgumentType.getInteger(ctx, "slot");
											String name = StringArgumentType.getString(ctx, "name");
											return addPlayerKit(slot, name, (PlayerHandle)mapSource(ctx.getSource()).user());
										}))
						)
				)
				.then(literal("remove")
						.then(argument("slot", IntegerArgumentType.integer(1, 5))
								.executes(ctx -> {
									return removePlayerKit(IntegerArgumentType.getInteger(ctx, "slot"), mapSource(ctx.getSource()).user());
								}))
				)
				.then(literal("item")
						.then(kitItemOption("card"))
						.then(kitItemOption("wand"))
						.then(kitItemOption("pouch"))
						.then(kitItemOption("pearl"))
						.then(kitItemOption("teleporter")))
		);


		this.addSessionSubAction("teamedit", List.of("bingo.admin"), (source, session) -> {
			if (!(source.user() instanceof PlayerHandle player)) {
				return 0;
			}

			source.context().runtime().openTeamEditor(player);
			return Command.SINGLE_SUCCESS;
		});


		this.addSessionSubAction("teams", List.of("bingo.host"), (source, session) -> {
			BingoPlayerSender.sendMessage(Component.text("Here are all the teams with at least 1 player:"), source.user());
			session.teamManager.getActiveTeams().getTeams().forEach(team -> {
				if (team.getMembers().isEmpty()) {
					return;
				}
				source.user().sendMessage(Component.text(" - ").append(team.getColoredName()).append(Component.text(": ")
						.append(Component.join(JoinConfiguration.separator(Component.text(", ")),
								team.getMembers().stream()
										.map(BingoParticipant::getDisplayName)
										.toList()))));
			});
			return Command.SINGLE_SUCCESS;
		});

		then(List.of("bingo.admin"), literal("lobby")
				.then(literal("create")
						.executes(executor(this::createLobby)))
				.then(literal("remove")
						.executes(executor(this::removeLobby)))
		);

		then(List.of("bingo.admin"), literal("grid")
				.then(literal("reset")
						.executes(executor(source -> {
							TeleportGridData data = new TeleportGridData(config.getOptionValue(BingoOptions.TELEPORTATION_GRID), new Random());
							data.reset();
							BingoPlayerSender.sendMessage(Component.text("Grid has been reset, you can now play up to " + data.getGamesLeft() + " games with these grid settings."), source.user());
							return Command.SINGLE_SUCCESS;
						})))
				.then(literal("status")
						.executes(executor(source -> {
							TeleportGridData data = new TeleportGridData(config.getOptionValue(BingoOptions.TELEPORTATION_GRID), new Random());
							BingoPlayerSender.sendMessage(Component.text("You can now play " + data.getGamesLeft() + " more games with these grid settings, before you need to reset it."), source.user());
							return Command.SINGLE_SUCCESS;
						})))
		);
	}

	public int bingo(BingoCommandSource source) {
		var session = source.getSession();
		if (session.isEmpty()) {
			return 0;
		}
		source.context().runtime().openBingoMenu((PlayerHandle) source.user(), session.orElseThrow());
		return 1;
	}

	public int vote(BingoCommandSource source, CommandContext<Source> context, BingoSession session) {
		if (!(session.phase() instanceof PregameLobby lobby)) {
			return 0;
		}
		if (!this.config.getOptionValue(BingoOptions.USE_VOTE_SYSTEM) ||
				this.config.getOptionValue(BingoOptions.VOTE_USING_COMMANDS_ONLY) ||
				this.config.getOptionValue(BingoOptions.VOTE_LIST).isEmpty()) {
			BingoPlayerSender.sendMessage(Component.text("Voting is disabled!").color(NamedTextColor.RED), source.user());
			return 0;
		}

		if (!(source.user() instanceof PlayerHandle player)) {
			return 0;
		}

		source.context().runtime().openVoteMenu(player, lobby);

		return Command.SINGLE_SUCCESS;
	}

	public int addPlayerKit(int slot, String kitName, PlayerHandle fromPlayerInventory) {
		PlayerKit kit = switch (slot) {
			case 1 -> PlayerKit.CUSTOM_1;
			case 2 -> PlayerKit.CUSTOM_2;
			case 3 -> PlayerKit.CUSTOM_3;
			case 4 -> PlayerKit.CUSTOM_4;
			case 5 -> PlayerKit.CUSTOM_5;
			default -> {
				BingoPlayerSender.sendMessage(Component.text("Invalid slot, please pick a slot from 1 through 5 to save this kit in").color(NamedTextColor.RED), fromPlayerInventory);
				yield null;
			}
		};
		if (kit == null) {
			return 0;
		}

		CustomKitData data = new CustomKitData();
		if (!data.assignCustomKit(MessageParser.MINI_BUILDER.deserialize(kitName), kit, fromPlayerInventory)) {
			Component message = MessageParser.MINI_BUILDER
					.deserialize("<red>Cannot add custom kit " + kitName + " to slot " + slot + ", this slot already contains kit ")
					.append(data.getCustomKit(kit).orElseThrow().name())
					.append(Component.text(". Remove it first!"));
			BingoPlayerSender.sendMessage(message, fromPlayerInventory);
			return 0;
		} else {
			Component message = MessageParser.MINI_BUILDER
					.deserialize("<green>Created custom kit " + kitName + " in slot " + slot + " from your inventory");
			BingoPlayerSender.sendMessage(message, fromPlayerInventory);
		}

		return Command.SINGLE_SUCCESS;
	}

	public int removePlayerKit(int slot, ActionUser user) {
		PlayerKit kit = switch (slot) {
			case 1 -> PlayerKit.CUSTOM_1;
			case 2 -> PlayerKit.CUSTOM_2;
			case 3 -> PlayerKit.CUSTOM_3;
			case 4 -> PlayerKit.CUSTOM_4;
			case 5 -> PlayerKit.CUSTOM_5;
			default -> {
				BingoPlayerSender.sendMessage(Component.text("Invalid slot, please a slot from 1 through 5 to save this kit in").color(NamedTextColor.RED), user);
				yield null;
			}
		};
		if (kit == null) {
			return 0;
		}

		CustomKitData data = new CustomKitData();
		Optional<CustomKit> customKit = data.getCustomKit(kit);
		if (customKit.isEmpty()) {
			Component message = MessageParser.MINI_BUILDER
					.deserialize("<red>Cannot remove kit from slot " + slot + " because no custom kit is assigned to this slot");
			BingoPlayerSender.sendMessage(message, user);
		} else {
			data.removeCustomKit(kit);

			Component message = MessageParser.MINI_BUILDER
					.deserialize("<green>Removed custom kit " + MessageParser.MINI_BUILDER.serialize(customKit.orElseThrow().name()) + " from slot " + slot);
			BingoPlayerSender.sendMessage(message, user);
		}

		return Command.SINGLE_SUCCESS;
	}


	public int kitItem(BingoCommandSource source, BingoSession session, String itemName) {
		return giveUserBingoItem(source.context(), session, (PlayerHandle)source.user(), itemName);
	}

	public int giveUserBingoItem(GameContext context, BingoSession session, PlayerHandle player, String itemName) {
		return switch (itemName) {
			case "card" -> {
				player.addItemsToInventory(PlayerKit.CARD_ITEM.buildItem(context.server()));
				yield Command.SINGLE_SUCCESS;
			}
			case "wand" -> {
				player.addItemsToInventory(session.items().createStack(GoUpWand.ID, null));
				yield Command.SINGLE_SUCCESS;
			}
			case "pouch" -> {
				player.addItemsToInventory(session.items().createStack(TeamPouch.ID, null));
				yield Command.SINGLE_SUCCESS;
			}
			case "pearl" -> {
				player.addItemsToInventory(session.items().createStack(EndlessPearl.ID, null));
				yield Command.SINGLE_SUCCESS;
			}
			case "teleporter" -> {
				player.addItemsToInventory(session.items().createStack(TeamTeleporter.ID, null));
				yield Command.SINGLE_SUCCESS;
			}
			//Unreachable
			default -> 0;
		};
	}

	public void showTeamCardsToUser(BingoReloadedRuntime runtime, ActionUser user, BingoSession session) {
		if (!session.canPlayersViewCard()) {
			return;
		}

		if (!(user instanceof PlayerHandle player)) {
			return;
		}

		runtime.openTeamCardSelect(player, session);
	}

	/**
	 * @return Integer the string represents or defaultValue if a conversion failed.
	 */
	public static int toInt(String in, int defaultValue) {
		try {
			return Integer.parseInt(in);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public int reload(BingoCommandSource source, String option) {
		return reloadCommand(source.context(), option, source.user());
	}

	public int reloadCommand(GameContext context, String reloadOption, ActionUser user) {
		BingoReloaded bingo = context.bingo();
		switch (reloadOption) {
			case "all" -> reloadAll(bingo, context.server());
			case "worlds" -> bingo.reloadManager(context.server());
			case "placeholders" -> bingo.reloadPlaceholders();
			case "scoreboards" -> bingo.reloadScoreboards();
			case "data" -> bingo.reloadData();
			case "language" -> bingo.reloadLanguage();
			case "sounds" -> bingo.reloadSounds();
			case "taskformat" -> bingo.reloadTaskFormat();
			default -> {
				BingoPlayerSender.sendMessage(Component.text("Cannot reload '" + reloadOption + "', invalid option"), user);
				return 0;
			}
		}

		BingoPlayerSender.sendMessage(Component.text("Reloaded " + reloadOption), user);
		return Command.SINGLE_SUCCESS;
	}

	public void reloadAll(BingoReloaded bingo, PlatformServer server) {
		bingo.reloadPlaceholders();
		bingo.reloadScoreboards();
		bingo.reloadData();
		bingo.reloadLanguage();
		bingo.reloadSounds();
		bingo.reloadTaskFormat();

		// reload worlds last to kick off everything else.
		bingo.reloadManager(server);
	}

	public int start(BingoCommandSource source, BingoSession session, boolean here, int seed) {
		if (here && !(source.user() instanceof PlayerHandle)) {
			return 0;
		}

		if (seed != 0) {
			session.settingsBuilder.cardSeed(seed);
		}

		if (here) {
			GlobalPosition pos = ((PlayerHandle)source.user()).position();
			session.startGame(pos);
		} else {
			session.startGame();
		}

		return Command.SINGLE_SUCCESS;
	}

	public int stats(BingoCommandSource source, String forPlayer) {
		if (!this.config.getOptionValue(BingoOptions.SAVE_PLAYER_STATISTICS)) {
			Component text = Component.text("Player statistics are not being tracked at this moment!")
					.color(NamedTextColor.RED);
			BingoPlayerSender.sendMessage(text, source.user());
			return 0;
		}
		BingoStatData statsData = new BingoStatData(source.gameManager().getServer());
		Component msg;
		if (!forPlayer.isEmpty()) {
			msg = statsData.getPlayerStatsFormatted(forPlayer);
		} else {
			if (!(source.user() instanceof PlayerHandle player)) {
				return 0;
			}

			msg = statsData.getPlayerStatsFormatted(player.uniqueId());
		}
		BingoPlayerSender.sendMessage(msg, source.user());
		return Command.SINGLE_SUCCESS;
	}

	public int createLobby(BingoCommandSource source) {
		if (!(source.user() instanceof PlayerHandle player)) {
			return 0;
		}

		GameManager gameManager = source.gameManager();

		GlobalPosition pos = player.position();
		Optional<BingoSession> session = source.getSession();
		// In multiple, we cannot create a lobby in a bingo world because there should only be one lobby ever.
		if (this.config.getOptionValue(BingoOptions.CONFIGURATION) == BingoOptions.PluginConfiguration.MULTIPLE && session.isPresent() && session.get().ownsWorld(player.world())) {
			BingoPlayerSender.sendMessage(MessageParser.MINI_BUILDER.deserialize("<red>Lobby cannot be created in a bingo-world. Please create it in the lobby world as defined by defaultWorldName.</red>"), player);
			return 0;
		}
		gameManager.getLobbyData().create(pos);
		BingoPlayerSender.sendMessage(MessageParser.MINI_BUILDER.deserialize("<green>Created a lobby spawn point at this position.\nPlayers can be teleported here using the option <dark_green>teleportToLobbyAfterGame</dark_green>.</green>"), player);

		return Command.SINGLE_SUCCESS;
	}

	public int removeLobby(BingoCommandSource source) {
		GameManager gameManager = source.gameManager();
		if (!gameManager.getLobbyData().isEnabled()) {
			BingoPlayerSender.sendMessage(MessageParser.MINI_BUILDER.deserialize("<red>A lobby has not been created yet.</red>\n<yellow>Tip: </yellow><italic>Use <aqua>/bingo lobby create</aqua> to create a lobby spawn point at your current position.</italic>"), source.user());
			return 0;
		}
		gameManager.getLobbyData().remove();
		BingoPlayerSender.sendMessage(MessageParser.MINI_BUILDER.deserialize("<green>Removed the created lobby.</green>\n<yellow>Tip: </yellow><italic>Use <aqua>/bingo lobby create</aqua> to create a lobby spawn point at your current position.</italic>"), source.user());
		return Command.SINGLE_SUCCESS;
	}

	public void addSessionSubAction(String name, BiFunction<BingoCommandSource, BingoSession, Integer> action) {
		then(literal(name)
				.executes(sessionExecutor((source, _, session) -> action.apply(source, session)))
		);
	}

	public void addSessionSubAction(String name, List<String> permissions, BiFunction<BingoCommandSource, BingoSession, Integer> action) {
		then(permissions, literal(name)
				.executes(sessionExecutor((source, _, session) -> action.apply(source, session)))
		);
	}

	public void addSessionSubAction(String name, SessionActionExecutor<Source> action) {
		then(literal(name)
				.executes(sessionExecutor(action))
		);
	}

	private ArgumentBuilder<Source, ?> reloadOption(String name) {
		return literal(name)
				.executes(executor(source -> reload(source, name)));
	};

	private ArgumentBuilder<Source, ?> kitItemOption(String name) {
		return literal(name)
				.executes(sessionExecutor((source, args, session) -> kitItem(source, session, name)));
	};

	private Command<Source> sessionExecutor(SessionActionExecutor<Source> executor) {
		return ctx -> {
			BingoCommandSource source = mapSource(ctx.getSource());
			Optional<BingoSession> session = source.getSession();
			if (session.isEmpty()) {
				return 0;
			}
			return executor.execute(source, ctx, session.orElseThrow());
		};
	}

	@FunctionalInterface
	public interface SessionActionExecutor<Source> {
		int execute(BingoCommandSource source, CommandContext<Source> args, BingoSession session);
	}
}
