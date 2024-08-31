package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.data.core.NodeDataAccessor;
import io.github.steaf23.bingoreloaded.data.core.node.BranchNode;
import io.github.steaf23.bingoreloaded.tasks.ItemTask;
import io.github.steaf23.bingoreloaded.tasks.TaskData;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class BingoCardData
{
    public static final Set<String> DEFAULT_CARD_NAMES = Set.of(
            "default_card",
            "default_card_hardcore"
    );
    private final TaskListData listsData = new TaskListData();
    public static final int MAX_ITEMS = 36;
    public static final int MIN_ITEMS = 1;

    private final NodeDataAccessor data = BingoReloaded.getOrCreateDataAccessor("data/cards.yml", NodeDataAccessor.class);

    public boolean removeCard(String cardName)
    {
        if (!data.contains(cardName))
            return false;

        if (DEFAULT_CARD_NAMES.contains(cardName)) {
            ConsoleMessenger.error("Cannot remove default card!");
            return false;
        }

        data.erase(cardName);
        data.saveChanges();
        return true;
    }

    public boolean duplicateCard(String cardName)
    {
        if (!data.contains(cardName))
            return false;

        BranchNode card = data.getStorage(cardName);
        data.setStorage(cardName + "_copy", card);
        data.saveChanges();
        return true;
    }

    public boolean renameCard(String cardName, String newName)
    {
        if (DEFAULT_CARD_NAMES.contains(cardName) || DEFAULT_CARD_NAMES.contains(newName))
            return false;
        if (!data.contains(cardName))
            return false;
        if (data.contains(newName)) // Card with newName already exists
            return false;

        BranchNode card = data.getStorage(cardName);
        data.setStorage(newName, card);
        data.erase(cardName);
        data.saveChanges();
        return true;
    }

    public Set<String> getCardNames()
    {
        return data.getKeys();
    }

    public int getListMax(String cardName, String listName)
    {
        return data.getInt(cardName + "." + listName + ".max", MAX_ITEMS);
    }

    public int getListMin(String cardName, String listName)
    {
        return data.getInt(cardName + "." + listName + ".min", MIN_ITEMS);
    }

    public void setList(String cardName, String listName, int max, int min)
    {
        data.setInt(cardName + "." + listName + ".max", max);
        data.setInt(cardName + "." + listName + ".min", min);
        data.saveChanges();
    }

    public void removeList(String cardName, String listName)
    {
        data.erase(cardName + "." + listName);
    }

    public ItemTask getRandomItemTask(String cardName)
    {
        return getRandomItemTask(cardName, new Random());
    }

    public ItemTask getRandomItemTask(String cardName, @NotNull Random generator) {
        List<TaskData> tasks = new ArrayList<>();
        getListNames(cardName).forEach((l) -> tasks.addAll(listsData.getTasks(l, false, false)));

        List<TaskData> allItemTasks = tasks.stream().filter(task -> task instanceof ItemTask).toList();

        if (!allItemTasks.isEmpty())
            return (ItemTask)allItemTasks.get(Math.abs(generator.nextInt(allItemTasks.size())));
        else
            return new ItemTask(Material.DIRT, 1);
    }

    public TaskData getRandomTask(String cardName, @NotNull Random generator, boolean withStatistics, boolean withAdvancements) {
        List<TaskData> allTasks = getAllTasks(cardName, withStatistics, withAdvancements);

        if (!allTasks.isEmpty())
            return allTasks.get(Math.abs(generator.nextInt(allTasks.size())));
        else
            return new ItemTask(Material.DIRT, 1);
    }


    public List<TaskData> getAllTasks(String cardName, boolean withStatistics, boolean withAdvancements) {
        List<TaskData> tasks = new ArrayList<>();
        getListNames(cardName).forEach((l) -> tasks.addAll(listsData.getTasks(l, withStatistics, withAdvancements)));
        return tasks;
    }

    public Set<String> getListNames(String cardName)
    {
        if (!data.contains(cardName))
            return new HashSet<>();
        else
        {
            return data.get(cardName).getKeys();
        }
    }

    public List<String> getListsSortedByMin(String cardName)
    {
        List<String> result = new ArrayList<>(data.get(cardName).getKeys());
        result.sort((a, b) -> Integer.compare(getListMin(cardName, a), getListMin(cardName, b)));
        return result;
    }

    public List<String> getListsSortedByMax(String cardName)
    {
        List<String> result = new ArrayList<>(data.get(cardName).getKeys());
        result.sort((a, b) -> Integer.compare(getListMax(cardName, a), getListMax(cardName, b)));
        return result;
    }

    public TaskListData lists()
    {
        return listsData;
    }
}
