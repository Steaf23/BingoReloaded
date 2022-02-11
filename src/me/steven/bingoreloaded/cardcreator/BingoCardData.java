package me.steven.bingoreloaded.cardcreator;

import me.steven.bingoreloaded.YMLDataManager;

public class BingoCardData
{
    private static final YMLDataManager data = new YMLDataManager("cards.yml");

    public void editCard(String cardName)
    {
        CardEntry card;
        if (!data.getConfig().contains(cardName))
        {
            card = new CardEntry(cardName);
            data.getConfig().set(cardName, new String[]{});
        }
    }

    public void removeCard(String cardName)
    {

    }
}
