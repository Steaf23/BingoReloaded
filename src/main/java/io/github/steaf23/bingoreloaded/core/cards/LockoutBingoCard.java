package io.github.steaf23.bingoreloaded.core.cards;

import io.github.steaf23.bingoreloaded.core.BingoGame;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.BingoGameManager;
import io.github.steaf23.bingoreloaded.core.data.TranslationData;
import io.github.steaf23.bingoreloaded.core.event.BingoCardTaskCompleteEvent;
import io.github.steaf23.bingoreloaded.core.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.core.player.BingoTeam;
import io.github.steaf23.bingoreloaded.core.player.TeamManager;

public class LockoutBingoCard extends BingoCard
{
    public int teamCount;
    public int currentMaxTasks;

    public LockoutBingoCard(CardSize size, int teamCount)
    {
        super(size);
        this.currentMaxTasks = size.fullCardSize;
        this.teamCount = teamCount;

        menu.setInfo(TranslationData.itemName("menu.card.info_lockout"),
                TranslationData.itemDescription("menu.card.info_lockout"));
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
        BingoGame game = BingoGameManager.get().getActiveGame(event.worldName);
        if (game == null)
        {
            return;
        }

        TeamManager teamManager = game.getTeamManager();
        // get the completeCount of the team with the most items.
        BingoTeam leadingTeam = teamManager.getLeadingTeam();
        BingoTeam losingTeam = teamManager.getLosingTeam();

        int itemsLeft = size.fullCardSize - getTotalCompleteCount(teamManager);

        // if amount on items cannot get up to amount of items of the team with the most items, this team cannot win anymore.
        if (itemsLeft + getCompleteCount(losingTeam) < getCompleteCount(leadingTeam))
        {
            dropTeam(losingTeam, teamManager);
        }
    }

    public void dropTeam(BingoTeam team, TeamManager teamManager)
    {
        new Message("game.team.dropped")
                .arg(team.getColoredName().asLegacyString())
                .sendAll(teamManager.getWorldName());
        team.outOfTheGame = true;
        for (BingoTask task : tasks)
        {
            if (task.completedBy.isPresent() && teamManager.getPlayersOfTeam(team).contains(task.completedBy.get()))
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
