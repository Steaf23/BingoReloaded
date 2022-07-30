package io.github.steaf23.bingoreloaded.cardcreator;

import io.github.steaf23.bingoreloaded.data.BingoSlotsData;
import io.github.steaf23.bingoreloaded.item.AbstractCardSlot;
import org.bukkit.Material;

import java.util.*;

public class CardEntry
{
    private final String name;
    private final Map<String, Integer> slotLists = new HashMap<>();

    public CardEntry(String name)
    {
        this.name = name;
    }

    public void addItemList(String listName, int maxOccurrences)
    {
        slotLists.put(listName, maxOccurrences);
    }

    public void removeItemList(String listName)
    {
        slotLists.remove(listName);
    }

    public Map<String, Integer> getSlotLists()
    {
        return slotLists;
    }

    public String getName()
    {
        return name;
    }

    public AbstractCardSlot getRandomSlot()
    {
        List<AbstractCardSlot> allItems = new ArrayList<>();

        for (String list : slotLists.keySet())
        {
            allItems.addAll(BingoSlotsData.getAllSlots(list));
        }
        int randomIdx = (new Random().nextInt(allItems.size()));

        return allItems.get(randomIdx);
    }

    public Material getRandomItemSlot()
    {
        //TODO: GET RANDOM ITEMS FROM THE ITEMS IN THE LIST!
        return Material.BEDROCK;
    }
}
