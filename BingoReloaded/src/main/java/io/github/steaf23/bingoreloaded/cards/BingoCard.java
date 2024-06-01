package io.github.steaf23.bingoreloaded.cards;


import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.data.TaskListData;
import io.github.steaf23.bingoreloaded.gui.inventory.CardMenu;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.tasks.*;
import io.github.steaf23.bingoreloaded.tasks.tracker.TaskProgressTracker;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.easymenulib.inventory.MenuBoard;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class BingoCard
{
    public final CardSize size;
    protected final TaskProgressTracker progressTracker;
    private List<BingoTask> tasks;

    protected final CardMenu menu;

    private static final TaskData DEFAULT_TASK = new ItemTask(Material.DIRT, 1);

    public BingoCard(MenuBoard menuBoard, CardSize size, TaskProgressTracker progressTracker) {
        this(new CardMenu(menuBoard, size, BingoTranslation.CARD_TITLE.translate()), size, progressTracker);
    }

    public BingoCard(CardMenu menu, CardSize size, TaskProgressTracker progressTracker) {
        this.size = size;
        this.tasks = new ArrayList<>();
        this.menu = menu;
        this.progressTracker = progressTracker;
        menu.setInfo(BingoTranslation.INFO_REGULAR_NAME.translate(),
                BingoTranslation.INFO_REGULAR_DESC.translate().split("\\n"));
    }

    /**
     * Generating a bingo card has a few steps:
     * - Create task shuffler
     * - Create a ticketlist. This list contains a list name for each task on the card,
     * based on how often an item from that list should appear on the card.
     * - Using the ticketlist, pick a random task from each ticketlist entry to put on the card.
     * - Finally shuffle the tasks and add them to the card.
     * If the final task count is lower than the amount of spaces available on the card, it will be filled up using default tasks.
     *
     * @param cardName
     * @param seed
     */
    public void generateCard(String cardName, int seed, boolean withAdvancements, boolean withStatistics) {
        BingoCardData cardsData = new BingoCardData();
        TaskListData listsData = cardsData.lists();
        // Create shuffler
        Random shuffler;
        if (seed == 0) {
            shuffler = new Random();
        } else {
            shuffler = new Random(seed);
        }

        // Create ticketList
        List<String> ticketList = new ArrayList<>();
        for (String listName : cardsData.getListsSortedByMin(cardName)) {
            if (listsData.getTasks(listName, withStatistics, withAdvancements).size() == 0) // Skip empty task lists.
            {
                continue;
            }

            int proportionalMin = Math.max(1, cardsData.getListMin(cardName, listName));
            for (int i = 0; i < proportionalMin; i++) {
                ticketList.add(listName);
            }
        }
        List<String> overflowList = new ArrayList<>();
        for (String listName : cardsData.getListNames(cardName)) {
            int proportionalMin = Math.max(1, cardsData.getListMin(cardName, listName));
            int proportionalMax = cardsData.getListMax(cardName, listName);

            for (int i = 0; i < proportionalMax - proportionalMin; i++) {
                overflowList.add(listName);
            }
        }
        Collections.shuffle(overflowList, shuffler);
        ticketList.addAll(overflowList);
        if (ticketList.size() > size.fullCardSize)
            ticketList = ticketList.subList(0, size.fullCardSize);

        // Pick random tasks
        List<TaskData> newTasks = new ArrayList<>();
        Map<String, List<TaskData>> allTasks = new HashMap<>();
        for (String listName : ticketList) {
            if (!allTasks.containsKey(listName)) {
                List<TaskData> listTasks = new ArrayList<>(listsData.getTasks(listName, withStatistics, withAdvancements));
                if (listTasks.size() == 0) // Skip empty task lists.
                {
                    continue;
                }
                Collections.shuffle(listTasks, shuffler);
                allTasks.put(listName, listTasks);
            }
            if (allTasks.get(listName).size() != 0) {
                newTasks.add(allTasks.get(listName).remove(allTasks.get(listName).size() - 1));
            }
            else {
                Message.error("Found empty task list '" + listName + "'.");
            }
        }
        while (newTasks.size() < size.fullCardSize) {
            newTasks.add(DEFAULT_TASK);
        }
        newTasks = newTasks.subList(0, size.fullCardSize);

        // Shuffle and add tasks to the card.
        Collections.shuffle(newTasks, shuffler);
        setTasks(newTasks.stream().map(BingoTask::new).toList());
    }

    public void showInventory(Player player) {
        menu.updateTasks(getTasks());
        menu.open(player);
    }

    public List<BingoTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<BingoTask> tasks) {
        this.tasks = tasks;
    }

    public boolean hasBingo(BingoTeam team) {
        List<BingoTask> allTasks = getTasks();
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

    /**
     * @param team The team.
     * @return The amount of completed items for the given team.
     */
    public int getCompleteCount(@Nullable BingoTeam team) {
        if (team == null) {
            return 0;
        }
        int count = 0;
        for (var task : getTasks()) {
            if (task.getCompletedBy().isPresent() && team.getMembers().contains(task.getCompletedBy().get()))
                count++;
        }

        return count;
    }

    public int getCompleteCount(@Nullable BingoParticipant participant) {
        return (int) getTasks().stream()
                .filter(t -> t.getCompletedBy().isPresent() && t.getCompletedBy().get().equals(participant)).count();
    }

    public BingoCard copy() {
        BingoCard card = new BingoCard(menu.getMenuBoard(), this.size, this.progressTracker);
        List<BingoTask> newTasks = new ArrayList<>();
        for (BingoTask slot : getTasks()) {
            newTasks.add(slot.copy());
        }
        card.setTasks(newTasks);
        return card;
    }

    public void handleTaskCompleted(BingoParticipant player, BingoTask task, long timeSeconds) {}
}
