package io.github.steaf23.bingoreloaded.cards;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gui.base.MenuBoard;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.player.team.BingoTeamContainer;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;

public class LockoutBingoCard extends BingoCard
{
    public int teamCount;
    public int currentMaxTasks;
    private final BingoSession session;
    private final BingoTeamContainer teams;

    public LockoutBingoCard(MenuBoard menuBoard, CardSize size, BingoSession session, BingoTeamContainer teams) {
        super(menuBoard, size);
        this.currentMaxTasks = size.fullCardSize;
        this.teamCount = teams.teamCount();
        this.session = session;
        this.teams = teams;

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
        BingoTeam leadingTeam = teams.getLeadingTeam();
        BingoTeam losingTeam = teams.getLosingTeam();

        int itemsLeft = size.fullCardSize - teams.getTotalCompleteCount();

        // if amount on items cannot get up to amount of items of the team with the most items, this team cannot win anymore.
        if (itemsLeft + losingTeam.getCompleteCount() < leadingTeam.getCompleteCount()) {
            dropTeam(losingTeam, session);
        }

        if (teamCount < 2) {
            return true;
        }

        if (teamCount > 2) {
            return false;
        }

        // Only pick a bingo winner when there are only 2 teams remaining
        int completeCount = team.getCompleteCount();
        return completeCount > (currentMaxTasks / 2);
    }

    public void dropTeam(BingoTeam team, BingoSession session) {
        if (team.outOfTheGame) {
            return;
        }
        new TranslatedMessage(BingoTranslation.DROPPED)
                .arg(team.getColoredName().asLegacyString())
                .sendAll(session);
        team.outOfTheGame = true;
        for (BingoTask task : tasks) {
            if (task.isCompleted() && team.getMembers().contains(task.getCompletedBy().orElseGet(() -> null))) {
                task.setVoided(true);
                currentMaxTasks--;
            }
        }
        teamCount--;
    }
}
