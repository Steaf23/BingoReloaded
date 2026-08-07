package io.github.steaf23.bingoreloaded.gameloop.spawn.strategy;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.teleportgrid.TeleportGridData;
import io.github.steaf23.bingoreloaded.data.teleportgrid.TeleportationGrid;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.lib.api.WorldPosition;
import io.github.steaf23.bingoreloaded.lib.util.ComponentUtils;
import io.github.steaf23.bingoreloaded.player.team.TeamContainer;
import io.github.steaf23.bingoreloaded.util.BingoPlayerSender;
import net.kyori.adventure.audience.ForwardingAudience;

import java.util.List;

public record GridSpawnStrategy(TeleportGridData data) implements SpawnStrategy {

	@Override
	public List<SpawnSite> getSpawnSites(Context context, TeamContainer teams) {
		WorldPosition pos = new WorldPosition(context.world(), 0.0D, 0.0D, 0.0D);
		while (!data.isDone()) {
			TeleportationGrid.Point nextStart = data.createNextStart();
			pos = context.world().highestBlockAt(nextStart.x(), nextStart.z());
			if (!data.getGridOptions().skipOceanBiomes() || !BingoGame.isOceanBiome(context.world().biomeAtPos(pos))) {
				break;
			}
		}

		if (data.isDone()) {
			String command = data.getGridOptions().finishedCommand();
			if (!command.isEmpty()) {
				String commandToSend = command.replace("{world}", context.session().getGameManager().getNameOfSession(context.session()));
				context.session().getGameManager().getPlatform().sendConsoleCommand(commandToSend);
			}
			BingoPlayerSender.sendMessage(
					ComponentUtils.MINI_BUILDER.deserialize("<red>Ran out of grid positions. The grid has been reset but you probably want to reset the world.</red>"),
					(ForwardingAudience)() -> teams.getAllOnlineParticipants().stream()
							.filter(p -> p.sessionPlayer()
									.map(BingoReloaded::isAdmin)
									.orElse(false)).toList());
			data.reset();
		}

		return List.of(new SpawnSite(pos,
				teams.getAllOnlineParticipants()));
	}
}
