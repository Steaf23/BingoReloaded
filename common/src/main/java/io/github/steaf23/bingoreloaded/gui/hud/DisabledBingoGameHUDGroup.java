package io.github.steaf23.bingoreloaded.gui.hud;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;

/**
 * Created for the sake of disabling the scoreboard if desired.
 */
public class DisabledBingoGameHUDGroup extends BingoGameHUDGroup
{
    public DisabledBingoGameHUDGroup(BingoSession session, boolean showPlayerNames) {
        super(session, showPlayerNames);
    }

    @Override
    public void updateTeamScores() {
    }
}
