package io.github.steaf23.bingoreloaded.gui.hud;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.playerdisplay.scoreboard.HUDRegistry;
import org.bukkit.entity.Player;

/**
 * Created for the sake of disabling the scoreboard if desired.
 */
public class DisabledBingoGameHUDGroup extends BingoGameHUDGroup
{
    public DisabledBingoGameHUDGroup(HUDRegistry registry, BingoSession session, boolean showPlayerNames) {
        super(registry, session, showPlayerNames);
    }

    @Override
    public void updateTeamScores() {
    }

    @Override
    public void removePlayer(Player player) {
    }

    @Override
    public void addPlayer(Player player) {
    }
}
