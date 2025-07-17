package io.github.steaf23.bingoreloaded.action;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.lib.action.ActionTree;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.BingoStatData;
import io.github.steaf23.bingoreloaded.data.CustomKitData;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.GameManager;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.gameloop.phase.PregameLobby;
import io.github.steaf23.bingoreloaded.lib.api.ActionUser;
import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.ServerSoftware;
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
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class BingoAction extends ActionTree {

	private final BingoConfigurationData config;
	private final GameManager gameManager;
	private final BingoReloaded bingo;

	public BingoAction(BingoReloaded bingo, BingoConfigurationData config, GameManager gameManager) {
		super("bingo", List.of("bingo.player"));
		this.config = config;
		this.bingo = bingo;
		this.gameManager = gameManager;

		setAction((action) -> {
			BingoSession session = getSessionFromUser(getLastUser());
			if (session == null) {
				return false;
			}
			BingoReloaded.runtime().openBingoMenu((PlayerHandle)getLastUser(), session);
			return true;
		});


		this.addSessionSubAction("vote", List.of(), (args, session) -> {
			if (!(session.phase() instanceof PregameLobby lobby)) {
				return false;
			}
			if (!config.getOptionValue(BingoOptions.USE_VOTE_SYSTEM) ||
					config.getOptionValue(BingoOptions.VOTE_USING_COMMANDS_ONLY) ||
					config.getOptionValue(BingoOptions.VOTE_LIST).isEmpty()) {
				BingoPlayerSender.sendMessage(Component.text("Voting is disabled!").color(NamedTextColor.RED), getLastUser());
				return false;
			}

			if (!(getLastUser() instanceof PlayerHandle player)) {
				return false;
			}

			BingoReloaded.runtime().openVoteMenu(player, lobby);

			return true;
		});


		this.addSessionSubAction("join", List.of(), (args, session) -> {
			if (!(getLastUser() instanceof PlayerHandle player)) {
				return false;
			}

			BingoReloaded.runtime().openTeamSelector(player, session);
			return true;
		});


		this.addSessionSubAction("leave", List.of(), (args, session) -> {
			if (!(getLastUser() instanceof PlayerHandle player)) {
				return false;
			}

			BingoParticipant participant = session.teamManager.getPlayerAsParticipant(player);
			if (participant != null) {
				session.removeParticipant(participant);
				return false;
			}
			return true;
		});


		this.addSessionSubAction("getcard", List.of(), (args, session) -> {
			if (!(getLastUser() instanceof PlayerHandle player)) {
				return false;
			}

			if (session.isRunning()) {
				BingoParticipant participant = session.teamManager.getPlayerAsParticipant(player);
				if (participant instanceof BingoPlayer bingoPlayer) {
					int cardSlot = session.settingsBuilder.view().kit().getCardSlot();
					BingoGame game = (BingoGame) session.phase();
					game.returnCardToPlayer(cardSlot, bingoPlayer, null);
				}
				return true;
			} else {
				return false;
			}
		});


		this.addSessionSubAction("back", List.of(), (args, session) -> {
			if (!(getLastUser() instanceof PlayerHandle player)) {
				return false;
			}

			if (session.isRunning()) {
				if (config.getOptionValue(BingoOptions.TELEPORT_AFTER_DEATH)) {
					((BingoGame) session.phase()).teleportPlayerAfterDeath(player);
					return true;
				}
			}
			return true;
		});


		this.addSessionSubAction("view", List.of(), (args, session) -> {
			if (!getLastUser().hasPermission("bingo.admin") && !config.getOptionValue(BingoOptions.ALLOW_VIEWING_ALL_CARDS)) {
				return false;
			}

			showTeamCardsToUser(getLastUser(), session);
			return true;
		});


		this.addSessionSubAction("about", List.of(), (args, session) -> {
			ServerSoftware server = PlatformResolver.get();
			getLastUser().sendMessage(Component.text("Bingo Reloaded Version: " + server.getExtensionInfo().version() +
					" Created by: " + server.getExtensionInfo().authors()));
			getLastUser().sendMessage(BingoMessage.createInfoUrlComponent(Component.text("Join the bingo reloaded discord server here to stay up to date!"), "https://discord.gg/AzZNxPRNPf"));

			return true;
		});


		this.addSubAction(new ActionTree("reload", List.of("bingo.admin"), args -> {
			if (args.length == 2) {
				return reloadCommand(args[1], getLastUser());
			} else {
				return reloadCommand("all", getLastUser());
			}
		}))
				.addTabCompletion(args -> List.of(
						"all",
						"config",
						"worlds",
						"placeholders",
						"scoreboards",
						"data",
						"language"
				));


		this.addSessionSubAction("start", List.of("bingo.admin"), (args, session) -> {
			if (args.length > 1) {
				int seed = Integer.parseInt(args[1]);
				session.settingsBuilder.cardSeed(seed);
			}

			session.startGame();
			return true;
		});


		this.addSessionSubAction("end", List.of("bingo.admin"), (args, session) -> {
			session.endGame();
			return true;
		});


		this.addSessionSubAction("wait", List.of("bingo.admin"), (args, session) -> {
			session.pauseAutomaticStart();
			BingoPlayerSender.sendMessage(Component.text("Toggled automatic starting timer"), getLastUser());
			return true;
		});


		this.addSessionSubAction("deathmatch", List.of("bingo.admin"), (args, session) -> {

			if (!session.isRunning()) {
				BingoMessage.NO_DEATHMATCH.sendToAudience(getLastUser(), NamedTextColor.RED);
				return false;
			}

			((BingoGame) session.phase()).startDeathMatch(3);
			return true;
		});

		this.addSessionSubAction("creator", List.of("bingo.admin"), (args, session) -> {
			if (!(getLastUser() instanceof PlayerHandle player)) {
				return false;
			}

			BingoReloaded.runtime().openBingoCreator(player);
			return true;
		});


		this.addSessionSubAction("stats", List.of("bingo.admin"), (args, session) -> {
			if (!config.getOptionValue(BingoOptions.SAVE_PLAYER_STATISTICS)) {
				Component text = Component.text("Player statistics are not being tracked at this moment!")
						.color(NamedTextColor.RED);
				BingoPlayerSender.sendMessage(text, getLastUser());
				return true;
			}
			BingoStatData statsData = new BingoStatData(gameManager.getPlatform());
			Component msg;
			if (args.length > 1 && getLastUser().hasPermission("bingo.admin")) {
				msg = statsData.getPlayerStatsFormatted(args[1]);
			} else {
				if (!(getLastUser() instanceof PlayerHandle player)) {
					return false;
				}

				msg = statsData.getPlayerStatsFormatted(player.uniqueId());
			}
			BingoPlayerSender.sendMessage(msg, getLastUser());
			return true;
		});


		ActionTree addKitAction = new ActionTree("add", (args) -> {
			if (args.length < 4) {
				BingoPlayerSender.sendMessage(Component.text("Please specify a kit name for slot " + args[2]).color(NamedTextColor.RED), getLastUser());
				return false;
			}

			if (!(getLastUser() instanceof PlayerHandle player)) {
				return false;
			}
			addPlayerKit(args[0], Arrays.stream(args).collect(Collectors.toList()).subList(1, args.length), player);
			return true;
		})
				.addTabCompletion(args -> List.of("1", "2", "3", "4", "5"));


		ActionTree removeKitAction = new ActionTree("remove", (args) -> removePlayerKit(args[0]))
				.addTabCompletion(args -> List.of("1", "2", "3", "4", "5"));


		ActionTree itemKitAction = new ActionTree("item", (args) -> {
			if (!(getLastUser() instanceof PlayerHandle player)) {
				return false;
			}
			return giveUserBingoItem(player, args[0]);
		})
				.addTabCompletion(args -> List.of("wand", "card"));


		this.addSubAction(new ActionTree("kit", List.of("bingo.admin"))
				.addSubAction(addKitAction)
				.addSubAction(removeKitAction)
				.addSubAction(itemKitAction));


		this.addSessionSubAction("teamedit", List.of("bingo.admin"), (args, session) -> {
			if (!(getLastUser() instanceof PlayerHandle player)) {
				return false;
			}

			BingoReloaded.runtime().openTeamEditor(player);
			return true;
		});


		this.addSessionSubAction("teams", List.of("bingo.admin"), (args, session) -> {
			BingoPlayerSender.sendMessage(Component.text("Here are all the teams with at least 1 player:"), getLastUser());
			session.teamManager.getActiveTeams().getTeams().forEach(team -> {
				if (team.getMembers().isEmpty()) {
					return;
				}
				getLastUser().sendMessage(Component.text(" - ").append(team.getColoredName()).append(Component.text(": ")
						.append(Component.join(JoinConfiguration.separator(Component.text(", ")),
								team.getMembers().stream()
										.map(BingoParticipant::getDisplayName)
										.toList()))));
			});
			return true;
		});
	}

	public void addPlayerKit(String slot, List<String> kitNameParts, PlayerHandle fromPlayerInventory) {
		PlayerKit kit = switch (slot) {
			case "1" -> PlayerKit.CUSTOM_1;
			case "2" -> PlayerKit.CUSTOM_2;
			case "3" -> PlayerKit.CUSTOM_3;
			case "4" -> PlayerKit.CUSTOM_4;
			case "5" -> PlayerKit.CUSTOM_5;
			default -> {
				BingoPlayerSender.sendMessage(Component.text("Invalid slot, please a slot from 1 through 5 to save this kit in").color(NamedTextColor.RED), getLastUser());
				yield null;
			}
		};
		if (kit == null) {
			return;
		}

		StringBuilder kitName = new StringBuilder();
		for (int i = 0; i < kitNameParts.size() - 1; i++) {
			kitName.append(kitNameParts.get(i)).append(" ");
		}
		kitName.append(kitNameParts.getLast());

		CustomKitData data = new CustomKitData();
		if (!data.assignCustomKit(ComponentUtils.MINI_BUILDER.deserialize(kitName.toString()), kit, fromPlayerInventory)) {
			Component message = ComponentUtils.MINI_BUILDER
					.deserialize("<red>Cannot add custom kit " + kitName + " to slot " + slot + ", this slot already contains kit ")
					.append(data.getCustomKit(kit).name())
					.append(Component.text(". Remove it first!"));
			BingoPlayerSender.sendMessage(message, getLastUser());
		} else {
			Component message = ComponentUtils.MINI_BUILDER
					.deserialize("<green>Created custom kit " + kitName + " in slot " + slot + " from your inventory");
			BingoPlayerSender.sendMessage(message, getLastUser());
		}
	}

	public boolean removePlayerKit(String slot) {
		PlayerKit kit = switch (slot) {
			case "1" -> PlayerKit.CUSTOM_1;
			case "2" -> PlayerKit.CUSTOM_2;
			case "3" -> PlayerKit.CUSTOM_3;
			case "4" -> PlayerKit.CUSTOM_4;
			case "5" -> PlayerKit.CUSTOM_5;
			default -> {
				BingoPlayerSender.sendMessage(Component.text("Invalid slot, please a slot from 1 through 5 to save this kit in").color(NamedTextColor.RED), getLastUser());
				yield null;
			}
		};
		if (kit == null) {
			return false;
		}

		CustomKitData data = new CustomKitData();
		CustomKit customKit = data.getCustomKit(kit);
		if (customKit == null) {
			Component message = ComponentUtils.MINI_BUILDER
					.deserialize("<red>Cannot remove kit from slot " + slot + " because no custom kit is assigned to this slot");
			BingoPlayerSender.sendMessage(message, getLastUser());
		} else {
			data.removeCustomKit(kit);

			Component message = ComponentUtils.MINI_BUILDER
					.deserialize("<green>Removed custom kit " + ComponentUtils.MINI_BUILDER.serialize(customKit.name()) + " from slot " + slot);
			BingoPlayerSender.sendMessage(message, getLastUser());
		}

		return true;
	}

	public boolean giveUserBingoItem(PlayerHandle player, String itemName) {
		return switch (itemName) {
			case "wand" -> {
				player.inventory().addItem(PlayerKit.WAND_ITEM.buildItem());
				yield true;
			}
			case "card" -> {
				player.inventory().addItem(PlayerKit.CARD_ITEM.buildItem());
				yield true;
			}
			default -> false;
		};
	}

	public void showTeamCardsToUser(ActionUser user, BingoSession session) {
		if (!session.isRunning()) {
			return;
		}

		if (!(getLastUser() instanceof PlayerHandle player)) {
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

	public boolean reloadCommand(String reloadOption, ActionUser user) {
		switch (reloadOption) {
			case "all" -> reloadAll();
			case "config" -> reloadConfig();
			case "worlds" -> reloadWorlds();
			case "placeholders" -> reloadPlaceholders();
			case "scoreboards" -> reloadScoreboards();
			case "data" -> reloadData();
			case "language" -> reloadLanguage();
			default -> {
				BingoPlayerSender.sendMessage(Component.text("Cannot reload '" + reloadOption + "', invalid option"), user);
				return false;
			}
		}

		BingoPlayerSender.sendMessage(Component.text("Reloaded " + reloadOption), user);
		return true;
	}

	public void reloadAll() {
		reloadConfig();
		reloadPlaceholders();
		reloadScoreboards();
		reloadData();
		reloadLanguage();

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

	public @Nullable BingoSession getSessionFromUser(ActionUser user) {
		if (user instanceof PlayerHandle player) {
			return gameManager.getSessionFromWorld(player.world());
		}

		return null;
	}

	public void addSessionSubAction(String name, List<String> permissions, BiFunction<String[], BingoSession, Boolean> action) {
		addSubAction(new ActionTree(name, permissions, (args) -> {
			BingoSession session = getSessionFromUser(getLastUser());
			if (session == null) {
				return false;
			} else {
				return action.apply(args, session);
			}
		}));
	}
}
