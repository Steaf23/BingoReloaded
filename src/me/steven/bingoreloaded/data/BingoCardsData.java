package me.steven.bingoreloaded.data;

import me.steven.bingoreloaded.cardcreator.CardEntry;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Set;

public class BingoCardsData
{
    private static final YMLDataManager data = new YMLDataManager("cards.yml");

    public static CardEntry getOrCreateCard(String cardName)
    {
        CardEntry card = new CardEntry(cardName);
        if (!data.getConfig().contains(cardName))
        {
            data.getConfig().set(cardName, new String[]{});
        }
        else
        {
            ConfigurationSection section = data.getConfig().getConfigurationSection(cardName);
            if (section != null)
            {
                for (String itemList : section.getKeys(false))
                {
                    card.addItemList(itemList, getListValues(cardName, itemList));
                }
            }
        }

        return card;
    }

    public static void saveCard(CardEntry card)
    {
        for (String list : card.getItemLists().keySet())
        {
            data.getConfig().set(card.getName() + "." + list, card.getItemLists().get(list));
        }
        data.saveConfig();
    }

    public static void saveCard(String cardName)
    {
        saveCard(getOrCreateCard(cardName));
    }

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

    public static int getListValues(String cardName, String listName)
    {
        return data.getConfig().getInt(cardName + "." + listName);
    }
}
