package io.github.steaf23.bingoreloaded.core.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.core.tasks.TaskData;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * This class is used to interface with the lists.yml file.
 */
public class TaskListsData
{
    private final YmlDataManager data = new YmlDataManager(BingoReloaded.get(), "lists.yml");

    public Set<TaskData> getTasks(String listName)
    {
        if (!data.getConfig().contains(listName + ".tasks"))
            return new HashSet<>();

        Set<TaskData> taskList = (Set<TaskData>)data.getConfig().getList(listName + ".tasks").stream().collect(Collectors.toSet());
        return taskList;
    }

    public int getTaskCount(String listName)
    {
        return data.getConfig().getInt(listName + ".size", 0);
    }

    public Set<TaskData> getItemTasks(String listName)
    {
        return getTasks(listName);
    }

    public void saveTasksFromGroup(String listName, List<TaskData> group, List<TaskData> tasksToSave)
    {
        Set<TaskData> savedTasks = getTasks(listName);
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

        data.getConfig().set(listName + ".tasks", savedTasks.stream().toList());
        data.getConfig().set(listName + ".size", savedTasks.size());
        data.saveConfig();
    }

    public boolean removeList(String listName)
    {
        if (!data.getConfig().contains(listName))
            return false;

        data.getConfig().set(listName, null);
        data.saveConfig();
        return true;
    }

    public boolean duplicateList(String listName)
    {
        if (!data.getConfig().contains(listName))
            return false;

        var list = data.getConfig().get(listName);
        data.getConfig().set(listName + "_copy", list);
        data.saveConfig();
        return true;
    }

    public boolean renameList(String listName, String newName)
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
     * @return All the list names present in the lists.yml file.
     */
    public Set<String> getListNames()
    {
        return data.getConfig().getKeys(false);
    }

    public TaskData getRandomTask(String listName)
    {
        Set<TaskData> tasks = getTasks(listName);
        int idx = new Random().nextInt(tasks.size());
        int i = 0;
        for (var task : tasks)
        {
            if (i == idx)
            {
                return task;
            }
        }
        return tasks.stream().findFirst().get();
    }
}
