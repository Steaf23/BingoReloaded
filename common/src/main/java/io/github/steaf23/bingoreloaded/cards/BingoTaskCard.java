package io.github.steaf23.bingoreloaded.cards;

import io.github.steaf23.bingoreloaded.api.CardMenu;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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
                if (!allTasks.get(indexRow).isCompletedByTeam(team)) {
                    completedRow = false;
                }

                int indexCol = size.size * x + y;
                if (!allTasks.get(indexCol).isCompletedByTeam(team)) {
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
            if (!allTasks.get(idx).isCompletedByTeam(team)) {
                completedDiagonal1 = false;
                break;
            }
        }

        boolean completedDiagonal2 = true;
        for (int idx = 0; idx < size.fullCardSize; idx += size.size - 1) {
            if (idx != 0 && idx != size.fullCardSize - 1) {
                if (!allTasks.get(idx).isCompletedByTeam(team)) {
                    completedDiagonal2 = false;
                    break;
                }
            }
        }
        return completedDiagonal1 || completedDiagonal2;
    }

    @Override
    public BingoTaskCard copy(@Nullable Component alternateTitle) {
        BingoTaskCard card = new BingoTaskCard(menu.copy(alternateTitle), this.size);
        List<GameTask> newTasks = new ArrayList<>();
        for (GameTask slot : getTasks()) {
            newTasks.add(slot.copy());
        }
        card.setTasks(newTasks);
        return card;
    }

    @Override
    public boolean canGenerateSeparateCards() {
        return true;
    }
}
