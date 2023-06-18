package io.github.steaf23.bingoreloaded.cards;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.event.BingoCardTaskCompleteEvent;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.player.TeamManager;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;

import java.util.List;

public class LockoutBingoCard extends BingoCard
{
    public int teamCount;
    public int currentMaxTasks;

    public LockoutBingoCard(CardSize size, int teamCount)
    {
        super(size);
        this.currentMaxTasks = size.fullCardSize;
        this.teamCount = teamCount;

        menu.setInfo(BingoTranslation.INFO_LOCKOUT_NAME.translate(),
                BingoTranslation.INFO_LOCKOUT_DESC.translate().split("\\n"));
    }

    public LockoutBingoCard(CardSize size, List<BingoTask> tasks, int teamCount, int currentMaxTasks) {
        super(size, tasks);
        this.teamCount = teamCount;
        this.currentMaxTasks = currentMaxTasks;
        menu.setInfo(BingoTranslation.INFO_LOCKOUT_NAME.translate(),
                BingoTranslation.INFO_LOCKOUT_DESC.translate().split("\\n"));
    }

    // Lockout cards cannot be copied since it should be the same instance for every player.
    @Override
    public LockoutBingoCard copy()
    {
        return this;
    }

    @Override
    public boolean hasBingo(BingoTeam team)
    {
        if (teamCount < 2)
        {
            return true;
        }
        int completeCount = getCompleteCount(team);
        return completeCount >= Math.floor(currentMaxTasks / (double) teamCount) + 1;
    }

    public void onCardSlotCompleteEvent(final BingoCardTaskCompleteEvent event)
    {
        if (event.session == null)
        {
            return;
        }

        TeamManager teamManager = event.session.teamManager;
        // get the completeCount of the team with the most items.
        BingoTeam leadingTeam = teamManager.getLeadingTeam();
        BingoTeam losingTeam = teamManager.getLosingTeam();

        int itemsLeft = size.fullCardSize - getTotalCompleteCount(teamManager);

        // if amount on items cannot get up to amount of items of the team with the most items, this team cannot win anymore.
        if (itemsLeft + getCompleteCount(losingTeam) < getCompleteCount(leadingTeam))
        {
            dropTeam(losingTeam, event.session);
        }
    }

    public void dropTeam(BingoTeam team, BingoSession session)
    {
        new TranslatedMessage(BingoTranslation.DROPPED)
                .arg(team.getColoredName().asLegacyString())
                .sendAll(session);
        team.outOfTheGame = true;
        for (BingoTask task : tasks)
        {
            if (task.isCompleted() && session.teamManager.getParticipantsOfTeam(team).contains(task.completedBy.get()))
            {
                task.setVoided(true);
                currentMaxTasks--;
            }
        }
        teamCount--;
    }

    public int getTotalCompleteCount(TeamManager teamManager)
    {
        int total = 0;
        for (BingoTeam t : teamManager.getActiveTeams())
        {
            total += getCompleteCount(t);
        }
        return total;
    }
}
