package io.github.steaf23.bingoreloaded.tasks;

import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.TaskListData;
import io.github.steaf23.bingoreloaded.tasks.data.ItemTask;
import io.github.steaf23.bingoreloaded.tasks.data.TaskData;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TaskGenerator
{
    public record GeneratorSettings(String cardName, int seed, boolean includeAdvancements, boolean includeStatistics, CardSize size, GameTask.TaskDisplayMode displayMode) {}

    private static final TaskData DEFAULT_TASK = new ItemTask(Material.DIRT, 1);

    /**
     * Generating a bingo card has a few steps:
     * - Create task shuffler
     * - Create a ticketlist. This list contains a list name for each task on the card,
     * based on how often an item from that list should appear on the card.
     * - Using the ticketlist, pick a random task from each ticketlist entry to put on the card.
     * - Finally shuffle the tasks and add them to the card.
     * If the final task count is lower than the amount of spaces available on the card, it will be filled up using default tasks.
     */
    public static List<GameTask> generateCardTasks(GeneratorSettings settings) {
        BingoCardData cardsData = new BingoCardData();
        TaskListData listsData = cardsData.lists();
        // Create shuffler
        Random shuffler;
        if (settings.seed == 0) {
            shuffler = new Random();
        } else {
            shuffler = new Random(settings.seed);
        }

        Map<String, List<TaskData>> taskMap = new HashMap<>();
        for (String listName : cardsData.getListNames(settings.cardName)) {
            List<TaskData> tasks = new ArrayList<>(listsData.getTasks(listName, settings.includeStatistics, settings.includeAdvancements));
            if (!tasks.isEmpty()) {
                Collections.shuffle(tasks, shuffler);
                taskMap.put(listName, tasks);
            }
        }

        // Create ticketList
        List<String> ticketList = new ArrayList<>();
        for (String listName : cardsData.getListsSortedByMin(settings.cardName)) {
            if (!taskMap.containsKey(listName)) {
                continue;
            }

            int proportionalMin = Math.max(1, cardsData.getListMin(settings.cardName, listName));
            for (int i = 0; i < proportionalMin; i++) {
                ticketList.add(listName);
            }
        }
        List<String> overflowList = new ArrayList<>();
        for (String listName : cardsData.getListNames(settings.cardName)) {
            int listMin = cardsData.getListMin(settings.cardName, listName);
            int listMax = cardsData.getListMax(settings.cardName, listName);

            if (!taskMap.containsKey(listName)) {
                continue;
            }

            int proportionalMin = Math.max(1, listMin);
            int proportionalMax = listMax;

            for (int i = 0; i < proportionalMax - proportionalMin; i++) {
                overflowList.add(listName);
            }
        }

        int fullCardSize = settings.size.fullCardSize;

        Collections.shuffle(overflowList, shuffler);
        ticketList.addAll(overflowList);
        if (ticketList.size() > fullCardSize)
            ticketList = ticketList.subList(0, fullCardSize);

        // Pick random tasks
        List<TaskData> newTasks = new ArrayList<>();
        for (String listName : ticketList) {
            // pop the first task in the list (which is random because we shuffled it beforehand) and add it to our final tasks
            List<TaskData> tasks = taskMap.get(listName);
            if (!tasks.isEmpty()) {
                newTasks.add(tasks.removeLast());
            }
            else {
                ConsoleMessenger.error("Found empty task list '" + listName + "'.");
            }
        }
        while (newTasks.size() < fullCardSize) {
            newTasks.add(DEFAULT_TASK);
        }
        newTasks = newTasks.subList(0, fullCardSize);

        // Shuffle and add tasks to the card.
        Collections.shuffle(newTasks, shuffler);
        return newTasks.stream().map(t -> new GameTask(t, settings.displayMode())).toList();
    }
}
