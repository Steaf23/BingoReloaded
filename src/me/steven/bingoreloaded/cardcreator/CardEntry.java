package me.steven.bingoreloaded.cardcreator;

import me.steven.bingoreloaded.data.BingoItemData;
import me.steven.bingoreloaded.item.BingoItem;
import org.bukkit.Material;
import org.bukkit.inventory.RecipeChoice;

import java.sql.Array;
import java.util.*;
import java.util.random.RandomGenerator;

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
            allItems.addAll(BingoItemData.getItems(list));
        }
        int randomIdx = (new Random().nextInt(allItems.size()));

        return allItems.get(randomIdx);
    }
}
