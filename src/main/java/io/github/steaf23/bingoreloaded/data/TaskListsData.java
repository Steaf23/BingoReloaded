package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.Message;
import io.github.steaf23.bingoreloaded.item.tasks.*;
import org.bukkit.configuration.MemorySection;

import javax.management.ListenerNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is used to interface with the lists.yml file.
 */
public class TaskListsData
{
    private static final YmlDataManager data = new YmlDataManager("lists.yml");

    public static List<BingoTask> getTasks(String listName)
    {
        MemorySection taskList = ((MemorySection) data.getConfig().get(listName));
        List<BingoTask> finalList = new ArrayList<>();
        taskList.getValues(false).forEach((k, v) -> {
            finalList.add((BingoTask)v);
        });
        return finalList;
    }

    public static List<BingoTask> getItemTasks(String listName)
    {
        return getTasks(listName);
//        List<ItemTask> tasks = new ArrayList<>();
//        getTasks(listName).forEach(task ->
//        {
//            if (task instanceof ItemTask)
//                tasks.add((ItemTask) task);
//        });
//        return tasks;
    }

    public static void saveTasks(String listName, BingoTask... tasks)
    {
        data.getConfig().set(listName, null);
        for (BingoTask task : tasks)
        {
            data.getConfig().set(listName + "." + task.data.hashCode(), task);
        }
        data.saveConfig();
    }

    public static int getTaskCount(String listName)
    {
        return ((MemorySection) data.getConfig().get(listName)).getValues(false).size();
    }

    public static boolean removeList(String listName)
    {
        if (data.getConfig().contains(listName))
        {
            data.getConfig().set(listName, null);
            data.saveConfig();
            return true;
        }
        return false;
    }

    /**
     * Add empty lists to the lists.yml file.
     * @param names names of the categories to add
     */
    public static void addEmptyList(String... names)
    {
        for (String n : names)
        {
            if (data.getConfig().contains(n)) return;
            if (n.equals("")) return;

            data.getConfig().set(n, new String[]{});
        }
        data.saveConfig();
    }

    /**
     * @return All the category names present in the lists.yml file.
     */
    public static Set<String> getListNames()
    {
        return data.getConfig().getKeys(false);
    }

    public static BingoTask getRandomTask(String listName)
    {
        List<BingoTask> tasks = getTasks(listName);
        int idx = new Random().nextInt(tasks.size());
        return tasks.get(idx);
    }
//
//    public static ItemTask getRandomItemTask(String listName)
//    {
//        Message.log("Picking random item from " + listName);
//        List<ItemTask> tasks = getItemTasks(listName);
//        int idx = new Random().nextInt(tasks.size());
//        return tasks.get(idx);
//    }
}
