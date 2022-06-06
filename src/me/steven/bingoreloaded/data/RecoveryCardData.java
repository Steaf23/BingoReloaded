package me.steven.bingoreloaded.data;

import me.steven.bingoreloaded.BingoGameMode;
import me.steven.bingoreloaded.BingoReloaded;
import me.steven.bingoreloaded.gui.cards.BingoCard;
import me.steven.bingoreloaded.gui.cards.CardBuilder;
import me.steven.bingoreloaded.gui.cards.CardSize;
import me.steven.bingoreloaded.item.BingoItem;
import me.steven.bingoreloaded.player.BingoTeam;
import me.steven.bingoreloaded.player.TeamManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class RecoveryCardData
{
    private static final YMLDataManager data = new YMLDataManager("recovered.yml");

    public static boolean loadCards(TeamManager teamManager)
    {
        boolean success = false;
        if (data.getConfig().getBoolean("ended")) return false;

        BingoReloaded.print(ChatColor.GREEN + "The last game did not finish, attempting to recover bingo card...");
        BingoGameMode mode = BingoGameMode.fromDataString(data.getConfig().getString("gamemode"));
        CardSize size = CardSize.fromWidth(data.getConfig().getInt("size"));

        if (teamManager.getActiveTeams().size() == 0)
        {
            BingoReloaded.print(ChatColor.RED + "Could not resume game, no teams have joined the last game?!");
            return false;
        }

        // add each team's card back
        ConfigurationSection cards = data.getConfig().getConfigurationSection("cards");
        if (cards == null) return false;
        for (String key : cards.getKeys(false))
        {
            BingoTeam cardOwner = teamManager.getTeamByName(key);

            BingoCard card = CardBuilder.fromMode(mode, size);
            success = fillCard(teamManager, cardOwner, card);
            teamManager.setCardForTeam(cardOwner, card);
        }

        return success;
    }

    public static boolean fillCard(TeamManager manager, BingoTeam team, BingoCard card)
    {
        List<?> itemNames = data.getConfig().getList("cards." + team.getName());
        if (itemNames == null) return false;
        if (itemNames.size() != card.size.fullCardSize) return false;

        List<BingoItem> items = new ArrayList<>();
        for (Object entry : itemNames)
        {
            String itemName = "";
            if (!(entry instanceof HashMap<?, ?> itemMap)) continue;
            Optional<?> mapEntry = itemMap.keySet().stream().findAny();
            if (mapEntry.isPresent())
            {
                itemName = (String) mapEntry.get();
            }

            BingoItem item = new BingoItem(Material.getMaterial(itemName));
            BingoTeam completedBy = manager.getTeamByName((String) itemMap.get(itemName));

            if (completedBy != null)
            {
                item.complete(completedBy, 0);
            }
            items.add(item);
        }

        card.items = items;
        return true;
    }

    public static void saveCards(TeamManager manager, BingoGameMode mode, CardSize size)
    {
        data.getConfig().set("gamemode", mode.getDataName());
        data.getConfig().set("size", size.cardSize);

        data.getConfig().set("cards", null);

        for (BingoTeam t : manager.getActiveTeams())
        {
            List<Map<String, String>> items = new ArrayList<>();

            for (int i = 0; i < t.card.items.size(); i++)
            {
                BingoItem item = t.card.items.get(i);

                if (item.getWhoCompleted() == null)
                {
                    items.add(new HashMap<>() {{put(item.item.name(), "");}});
                }
                else
                {
                    items.add(new HashMap<>() {{put(item.item.name(), item.getWhoCompleted().getName());}});
                }
            }
            data.getConfig().set("cards." + t.getName(), items);
        }

        for (BingoTeam t : manager.getActiveTeams())
        {
            data.getConfig().set("teams." + t.getName(), t.team.getEntries().stream().toList());
        }

        data.saveConfig();
    }

    public static void markCardEnded(boolean value)
    {
        data.getConfig().set("ended", value);
        data.saveConfig();
    }

    public static BingoTeam getActiveTeamOfPlayer(Player player, TeamManager manager)
    {
        String name = player.getName();
        for (BingoTeam t : manager.getActiveTeams())
        {
            if (data.getConfig().getStringList("teams." + t.getName()).contains(name))
            {
                return t;
            }
        }
        return null;
    }
}
