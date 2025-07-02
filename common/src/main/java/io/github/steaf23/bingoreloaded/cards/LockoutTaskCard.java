package io.github.steaf23.bingoreloaded.cards;

import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gui.inventory.card.CardMenu;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.player.team.BingoTeamContainer;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LockoutTaskCard extends TaskCard
{
    public int teamCount;
    public int currentMaxTasks;
    private final BingoSession session;
    private final BingoTeamContainer teams;

    public LockoutTaskCard(@NotNull CardMenu menu, CardSize size, BingoSession session, BingoTeamContainer teams) {
        super(menu, size);
        this.currentMaxTasks = size.fullCardSize;
        this.teamCount = teams.teamCount();
        this.session = session;
        this.teams = teams;

        menu.setInfo(BingoMessage.INFO_LOCKOUT_NAME.asPhrase(),
                BingoMessage.INFO_LOCKOUT_DESC.asMultiline());
    }

    @Override
    public boolean hasTeamWon(BingoTeam team) {
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

    // Lockout cards cannot be copied since it should be the same instance for every player.
    @Override
    public TaskCard copy(@Nullable Component alternateTitle) {
        return this;
    }

    @Override
    public boolean canGenerateSeparateCards() {
        return false;
    }


    public void dropTeam(BingoTeam team, BingoSession session) {
        if (team.outOfTheGame) {
            return;
        }
        BingoMessage.DROPPED.sendToAudience(session, team.getColoredName());
        team.outOfTheGame = true;
        for (GameTask task : getTasks()) {
            if (task.isCompletedByTeam(team)) {
                task.setVoided(true);
                currentMaxTasks--;
            }
        }
        teamCount--;
    }
}
