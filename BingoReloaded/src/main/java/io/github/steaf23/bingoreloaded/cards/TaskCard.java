package io.github.steaf23.bingoreloaded.cards;


import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.TaskListData;
import io.github.steaf23.bingoreloaded.gui.inventory.card.CardMenu;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.bingoreloaded.tasks.data.ItemTask;
import io.github.steaf23.bingoreloaded.tasks.TaskData;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public abstract class TaskCard
{
    public final CardSize size;
    private final List<GameTask> tasks;

    protected final CardMenu menu;

    public TaskCard(CardMenu menu, CardSize size) {
        this.size = size;
        this.tasks = new ArrayList<>();
        this.menu = menu;
        menu.setInfo(BingoMessage.INFO_REGULAR_NAME.asPhrase(),
                BingoMessage.INFO_REGULAR_DESC.asMultiline());
    }

    public abstract boolean hasTeamWon(BingoTeam team);
    public abstract TaskCard copy();

    /**
     * Generating a bingo card has a few steps:
     * - Create task shuffler
     * - Create a ticketlist. This list contains a list name for each task on the card,
     * based on how often an item from that list should appear on the card.
     * - Using the ticketlist, pick a random task from each ticketlist entry to put on the card.
     * - Finally shuffle the tasks and add them to the card.
     * If the final task count is lower than the amount of spaces available on the card, it will be filled up using default tasks.
     *
     * @param cardName name of the card to pick tasks from.
     * @param seed cards generated with the same seed and cardName will have the same tasks in the same positions.
     */
    public void generateCard(String cardName, int seed, boolean withAdvancements, boolean withStatistics) {
        setTasks(TaskGenerator.generateCardTasks(cardName, seed, withAdvancements, withStatistics, size));
    }

    public void showInventory(Player player) {
        menu.updateTasks(getTasks());
        menu.open(player);
    }

    public List<GameTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<GameTask> tasks) {
        this.tasks.clear();
        this.tasks.addAll(tasks);
        this.menu.updateTasks(tasks);
    }

    /**
     * @param team The team.
     * @return The amount of completed items for the given team.
     */
    public int getCompleteCount(@NotNull BingoTeam team) {
        int count = 0;
        for (var task : getTasks()) {
            if (task.getCompletedBy().isPresent() && team.getMembers().contains(task.getCompletedBy().get()))
                count++;
        }

        return count;
    }

    public int getCompleteCount(@NotNull BingoParticipant participant) {
        return (int) getTasks().stream()
                .filter(t -> t.getCompletedBy().isPresent() && t.getCompletedBy().get().getId().equals(participant.getId())).count();
    }

    public void handleTaskCompleted(BingoParticipant player, GameTask task, long timeSeconds) {}
}
