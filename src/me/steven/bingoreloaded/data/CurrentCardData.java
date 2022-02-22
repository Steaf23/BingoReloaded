package me.steven.bingoreloaded.data;

import me.steven.bingoreloaded.BingoGameMode;
import me.steven.bingoreloaded.BingoReloaded;
import me.steven.bingoreloaded.gui.cards.BingoCard;
import me.steven.bingoreloaded.gui.cards.CardBuilder;
import me.steven.bingoreloaded.gui.cards.CardSize;
import me.steven.bingoreloaded.item.BingoItem;
import me.steven.bingoreloaded.player.TeamManager;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class CurrentCardData
{
    private static final YMLDataManager data = new YMLDataManager("recovered.yml");

    public static BingoCard loadCard(TeamManager teamManager)
    {
        if (data.getConfig().getBoolean("ended")) return null;

        BingoGameMode mode = data.getConfig().getObject("gamemode", BingoGameMode.class);
        CardSize size = data.getConfig().getObject("size", CardSize.class);

        List<?> itemNames = data.getConfig().getList("items");

        if (itemNames == null) return null;
        if (size == null) return null;
        if (itemNames.size() != size.fullCardSize) return null;

        List<BingoItem> items = new ArrayList<>();
        for (Object entry : itemNames)
        {
            if (!(entry instanceof String itemName)) return null;

            BingoItem item = new BingoItem(Material.getMaterial(itemName));
            Team completedBy = teamManager.getTeamByName(data.getConfig().getString("items" + "." + itemName));

            if (completedBy != null)
            {
                item.complete(completedBy);
            }
        }

        BingoCard card = CardBuilder.fromMode(mode, size);
        card.items = items;
        return card;
    }

    public static void saveCardData(BingoCard card, BingoGameMode mode, Team team)
    {
        data.getConfig().set("ended", false);
        data.getConfig().set("gamemode", mode);
        data.getConfig().set("size", card.size);

        List<Map<String, String>> items = new ArrayList<>();
        for (int i = 0; i < card.items.size(); i++)
        {
            BingoItem item = card.items.get(i);

            if (item.getWhoCompleted() == null)
            {
                items.add(new HashMap<>() {{put(item.item.name(), "");}});
            }
            else
            {
                items.add(new HashMap<>() {{put(item.item.name(), item.getWhoCompleted().getDisplayName());}});
            }
        }

        data.getConfig().set(team.getDisplayName(), items);
        data.saveConfig();
    }
}
