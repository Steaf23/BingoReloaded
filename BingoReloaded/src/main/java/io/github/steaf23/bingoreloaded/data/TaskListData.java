package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.tasks.data.AdvancementTask;
import io.github.steaf23.bingoreloaded.tasks.data.StatisticTask;
import io.github.steaf23.bingoreloaded.tasks.TaskData;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;

import java.util.ArrayList;
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

    private final DataAccessor data = BingoReloaded.getDataAccessor("data/" + BingoReloaded.getDefaultTasksVersion());

    public Set<TaskData> getTasks(String listName, boolean withStatistics, boolean withAdvancements)
    {
        if (!data.contains(listName + ".tasks"))
            return new HashSet<>();

        return data.getSerializableList(listName + ".tasks", TaskData.class).stream().filter((i ->
                !(i instanceof StatisticTask && !withStatistics) &&
                !(i instanceof AdvancementTask && !withAdvancements))).collect(Collectors.toSet());
    }

    public int getTaskCount(String listName)
    {
        return data.getInt(listName + ".size", 0);
    }

    public void saveTasksFromGroup(String listName, List<TaskData> group, List<TaskData> tasksToSave)
    {
        Set<TaskData> savedTasks = getTasks(listName, true, true);
        Set<TaskData> tasksToRemove = group.stream().filter(t ->
                tasksToSave.stream().noneMatch(i -> i.equals(t))).collect(Collectors.toSet());

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

        data.setSerializableList(listName + ".tasks", TaskData.class, new ArrayList<>(savedTasks));
        data.setInt(listName + ".size", savedTasks.size());
        data.saveChanges();
    }

    public boolean removeList(String listName)
    {
        if (!data.contains(listName))
            return false;

        if (DEFAULT_LIST_NAMES.contains(listName)) {
            ConsoleMessenger.error("Cannot remove default lists!");
            return false;
        }
        data.erase(listName);
        data.saveChanges();
        return true;
    }

    public boolean duplicateList(String listName)
    {
        if (!data.contains(listName))
            return false;

        var list = data.getStorage(listName);
        String newName = listName + "_copy";
        if (data.contains(newName)) // Card with newName already exists
            return false;

        data.setStorage(newName, list);
        data.saveChanges();
        return true;
    }

    public boolean renameList(String oldName, String newName)
    {
        if (DEFAULT_LIST_NAMES.contains(oldName) || DEFAULT_LIST_NAMES.contains(newName))
            return false;
        if (!data.contains(oldName))
            return false;
        if (data.contains(newName)) // Card with newName already exists
            return false;

        var list = data.getStorage(oldName);
        data.setStorage(newName, list);
        data.erase(oldName);
        data.saveChanges();
        return true;
    }

    /**
     * @return All the list names present in the lists_x.yml file.
     */
    public Set<String> getListNames()
    {
        return data.getKeys();
    }
}
