package me.steven.bingoreloaded.cardcreator;

import java.util.HashMap;
import java.util.Map;

public class CardEntry
{
    public final String name;
    private final Map<String, Integer> itemLists = new HashMap<>();

    public CardEntry(String name)
    {
        this.name = name;
    }

    private void addItemList(String listName, int maxOccurrences)
    {

    }

    private void removeItemList(String listName)
    {

    }
}
