package io.github.steaf23.bingoreloaded.gameloop.spawn.strategy;

import io.github.steaf23.bingoreloaded.data.teleportspiral.TeleportSpiralData;
import io.github.steaf23.bingoreloaded.data.teleportspiral.TeleportationSpiral;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.lib.api.GlobalPosition;
import io.github.steaf23.bingoreloaded.player.team.TeamContainer;
import java.util.List;

public record SpiralSpawnStrategy(
    TeleportSpiralData data
) implements SpawnStrategy {
    @Override
    public List<SpawnSite> getSpawnSites(Context context, TeamContainer teams) {
        while (true) {
            TeleportationSpiral.Point nextStart = data.getNextSpiralPosition();
            GlobalPosition pos = context
                .world()
                .highestBlockAt(nextStart.x(), nextStart.z());
            if (
                !data.getSpiralOptions().skipOceanBiomes() ||
                !BingoGame.isOceanBiome(context.world(), pos)
            ) {
                return List.of(
                    new SpawnSite(pos, teams.getAllOnlineParticipants())
                );
            }
        }
    }
}
