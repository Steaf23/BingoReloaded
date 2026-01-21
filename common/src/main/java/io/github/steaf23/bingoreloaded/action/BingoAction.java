package io.github.steaf23.bingoreloaded.action;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoLobbyData;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.BingoStatData;
import io.github.steaf23.bingoreloaded.data.CustomKitData;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.GameManager;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.gameloop.phase.PregameLobby;
import io.github.steaf23.bingoreloaded.item.GoUpWand;
import io.github.steaf23.bingoreloaded.lib.action.ActionArgument;
import io.github.steaf23.bingoreloaded.lib.action.ActionResult;
import io.github.steaf23.bingoreloaded.lib.action.ActionTree;
import io.github.steaf23.bingoreloaded.lib.api.ActionUser;
import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import io.github.steaf23.bingoreloaded.lib.api.ServerSoftware;
import io.github.steaf23.bingoreloaded.lib.api.WorldPosition;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.util.ComponentUtils;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.settings.CustomKit;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.bingoreloaded.util.BingoPlayerSender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class BingoAction extends ActionTree {

	private final BingoConfigurationData config;
	private final GameManager gameManager;
	private final BingoReloaded bingo;
	private final BingoLobbyData lobbyData;

	public BingoAction(BingoReloaded bingo, BingoConfigurationData config, GameManager gameManager) {
		super("bingo", List.of("bingo.player"));
		this.config = config;
		this.bingo = bingo;
		this.gameManager = gameManager;
		this.lobbyData = new BingoLobbyData();

		setAction((user, args) -> {
			BingoSession session = getSessionFromUser(user);
			if (session == null) {
				return ActionResult.IGNORED;
			}
			BingoReloaded.runtime().openBingoMenu((PlayerHandle) user, session);
			return ActionResult.SUCCESS;
		});


		this.addSessionSubAction("vote", List.of(), (user, session, args) -> {
			if (!(session.phase() instanceof PregameLobby lobby)) {
				return ActionResult.IGNORED;
			}
			if (!config.getOptionValue(BingoOptions.USE_VOTE_SYSTEM) ||
					config.getOptionValue(BingoOptions.VOTE_USING_COMMANDS_ONLY) ||
					config.getOptionValue(BingoOptions.VOTE_LIST).isEmpty()) {
				BingoPlayerSender.sendMessage(Component.text("Voting is disabled!").color(NamedTextColor.RED), user);
				return ActionResult.IGNORED;
			}

			if (!(user instanceof PlayerHandle player)) {
				return ActionResult.IGNORED;
			}

			BingoReloaded.runtime().openVoteMenu(player, lobby);

			return ActionResult.SUCCESS;
		});


		this.addSessionSubAction("join", List.of(), (user, session, args) -> {
			if (!(user instanceof PlayerHandle player)) {
				return ActionResult.IGNORED;
			}

			BingoReloaded.runtime().openTeamSelector(player, session);
			return ActionResult.SUCCESS;
		});


		this.addSessionSubAction("leave", List.of(), (user, session, args) -> {
			if (!(user instanceof PlayerHandle player)) {
				return ActionResult.IGNORED;
			}

			BingoParticipant participant = session.teamManager.getPlayerAsParticipant(player);
			if (participant != null) {
				session.removeParticipant(participant);
				return ActionResult.SUCCESS;
			}
			return ActionResult.IGNORED;
		});


		this.addSessionSubAction("getcard", List.of(), (user, session, args) -> {
			if (!(user instanceof PlayerHandle player)) {
				return ActionResult.IGNORED;
			}

			if (session.canPlayersViewCard()) {
				BingoParticipant participant = session.teamManager.getPlayerAsParticipant(player);
				if (participant instanceof BingoPlayer bingoPlayer) {
					int cardSlot = session.settingsBuilder.view().kit().getCardSlot();
					BingoGame game = (BingoGame) session.phase();
					game.returnCardToPlayer(player.world(), cardSlot, bingoPlayer);
				}
				return ActionResult.SUCCESS;
			} else {
				return ActionResult.IGNORED;
			}
		});


		this.addSessionSubAction("back", List.of(), (user, session, args) -> {
			if (!(user instanceof PlayerHandle player)) {
				return ActionResult.IGNORED;
			}

			if (session.isRunning()) {
				if (config.getOptionValue(BingoOptions.TELEPORT_AFTER_DEATH)) {
					((BingoGame) session.phase()).teleportPlayerAfterDeath(player);
					return ActionResult.SUCCESS;
				}
			}
			return ActionResult.IGNORED;
		});


		this.addSessionSubAction("view", List.of(), (user, session, args) -> {
			if (!user.hasPermission("bingo.admin") && !config.getOptionValue(BingoOptions.ALLOW_VIEWING_ALL_CARDS)) {
				return ActionResult.NO_PERMISSION;
			}

			showTeamCardsToUser(user, session);
			return ActionResult.SUCCESS;
		});


		this.addSessionSubAction("about", List.of(), (user, session, args) -> {
			ServerSoftware server = PlatformResolver.get();
			user.sendMessage(Component.text("\nBingo Reloaded Version: " + server.getExtensionInfo().version() +
					" Created by: " + server.getExtensionInfo().authors()));
			user.sendMessage(BingoMessage.createInfoUrlComponent(Component.text("\nJoin the bingo reloaded discord server here to stay up to date!"), "https://discord.gg/AzZNxPRNPf"));
			user.sendMessage(BingoMessage.createInfoUrlComponent(Component.text("\nClick here to download the Bingo Reloaded Companion mod if you play bingo!").color(NamedTextColor.DARK_GREEN), "https://modrinth.com/mod/bingo-reloaded-companion"));
			return ActionResult.SUCCESS;
		});


		this.addSubAction(new ActionTree("reload", List.of("bingo.admin"), (user, args) -> {
			if (args.length == 1) {
				return reloadCommand(args[0], user);
			} else {
				return reloadCommand("all", user);
			}
		}).addArgument(ActionArgument.optional("option", List.of(
				"all",
				"config",
				"worlds",
				"placeholders",
				"scoreboards",
				"data",
				"language",
				"sounds"
		))));


		this.addSessionSubAction("start", List.of("bingo.admin"), (user, session, args) -> {
					if (args.length == 0) {
						session.startGame();
						return ActionResult.SUCCESS;
					} else if (args.length == 1 && args[0].equals("here")) {
						if (!(user instanceof PlayerHandle player)) {
							return ActionResult.INCORRECT_USE;
						}

						WorldPosition pos = player.position();
						session.startGame(pos);
						return ActionResult.SUCCESS;
					} else if (args.length == 2 && args[0].equals("here")) {
						if (!(user instanceof PlayerHandle player)) {
							return ActionResult.INCORRECT_USE;
						}

						int seed = Integer.parseInt(args[1]);
						session.settingsBuilder.cardSeed(seed);

						WorldPosition pos = player.position();
						session.startGame(pos);
						return ActionResult.SUCCESS;
					} else if (args.length == 1) {

						int seed = Integer.parseInt(args[0]);
						session.settingsBuilder.cardSeed(seed);

						session.startGame();
						return ActionResult.SUCCESS;
					}

					return ActionResult.INCORRECT_USE;
				})
				.addArgument(ActionArgument.optional("here", List.of("here")))
				.addArgument(ActionArgument.optional("seed"));


		this.addSessionSubAction("end", List.of("bingo.admin"), (user, session, args) -> {
			session.endGame();
			return ActionResult.SUCCESS;
		});


		this.addSessionSubAction("wait", List.of("bingo.admin"), (user, session, args) -> {
			session.pauseAutomaticStart();
			BingoPlayerSender.sendMessage(Component.text("Toggled automatic starting timer"), user);
			return ActionResult.SUCCESS;
		});


		this.addSessionSubAction("deathmatch", List.of("bingo.admin"), (user, session, args) -> {

			if (!session.isRunning()) {
				BingoMessage.NO_DEATHMATCH.sendToAudience(user, NamedTextColor.RED);
				return ActionResult.IGNORED;
			}

			((BingoGame) session.phase()).startDeathMatch(3);
			return ActionResult.SUCCESS;
		});

		this.addSessionSubAction("creator", List.of("bingo.admin"), (user, session, args) -> {
			if (!(user instanceof PlayerHandle player)) {
				return ActionResult.IGNORED;
			}

			BingoReloaded.runtime().openBingoCreator(player);
			return ActionResult.SUCCESS;
		});


		this.addSessionSubAction("stats", List.of("bingo.admin"), (user, session, args) -> {
			if (!config.getOptionValue(BingoOptions.SAVE_PLAYER_STATISTICS)) {
				Component text = Component.text("Player statistics are not being tracked at this moment!")
						.color(NamedTextColor.RED);
				BingoPlayerSender.sendMessage(text, user);
				return ActionResult.IGNORED;
			}
			BingoStatData statsData = new BingoStatData(gameManager.getPlatform());
			Component msg;
			if (args.length > 1 && user.hasPermission("bingo.admin")) {
				msg = statsData.getPlayerStatsFormatted(args[1]);
			} else {
				if (!(user instanceof PlayerHandle player)) {
					return ActionResult.IGNORED;
				}

				msg = statsData.getPlayerStatsFormatted(player.uniqueId());
			}
			BingoPlayerSender.sendMessage(msg, user);
			return ActionResult.SUCCESS;
		});


		ActionTree addKitAction = new ActionTree("add", (user, args) -> {
			if (args.length < 1) {
				return ActionResult.INCORRECT_USE;
			}

			if (args.length < 2) {
				BingoPlayerSender.sendMessage(Component.text("Please specify a kit name for slot " + args[0]).color(NamedTextColor.RED), user);
				return ActionResult.INCORRECT_USE;
			}

			if (!(user instanceof PlayerHandle player)) {
				return ActionResult.IGNORED;
			}
			addPlayerKit(args[0], String.join(" ", Arrays.copyOfRange(args, 1, args.length)), player);
			return ActionResult.SUCCESS;
		})
				.addArgument(ActionArgument.required("slot", List.of("1", "2", "3", "4", "5")).withUseDisplay(ActionArgument.UseDisplay.OPTIONS))
				.addArgument(ActionArgument.required("name"));


		ActionTree removeKitAction = new ActionTree("remove", (user, args) -> removePlayerKit(user, args[0]))
				.addArgument(ActionArgument.required("slot", List.of("1", "2", "3", "4", "5")).withUseDisplay(ActionArgument.UseDisplay.OPTIONS));


		ActionTree itemKitAction = new ActionTree("item", (user, args) -> {
			if (!(user instanceof PlayerHandle player)) {
				return ActionResult.IGNORED;
			}
			return giveUserBingoItem(player, args[0]);
		})
				.addArgument(ActionArgument.required("item_name", List.of("wand", "card")));


		this.addSubAction(new ActionTree("kit", List.of("bingo.admin"))
				.addSubAction(addKitAction)
				.addSubAction(removeKitAction)
				.addSubAction(itemKitAction));


		this.addSessionSubAction("teamedit", List.of("bingo.admin"), (user, session, args) -> {
			if (!(user instanceof PlayerHandle player)) {
				return ActionResult.IGNORED;
			}

			BingoReloaded.runtime().openTeamEditor(player);
			return ActionResult.SUCCESS;
		});


		this.addSessionSubAction("teams", List.of("bingo.admin"), (user, session, args) -> {
			BingoPlayerSender.sendMessage(Component.text("Here are all the teams with at least 1 player:"), user);
			session.teamManager.getActiveTeams().getTeams().forEach(team -> {
				if (team.getMembers().isEmpty()) {
					return;
				}
				user.sendMessage(Component.text(" - ").append(team.getColoredName()).append(Component.text(": ")
						.append(Component.join(JoinConfiguration.separator(Component.text(", ")),
								team.getMembers().stream()
										.map(BingoParticipant::getDisplayName)
										.toList()))));
			});
			return ActionResult.SUCCESS;
		});

		ActionTree createLobbyAction = new ActionTree("create", (user, args) -> {
			if (!(user instanceof PlayerHandle player)) {
				return ActionResult.IGNORED;
			}

			WorldPosition pos = player.position();
			gameManager.getLobbyData().create(pos);
			BingoPlayerSender.sendMessage(ComponentUtils.MINI_BUILDER.deserialize("<green>Created a lobby spawn point at this position.\nPlayers can be teleported here using the option <dark_green>teleportToLobbyAfterGame</dark_green>.</green>"), player);

			return ActionResult.SUCCESS;
		});

		ActionTree removeLobbyAction = new ActionTree("remove", (user, args) -> {
			if (!gameManager.getLobbyData().isEnabled()) {
				BingoPlayerSender.sendMessage(ComponentUtils.MINI_BUILDER.deserialize("<red>A lobby has not been created yet.</red>\n<yellow>Tip: </yellow><italic>Use <aqua>/bingo lobby create</aqua> to create a lobby spawn point at your current position.</italic>"), user);
				return ActionResult.IGNORED;
			}
			gameManager.getLobbyData().remove();
			BingoPlayerSender.sendMessage(ComponentUtils.MINI_BUILDER.deserialize("<green>Removed the created lobby.</green>\n<yellow>Tip: </yellow><italic>Use <aqua>/bingo lobby create</aqua> to create a lobby spawn point at your current position.</italic>"), user);
			return ActionResult.SUCCESS;
		});

		this.addSubAction(new ActionTree("lobby", List.of("bingo.admin"))
				.addSubAction(createLobbyAction)
				.addSubAction(removeLobbyAction));
	}

	public void addPlayerKit(String slot, String kitName, PlayerHandle fromPlayerInventory) {
		PlayerKit kit = switch (slot) {
			case "1" -> PlayerKit.CUSTOM_1;
			case "2" -> PlayerKit.CUSTOM_2;
			case "3" -> PlayerKit.CUSTOM_3;
			case "4" -> PlayerKit.CUSTOM_4;
			case "5" -> PlayerKit.CUSTOM_5;
			default -> {
				BingoPlayerSender.sendMessage(Component.text("Invalid slot, please pick a slot from 1 through 5 to save this kit in").color(NamedTextColor.RED), fromPlayerInventory);
				yield null;
			}
		};
		if (kit == null) {
			return;
		}

		CustomKitData data = new CustomKitData();
		if (!data.assignCustomKit(ComponentUtils.MINI_BUILDER.deserialize(kitName), kit, fromPlayerInventory)) {
			Component message = ComponentUtils.MINI_BUILDER
					.deserialize("<red>Cannot add custom kit " + kitName + " to slot " + slot + ", this slot already contains kit ")
					.append(data.getCustomKit(kit).name())
					.append(Component.text(". Remove it first!"));
			BingoPlayerSender.sendMessage(message, fromPlayerInventory);
		} else {
			Component message = ComponentUtils.MINI_BUILDER
					.deserialize("<green>Created custom kit " + kitName + " in slot " + slot + " from your inventory");
			BingoPlayerSender.sendMessage(message, fromPlayerInventory);
		}
	}

	public ActionResult removePlayerKit(ActionUser user, String slot) {
		PlayerKit kit = switch (slot) {
			case "1" -> PlayerKit.CUSTOM_1;
			case "2" -> PlayerKit.CUSTOM_2;
			case "3" -> PlayerKit.CUSTOM_3;
			case "4" -> PlayerKit.CUSTOM_4;
			case "5" -> PlayerKit.CUSTOM_5;
			default -> {
				BingoPlayerSender.sendMessage(Component.text("Invalid slot, please a slot from 1 through 5 to save this kit in").color(NamedTextColor.RED), user);
				yield null;
			}
		};
		if (kit == null) {
			return ActionResult.INCORRECT_USE;
		}

		CustomKitData data = new CustomKitData();
		CustomKit customKit = data.getCustomKit(kit);
		if (customKit == null) {
			Component message = ComponentUtils.MINI_BUILDER
					.deserialize("<red>Cannot remove kit from slot " + slot + " because no custom kit is assigned to this slot");
			BingoPlayerSender.sendMessage(message, user);
		} else {
			data.removeCustomKit(kit);

			Component message = ComponentUtils.MINI_BUILDER
					.deserialize("<green>Removed custom kit " + ComponentUtils.MINI_BUILDER.serialize(customKit.name()) + " from slot " + slot);
			BingoPlayerSender.sendMessage(message, user);
		}

		return ActionResult.SUCCESS;
	}

	public ActionResult giveUserBingoItem(PlayerHandle player, String itemName) {
		BingoSession session = getSessionFromUser(player);
		if (session == null) {
			return ActionResult.IGNORED;
		}

		return switch (itemName) {
			case "wand" -> {
				player.inventory().addItem(session.items().createStack(GoUpWand.ID));
				yield ActionResult.SUCCESS;
			}
			case "card" -> {
				player.inventory().addItem(PlayerKit.CARD_ITEM.buildItem());
				yield ActionResult.SUCCESS;
			}
			default -> ActionResult.INCORRECT_USE;
		};
	}

	public void showTeamCardsToUser(ActionUser user, BingoSession session) {
		if (!session.canPlayersViewCard()) {
			return;
		}

		if (!(user instanceof PlayerHandle player)) {
			return;
		}

		BingoReloaded.runtime().openTeamCardSelect(player, session);
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

	public ActionResult reloadCommand(String reloadOption, ActionUser user) {
		switch (reloadOption) {
			case "all" -> reloadAll();
			case "config" -> reloadConfig();
			case "worlds" -> reloadWorlds();
			case "placeholders" -> reloadPlaceholders();
			case "scoreboards" -> reloadScoreboards();
			case "data" -> reloadData();
			case "language" -> reloadLanguage();
			case "sounds" -> reloadSounds();
			default -> {
				BingoPlayerSender.sendMessage(Component.text("Cannot reload '" + reloadOption + "', invalid option"), user);
				return ActionResult.INCORRECT_USE;
			}
		}

		BingoPlayerSender.sendMessage(Component.text("Reloaded " + reloadOption), user);
		return ActionResult.SUCCESS;
	}

	public void reloadAll() {
		reloadConfig();
		reloadPlaceholders();
		reloadScoreboards();
		reloadData();
		reloadLanguage();
		reloadSounds();

		// reload worlds last to kick off everything else.
		reloadWorlds();
	}

	public void reloadConfig() {
		bingo.reloadConfigFromFile();
	}

	public void reloadWorlds() {
		bingo.reloadManager();
	}

	public void reloadPlaceholders() {
		bingo.reloadPlaceholders();
	}

	public void reloadScoreboards() {
		bingo.reloadScoreboards();
	}

	public void reloadData() {
		bingo.reloadData();
	}

	public void reloadLanguage() {
		bingo.reloadLanguage();
	}

	public void reloadSounds() {
		bingo.reloadSounds();
	}

	public @Nullable BingoSession getSessionFromUser(ActionUser user) {
		if (user instanceof PlayerHandle player) {
			return gameManager.getSessionFromWorld(player.world());
		}

		return null;
	}

	public ActionTree addSessionSubAction(String name, List<String> permissions, SessionCommandFunction action) {
		return addSubAction(new ActionTree(name, permissions, (user, args) -> {
			BingoSession session = getSessionFromUser(user);
			if (session == null) {
				return ActionResult.IGNORED;
			} else {
				return action.perform(user, session, args);
			}
		}));
	}

	@FunctionalInterface
	public interface SessionCommandFunction {

		ActionResult perform(ActionUser user, BingoSession session, String... arguments);
	}
}
