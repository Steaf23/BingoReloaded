package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.helper.YmlDataManager;
import io.github.steaf23.bingoreloaded.tasks.ItemTask;
import io.github.steaf23.bingoreloaded.tasks.TaskData;
import org.bukkit.Material;

import java.util.*;
import java.util.stream.Collectors;

public class BingoCardData
{
    private final TaskListData listsData = new TaskListData();
    public static final int MAX_ITEMS = 36;
    public static final int MIN_ITEMS = 1;

    private final YmlDataManager data = BingoReloaded.createYmlDataManager("data/cards.yml");

    public boolean removeCard(String cardName)
    {
        if (!data.getConfig().contains(cardName))
            return false;

        data.getConfig().set(cardName, null);
        data.saveConfig();
        return true;
    }

    public boolean duplicateCard(String cardName)
    {
        if (!data.getConfig().contains(cardName))
            return false;

        var card = data.getConfig().get(cardName);
        data.getConfig().set(cardName + "_copy", card);
        data.saveConfig();
        return true;
    }

    public boolean renameCard(String cardName, String newName)
    {
        var defaultCards = List.of("default_card");
        if (defaultCards.contains(cardName) || defaultCards.contains(newName))
            return false;
        if (!data.getConfig().contains(cardName))
            return false;
        if (data.getConfig().contains(newName)) // Card with newName already exists
            return false;

        var card = data.getConfig().get(cardName);
        data.getConfig().set(newName, card);
        data.getConfig().set(cardName, null);
        data.saveConfig();
        return true;
    }

    public Set<String> getCardNames()
    {
        return data.getConfig().getKeys(false);
    }

    public int getListMax(String cardName, String listName)
    {
        return data.getConfig().getInt(cardName + "." + listName + ".max", MAX_ITEMS);
    }

    public int getListMin(String cardName, String listName)
    {
        return data.getConfig().getInt(cardName + "." + listName + ".min", MIN_ITEMS);
    }

    public void setList(String cardName, String listName, int max, int min)
    {
        data.getConfig().createSection(cardName + "." + listName, new HashMap<>(){{
            put("max", max);
            put("min", min);
        }});
        data.saveConfig();
    }

    public void removeList(String cardName, String listName)
    {
        data.getConfig().set(cardName + "." + listName, null);
    }

    public ItemTask getRandomItemTask(String cardName)
    {
        List<TaskData> tasks = new ArrayList<>();
        getListNames(cardName).forEach((l) -> tasks.addAll(listsData.getTasks(l, false, false)));

        List<TaskData> allItemTasks = tasks.stream().filter(task -> task instanceof ItemTask).collect(Collectors.toList());

        if (allItemTasks.size() > 0)
            return (ItemTask)allItemTasks.get(Math.abs(new Random().nextInt(allItemTasks.size())));
        else
            return new ItemTask(Material.DIAMOND_HOE, 1);
    }

    public Set<String> getListNames(String cardName)
    {
        if (data.getConfig().getConfigurationSection(cardName) == null)
            return new HashSet<>();
        else
        {
            return data.getConfig().getConfigurationSection(cardName).getKeys(false);
        }
    }

    public List<String> getListsSortedByMin(String cardName)
    {
        List<String> result = new ArrayList<>(data.getConfig().getConfigurationSection(cardName).getKeys(false));
        result.sort((a, b) -> Integer.compare(getListMin(cardName, a), getListMin(cardName, b)));
        return result;
    }

    public List<String> getListsSortedByMax(String cardName)
    {
        List<String> result = new ArrayList<>(data.getConfig().getConfigurationSection(cardName).getKeys(false));
        result.sort((a, b) -> Integer.compare(getListMax(cardName, a), getListMax(cardName, b)));
        return result;
    }

    public TaskListData lists()
    {
        return listsData;
    }
}
