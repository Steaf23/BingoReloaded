package io.github.steaf23.bingoreloaded.gui.cards;

import io.github.steaf23.bingoreloaded.BingoGame;
import io.github.steaf23.bingoreloaded.Message;
import io.github.steaf23.bingoreloaded.event.BingoGameEvent;
import io.github.steaf23.bingoreloaded.event.CountdownTimerFinishedEvent;
import io.github.steaf23.bingoreloaded.event.ReceiveBingoGameEvent;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.player.TeamManager;
import io.github.steaf23.bingoreloaded.util.CountdownTimer;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CountdownBingoCard extends CompleteBingoCard
{
    public CountdownBingoCard(CardSize size, BingoGame game)
    {
        super(size, game);
    }

    @EventHandler
    public void onCountdownFinished(final CountdownTimerFinishedEvent event)
    {
        Set<BingoTeam> tiedTeams = new HashSet<>();
        TeamManager teamManager = game.getTeamManager();
        tiedTeams.add(teamManager.getLeadingTeam());

        Message.log("TIME IS UP!");

        int leadingPoints = teamManager.getCompleteCount(teamManager.getLeadingTeam());
        for (BingoTeam team : teamManager.getActiveTeams())
        {
            if (teamManager.getCompleteCount(team) == leadingPoints)
            {
                tiedTeams.add(team);
            }
            else
            {
                team.outOfTheGame = true;
            }
        }

        if (tiedTeams.size() == 1)
        {
            game.bingo(teamManager.getLeadingTeam());
        }
        else
        {
            game.startDeathMatch(3);
        }
    }

    @EventHandler
    public void onBingoGameEventReceived(final ReceiveBingoGameEvent event)
    {
        if (event.eventType.equals(BingoGameEvent.ENDED))
        {

        }
    }
}
