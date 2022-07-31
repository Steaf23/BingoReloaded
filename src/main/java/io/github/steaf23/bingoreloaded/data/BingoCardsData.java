package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.item.ItemCardSlot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class BingoCardsData
{
    private static final YMLDataManager data = new YMLDataManager("cards.yml");

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
        return data.getConfig().getInt(cardName + "." + listName + ".max", 36);
    }

    public static int getListMin(String cardName, String listName)
    {
        return data.getConfig().getInt(cardName + "." + listName + ".min", 0);
    }

    public static void setList(String cardName, String listName, int max, int min)
    {
        data.getConfig().set(cardName + "." + listName, new HashMap<>(){{
            put("max", max);
            put("min", min);
        }});
        data.saveConfig();
    }

    public static ItemCardSlot getRandomItemSlot(String cardName)
    {
        String[] lists = getListsOnCard(cardName).toArray(new String[0]);
        int listIdx = new Random().nextInt(lists.length);
        return BingoSlotsData.getRandomItemSlot(lists[listIdx]);
    }

    public static Set<String> getListsOnCard(String cardName)
    {
        if (data.getConfig().getConfigurationSection(cardName) == null)
            return new HashSet<>();
        else
        {
            return data.getConfig().getConfigurationSection(cardName).getKeys(false);
        }
    }
}
