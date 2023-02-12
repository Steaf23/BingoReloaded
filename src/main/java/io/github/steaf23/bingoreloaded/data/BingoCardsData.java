package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.item.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.item.tasks.ItemTask;
import org.bukkit.Material;

import java.util.*;

public class BingoCardsData
{
    public static final int MAX_ITEMS = 36;
    public static final int MIN_ITEMS = 1;

    private static final YmlDataManager data = new YmlDataManager("cards.yml");

    public static boolean removeCard(String cardName)
    {
        if (!data.getConfig().contains(cardName))
            return false;

        data.getConfig().set(cardName, null);
        data.saveConfig();
        return true;
    }

    public static boolean duplicateCard(String cardName)
    {
        if (!data.getConfig().contains(cardName))
            return false;

        var card = data.getConfig().get(cardName);
        data.getConfig().set(cardName + "_copy", card);
        data.saveConfig();
        return true;
    }

    public static boolean renameCard(String cardName, String newName)
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

    public static Set<String> getCardNames()
    {
        return data.getConfig().getKeys(false);
    }

    public static int getListMax(String cardName, String listName)
    {
        return data.getConfig().getInt(cardName + "." + listName + ".max", MAX_ITEMS);
    }

    public static int getListMin(String cardName, String listName)
    {
        return data.getConfig().getInt(cardName + "." + listName + ".min", MIN_ITEMS);
    }

    public static void setList(String cardName, String listName, int max, int min)
    {
        data.getConfig().createSection(cardName + "." + listName, new HashMap<>(){{
            put("max", max);
            put("min", min);
        }});
        data.saveConfig();
    }

    public static void removeList(String cardName, String listName)
    {
        data.getConfig().set(cardName + "." + listName, null);
    }

    public static BingoTask getRandomItemTask(String cardName)
    {
        List<BingoTask> tasks = new ArrayList<>();
        getLists(cardName).forEach((l) -> tasks.addAll(TaskListsData.getItemTasks(l)));

        List<BingoTask> allItemTasks = tasks.stream().filter(task -> task.type == BingoTask.TaskType.ITEM).toList();

        if (allItemTasks.size() > 0)
            return allItemTasks.get(Math.abs(new Random().nextInt(allItemTasks.size())));
        else
            return new BingoTask(new ItemTask(Material.DIAMOND_HOE, 1));
    }

    public static Set<String> getLists(String cardName)
    {
        if (data.getConfig().getConfigurationSection(cardName) == null)
            return new HashSet<>();
        else
        {
            return data.getConfig().getConfigurationSection(cardName).getKeys(false);
        }
    }

    public static List<String> getListsSortedByMin(String cardName)
    {
        List<String> result = new ArrayList<>(data.getConfig().getConfigurationSection(cardName).getKeys(false));
        result.sort((a, b) -> Integer.compare(getListMin(cardName, a), getListMin(cardName, b)));
        return result;
    }

    public static List<String> getListsSortedByMax(String cardName)
    {
        List<String> result = new ArrayList<>(data.getConfig().getConfigurationSection(cardName).getKeys(false));
        result.sort((a, b) -> Integer.compare(getListMax(cardName, a), getListMax(cardName, b)));
        return result;
    }
}
