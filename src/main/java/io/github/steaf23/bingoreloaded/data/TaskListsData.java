package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.item.tasks.*;
import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.configuration.MemorySection;

import java.util.*;

/**
 * This class is used to interface with the lists.yml file.
 */
public class TaskListsData
{
    private static final YmlDataManager data = new YmlDataManager("lists.yml");

    public static List<BingoTask> getTasks(String listName)
    {
        if (!data.getConfig().contains(listName + ".tasks"))
            return new ArrayList<>();

        MemorySection taskList = ((MemorySection) data.getConfig().get(listName + ".tasks"));
        List<BingoTask> finalList = new ArrayList<>();
        taskList.getValues(false).forEach((k, v) -> {
            finalList.add((BingoTask)v);
        });
        return finalList;
    }

    public static int getTaskCount(String listName)
    {
        return data.getConfig().getInt(listName + ".size", 0);
    }

    public static List<BingoTask> getItemTasks(String listName)
    {
        return getTasks(listName);
    }

    public static void saveTasks(String listName, BingoTask... tasks)
    {
        data.getConfig().set(listName, null);
        for (BingoTask task : tasks)
        {
            data.getConfig().set(listName + ".tasks." + task.data.hashCode(), task);
        }
        data.getConfig().set(listName + ".size", tasks.length);
        data.saveConfig();
    }

    public static void saveTasksFromGroup(String listName, List<BingoTask> group, List<BingoTask> tasksToSave)
    {
        if (tasksToSave.size() == 0)
            return;

        data.getConfig().set(listName, new HashMap<String, Object>(){{
            put("tasks", new HashMap<>());
            put("size", 0);
        }});

        var savedTasks = getTasks(listName);
        List<BingoTask> tasksToRemove = group.stream().filter(t ->
        {
            for (BingoTask saveTask : tasksToSave)
            {
                if (t.data.isTaskEqual(saveTask.data)) return false;
            }
            return true;
        }).toList();

        for (BingoTask task : savedTasks)
        {
            TaskData taskData = task.data;
            for (BingoTask t : tasksToRemove)
            {
                if (t.data.isTaskEqual(taskData))
                    data.getConfig().set(listName + ".tasks." + task.data.hashCode(), null);
            }

            for (BingoTask t : tasksToSave)
            {
                if (t.data.isTaskEqual(taskData))
                    data.getConfig().set(listName + ".tasks." + task.data.hashCode(), null);
            }
        }

        for (BingoTask task : tasksToSave)
        {
            data.getConfig().set(listName + ".tasks." + task.data.hashCode(), task);
        }

        data.getConfig().set(listName + ".size", getTasks(listName).size());
        data.saveConfig();
    }

    public static boolean removeList(String listName)
    {
        if (!data.getConfig().contains(listName))
            return false;

        data.getConfig().set(listName, null);
        data.saveConfig();
        return true;
    }

    public static boolean duplicateList(String listName)
    {
        if (!data.getConfig().contains(listName))
            return false;

        var list = data.getConfig().get(listName);
        data.getConfig().set(listName + "_copy", list);
        data.saveConfig();
        return true;
    }

    public static boolean renameList(String listName, String newName)
    {
        var defaultLists = List.of("default_items", "default_advancements", "default_statistics");
        if (defaultLists.contains(listName) || defaultLists.contains(newName))
            return false;
        if (!data.getConfig().contains(listName))
            return false;
        if (data.getConfig().contains(newName)) // Card with newName already exists
            return false;

        var list = data.getConfig().get(listName);
        data.getConfig().set(newName, list);
        data.getConfig().set(listName, null);
        data.saveConfig();
        return true;
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
}
