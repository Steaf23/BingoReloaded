package io.github.steaf23.bingoreloaded.cards;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.event.BingoCardTaskCompleteEvent;
import io.github.steaf23.bingoreloaded.gui.base.MenuManager;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.player.TeamManager;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;

public class LockoutBingoCard extends BingoCard
{
    public int teamCount;
    public int currentMaxTasks;

    private final TeamManager teamManager;

    public LockoutBingoCard(MenuManager menuManager, CardSize size, int teamCount, TeamManager teamManager) {
        super(menuManager, size);
        this.currentMaxTasks = size.fullCardSize;
        this.teamCount = teamCount;
        this.teamManager = teamManager;

        menu.setInfo(BingoTranslation.INFO_LOCKOUT_NAME.translate(),
                BingoTranslation.INFO_LOCKOUT_DESC.translate().split("\\n"));
    }

    // Lockout cards cannot be copied since it should be the same instance for every player.
    @Override
    public LockoutBingoCard copy() {
        return this;
    }

    @Override
    public boolean hasBingo(BingoTeam team) {
        // get the completeCount of the team with the most items.
        BingoTeam leadingTeam = teamManager.getLeadingTeam();
        BingoTeam losingTeam = teamManager.getLosingTeam();

        int itemsLeft = size.fullCardSize - getTotalCompleteCount(teamManager);

        Message.log("Leading team score: " + getCompleteCount(leadingTeam));
        Message.log("Losing team score: " + getCompleteCount(losingTeam));
        Message.log("Losing team: " + losingTeam.getIdentifier());
        Message.log("itemsLeft: " + itemsLeft);
        Message.log("should drop?: " + (itemsLeft + getCompleteCount(losingTeam) < getCompleteCount(leadingTeam)));

        // if amount on items cannot get up to amount of items of the team with the most items, this team cannot win anymore.
        if (itemsLeft + getCompleteCount(losingTeam) < getCompleteCount(leadingTeam)) {
            dropTeam(losingTeam, teamManager.getSession());
        }

        if (teamCount < 2) {
            return true;
        }

        if (teamCount > 2) {
            return false;
        }

        // Only pick a bingo winner when there are only 2 teams remaining
        int completeCount = getCompleteCount(team);
        Message.log("BINGO?");
        Message.log("completeCount: " + getCompleteCount(team));
        Message.log("currentMaxTasks: " + currentMaxTasks);
        Message.log("teamCount: " + teamCount);
        Message.log("currentMaxTasks / 2:" + (currentMaxTasks / 2));
        return completeCount > currentMaxTasks / 2;
    }

    public void dropTeam(BingoTeam team, BingoSession session) {
        new TranslatedMessage(BingoTranslation.DROPPED)
                .arg(team.getColoredName().asLegacyString())
                .sendAll(session);
        team.outOfTheGame = true;
        for (BingoTask task : tasks) {
            if (task.isCompleted() && session.teamManager.getParticipantsOfTeam(team).contains(task.completedBy.get())) {
                task.setVoided(true);
                currentMaxTasks--;
            }
        }
        teamCount--;
    }

    public int getTotalCompleteCount(TeamManager teamManager) {
        int total = 0;
        for (BingoTeam t : teamManager.getActiveTeams()) {
            total += getCompleteCount(t);
        }
        return total;
    }
}
