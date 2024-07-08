package io.github.steaf23.bingoreloaded.cards;

import io.github.steaf23.bingoreloaded.gui.inventory.CardMenu;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.bingoreloaded.tasks.tracker.TaskProgressTracker;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BingoTaskCard extends TaskCard
{
    public BingoTaskCard(@NotNull CardMenu menu, CardSize size) {
        super(menu, size);
    }

    @Override
    public boolean hasTeamWon(BingoTeam team) {
        List<GameTask> allTasks = getTasks();
        //check for rows and columns
        for (int y = 0; y < size.size; y++) {
            boolean completedRow = true;
            boolean completedCol = true;
            for (int x = 0; x < size.size; x++) {
                int indexRow = size.size * y + x;
                Optional<BingoParticipant> completedBy = allTasks.get(indexRow).getCompletedBy();
                if (completedBy.isEmpty() || !team.getMembers().contains(completedBy.get())) {
                    completedRow = false;
                }

                int indexCol = size.size * x + y;
                completedBy = allTasks.get(indexCol).getCompletedBy();
                if (completedBy.isEmpty() || !team.getMembers().contains(completedBy.get())) {
                    completedCol = false;
                }
            }

            if (completedRow || completedCol) {
                return true;
            }
        }

        // check for diagonals
        boolean completedDiagonal1 = true;
        for (int idx = 0; idx < size.fullCardSize; idx += size.size + 1) {
            Optional<BingoParticipant> completedBy = allTasks.get(idx).getCompletedBy();
            if (completedBy.isEmpty() || !team.getMembers().contains(completedBy.get())) {
                completedDiagonal1 = false;
                break;
            }
        }

        boolean completedDiagonal2 = true;
        for (int idx = 0; idx < size.fullCardSize; idx += size.size - 1) {
            if (idx != 0 && idx != size.fullCardSize - 1) {
                Optional<BingoParticipant> completedBy = allTasks.get(idx).getCompletedBy();
                if (completedBy.isEmpty() || !team.getMembers().contains(completedBy.get())) {
                    completedDiagonal2 = false;
                    break;
                }
            }
        }
        return completedDiagonal1 || completedDiagonal2;
    }

    @Override
    public BingoTaskCard copy() {
        BingoTaskCard card = new BingoTaskCard(menu.copy(), this.size);
        List<GameTask> newTasks = new ArrayList<>();
        for (GameTask slot : getTasks()) {
            newTasks.add(slot.copy());
        }
        card.setTasks(newTasks);
        return card;
    }
}
