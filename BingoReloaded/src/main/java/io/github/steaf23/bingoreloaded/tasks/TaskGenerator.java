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
    private static final TaskData DEFAULT_TASK = new ItemTask(Material.DIRT, 1);

    public static List<GameTask> generateCardTasks(String cardName, int seed, boolean withAdvancements, boolean withStatistics, CardSize size) {
        BingoCardData cardsData = new BingoCardData();
        TaskListData listsData = cardsData.lists();
        // Create shuffler
        Random shuffler;
        if (seed == 0) {
            shuffler = new Random();
        } else {
            shuffler = new Random(seed);
        }

        Map<String, List<TaskData>> taskMap = new HashMap<>();
        for (String listName : cardsData.getListNames(cardName)) {
            List<TaskData> tasks = new ArrayList<>(listsData.getTasks(listName, withStatistics, withAdvancements));
            if (!tasks.isEmpty()) {
                Collections.shuffle(tasks, shuffler);
                taskMap.put(listName, tasks);
            }
        }

        // Create ticketList
        List<String> ticketList = new ArrayList<>();
        for (String listName : cardsData.getListsSortedByMin(cardName)) {
            if (!taskMap.containsKey(listName)) {
                continue;
            }

            int proportionalMin = Math.max(1, cardsData.getListMin(cardName, listName));
            for (int i = 0; i < proportionalMin; i++) {
                ticketList.add(listName);
            }
        }
        List<String> overflowList = new ArrayList<>();
        for (String listName : cardsData.getListNames(cardName)) {
            int listMin = cardsData.getListMin(cardName, listName);
            int listMax = cardsData.getListMax(cardName, listName);

            if (!taskMap.containsKey(listName)) {
                continue;
            }

            int proportionalMin = Math.max(1, listMin);
            int proportionalMax = listMax;

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
        while (newTasks.size() < size.fullCardSize) {
            newTasks.add(DEFAULT_TASK);
        }
        newTasks = newTasks.subList(0, size.fullCardSize);

        // Shuffle and add tasks to the card.
        Collections.shuffle(newTasks, shuffler);
        return newTasks.stream().map(GameTask::new).toList();
    }
}
