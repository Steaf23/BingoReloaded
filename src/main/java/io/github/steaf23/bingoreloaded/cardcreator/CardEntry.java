package io.github.steaf23.bingoreloaded.cardcreator;

import io.github.steaf23.bingoreloaded.data.BingoSlotsData;
import org.bukkit.Material;

import java.util.*;

public class CardEntry
{
    private final String name;
    private final Map<String, Integer> itemLists = new HashMap<>();

    public CardEntry(String name)
    {
        this.name = name;
    }

    public void addItemList(String listName, int maxOccurrences)
    {
        itemLists.put(listName, maxOccurrences);
    }

    public void removeItemList(String listName)
    {
        itemLists.remove(listName);
    }

    public Map<String, Integer> getItemLists()
    {
        return itemLists;
    }

    public String getName()
    {
        return name;
    }

    public Material getRandomItem()
    {
        List<Material> allItems = new ArrayList<>();

        for (String list : itemLists.keySet())
        {
            allItems.addAll(BingoSlotsData.getSlots(list));
        }
        int randomIdx = (new Random().nextInt(allItems.size()));

        return allItems.get(randomIdx);
    }
}
