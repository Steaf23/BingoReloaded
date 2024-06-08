package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.helper.YmlDataManager;
import io.github.steaf23.bingoreloaded.tasks.AdvancementTask;
import io.github.steaf23.bingoreloaded.tasks.StatisticTask;
import io.github.steaf23.bingoreloaded.tasks.TaskData;
import io.github.steaf23.bingoreloaded.util.Message;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * This class is used to interface with the lists_x.yml file.
 */
public class TaskListData
{
    public static final Set<String> DEFAULT_LIST_NAMES = Set.of(
            "default_items",
            "default_advancements",
            "default_statistics",
            "default_items_hardcore",
            "default_advancements_hardcore",
            "default_statistics_hardcore"
    );

    private final YmlDataManager data = BingoReloaded.createYmlDataManager("data/" + BingoReloaded.getDefaultTasksVersion());

    public Set<TaskData> getTasks(String listName, boolean withStatistics, boolean withAdvancements)
    {
        if (!data.getConfig().contains(listName + ".tasks"))
            return new HashSet<>();

        Set<TaskData> taskList = (Set<TaskData>)data.getConfig().getList(listName + ".tasks").stream().filter((i ->
                !(i instanceof StatisticTask && !withStatistics) &&
                !(i instanceof AdvancementTask && !withAdvancements))).collect(Collectors.toSet());
        return taskList;
    }

    public int getTaskCount(String listName)
    {
        return data.getConfig().getInt(listName + ".size", 0);
    }

    public void saveTasksFromGroup(String listName, List<TaskData> group, List<TaskData> tasksToSave)
    {
        Set<TaskData> savedTasks = getTasks(listName, true, true);
        Set<TaskData> tasksToRemove = group.stream().filter(t ->
        {
            return tasksToSave.stream().noneMatch(i -> i.equals(t));
        }).collect(Collectors.toSet());

        for (TaskData t : tasksToRemove)
        {
            savedTasks.remove(t);
        }

        for (TaskData task : tasksToSave)
        {
            // If the task cant be added to this, update the existing entry instead,
            //      used for CountableTasks since their count doesn't get used in hash comparisons
            if (!savedTasks.add(task))
            {
                savedTasks.remove(task);
                savedTasks.add(task);
            }
        }

        data.getConfig().set(listName + ".tasks", savedTasks.stream().collect(Collectors.toList()));
        data.getConfig().set(listName + ".size", savedTasks.size());
        data.saveConfig();
    }

    public boolean removeList(String listName)
    {
        if (!data.getConfig().contains(listName))
            return false;

        if (DEFAULT_LIST_NAMES.contains(listName)) {
            Message.error("Cannot remove default lists!");
            return false;
        }
        data.getConfig().set(listName, null);
        data.saveConfig();
        return true;
    }

    public boolean duplicateList(String listName)
    {
        if (!data.getConfig().contains(listName))
            return false;

        var list = data.getConfig().get(listName);
        String newName = listName + "_copy";
        if (data.getConfig().contains(newName)) // Card with newName already exists
            return false;

        data.getConfig().set(newName, list);
        data.saveConfig();
        return true;
    }

    public boolean renameList(String oldName, String newName)
    {
        if (DEFAULT_LIST_NAMES.contains(oldName) || DEFAULT_LIST_NAMES.contains(newName))
            return false;
        if (!data.getConfig().contains(oldName))
            return false;
        if (data.getConfig().contains(newName)) // Card with newName already exists
            return false;

        var list = data.getConfig().get(oldName);
        data.getConfig().set(newName, list);
        data.getConfig().set(oldName, null);
        data.saveConfig();
        return true;
    }

    /**
     * @return All the list names present in the lists_x.yml file.
     */
    public Set<String> getListNames()
    {
        return data.getConfig().getKeys(false);
    }
}
