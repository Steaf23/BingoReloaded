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
        if (data.getConfig().contains(cardName))
        {
            data.getConfig().set(cardName, null);
            data.saveConfig();
            return true;
        }
        return false;
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
        List<BingoTask> allItemTasks = new ArrayList<>();
        getLists(cardName).forEach((l) -> allItemTasks.addAll(TaskListsData.getItemTasks(l)));

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
