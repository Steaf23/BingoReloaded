package io.github.steaf23.bingoreloaded.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.github.steaf23.bingoreloaded.api.BingoCommandSource;
import io.github.steaf23.bingoreloaded.cards.TaskCard;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.GameManager;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.lib.api.ActionUser;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.VirtualBingoPlayer;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;

public class BotCommand<Source> extends MappedCommand<Source> {

	public BotCommand(Settings<Source> settings) {
		super("bingobot", List.of("bingo.admin"), settings);

//		addSessionSubAction("add", List.of(), (args, session) -> {
//			String playerName = args[0];
//			String teamName = args[1];
//
//			addVirtualPlayerToTeam(session, playerName, teamName);
//			return true;
//		});
//
//		addSessionSubAction("add10", List.of(), (args, session) -> {
//			for (int i = 0; i < 10; i++) {
//				String playerName = "testPlayer_" + i;
//				String teamName = args[0];
//				addVirtualPlayerToTeam(session, playerName, teamName);
//			}
//
//			return true;
//		});
//
//		addSessionSubAction("add100", List.of(), (args, session) -> {
//			for (int i = 0; i < 100; i++) {
//				String playerName = "testPlayer_" + i;
//				String teamName = args[0];
//				addVirtualPlayerToTeam(session, playerName, teamName);
//			}
//
//			return true;
//		});
//
//		addSessionSubAction("fill", List.of(), (args, session) -> {
//			ConsoleMessenger.log("CAPACITY: " + session.teamManager.getTotalParticipantCapacity());
//			for (String teamId : session.teamManager.getJoinableTeams().keySet()) {
//				for (int i = 0; i < session.teamManager.getMaxTeamSize(); i++) {
//					String name = "test_" + teamId + "_" + i;
//					addVirtualPlayerToTeam(session, name, teamId);
//				}
//			}
//			return true;
//		});
//
//		addSessionSubAction("fillauto", List.of(), (args, session) -> {
//			ConsoleMessenger.log("CAPACITY: " + session.teamManager.getTotalParticipantCapacity());
//			for (int i = 0; i < session.teamManager.getTotalParticipantCapacity() + 3; i++) {
//				String name = "test_" + i;
//				addVirtualPlayerToTeam(session, name, "auto");
//			}
//			return true;
//		});
//
//		addSessionSubAction("remove", List.of(), (args, session) -> {
//			String playerName = args[0];
//			BingoParticipant player = getVirtualPlayerFromName(session, playerName);
//			if (player != null) {
//				session.teamManager.removeMemberFromTeam(player);
//			}
//			return true;
//		});
//
//		addSessionSubAction("complete", List.of(), (args, session) -> {
//			BingoParticipant virtualPlayer = getVirtualPlayerFromName(session, args[0]);
//			int taskIndex = Integer.parseInt(args[1]);
//			if (virtualPlayer == null) {
//				ConsoleMessenger.error("Cannot complete task " + args[1] + " for non existing virtual player: " + args[0]);
//				return false;
//			}
//			completeTaskByPlayer(virtualPlayer, taskIndex);
//			return true;
//		});
	}

	void completeTaskByPlayer(BingoParticipant player, int taskIndex) {
		if (!player.getSession().isRunning())
			return;

		Optional<TaskCard> card = player.getCard();

		if (card.isEmpty() || taskIndex >= card.get().getTasks().size()) {
			ConsoleMessenger.error("index out of bounds for task list!");
			return;
		}

		GameTask task = card.get().getTasks().get(taskIndex);
		BingoGame game = (BingoGame) player.getSession().phase();
		task.complete(player, game.getGameTimePassed());
		game.onBingoTaskCompleted(player, task);
	}

	@Nullable
	private BingoParticipant getVirtualPlayerFromName(BingoSession session, String name) {
		return session.teamManager.getParticipants().stream()
				.filter(p -> p.getName().equals(name))
				.findAny().orElse(null);
	}

	public void addVirtualPlayerToTeam(BingoSession session, String playerName, String teamName) {
		BingoParticipant virtualPlayer = getVirtualPlayerFromName(session, playerName);
		if (virtualPlayer == null) {
			virtualPlayer = new VirtualBingoPlayer(UUID.randomUUID(), playerName, session);
		}
		session.teamManager.addMemberToTeam(virtualPlayer, teamName);
	}

	public @Nullable BingoSession getSessionFromUser(GameManager manager, ActionUser user) {
		if (user instanceof PlayerHandle player) {
			return manager.getSessionFromWorld(player.world());
		}

		return null;
	}

	public void addSessionSubAction(String name, List<String> permissions, BiFunction<CommandContext<Source>, BingoSession, Boolean> action) {
		addSessionSubAction(name, permissions, ((context, args, session) -> action.apply(args, session) ? Command.SINGLE_SUCCESS : 0));
	}

	public void addSessionSubAction(String name, List<String> permissions, BingoCommand.SessionActionExecutor<Source> action) {
		then(literal(name)
				.requires(permissionRequirement(permissions))
				.executes(sessionExecutor(action))
		);
	}

	private Command<Source> sessionExecutor(BingoCommand.SessionActionExecutor<Source> executor) {
		return ctx -> {
			BingoCommandSource source = mapSource(ctx.getSource());
			Optional<BingoSession> session = source.getSession();
			if (session.isEmpty()) {
				return 0;
			}
			return executor.execute(source, ctx, session.orElseThrow());
		};
	}
}
