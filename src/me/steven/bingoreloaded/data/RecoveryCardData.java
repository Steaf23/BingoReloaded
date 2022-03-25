package me.steven.bingoreloaded.data;

import me.steven.bingoreloaded.BingoGameMode;
import me.steven.bingoreloaded.BingoReloaded;
import me.steven.bingoreloaded.gui.cards.BingoCard;
import me.steven.bingoreloaded.gui.cards.CardBuilder;
import me.steven.bingoreloaded.gui.cards.CardSize;
import me.steven.bingoreloaded.item.BingoItem;
import me.steven.bingoreloaded.player.TeamManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

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

        // add each team's card back
        ConfigurationSection cards = data.getConfig().getConfigurationSection("cards");
        if (cards == null) return false;
        for (String key : cards.getKeys(false))
        {
            Team cardOwner = teamManager.getTeamByName(key);

            BingoCard card = CardBuilder.fromMode(mode, size);
            success = fillCard(teamManager, cardOwner, card);
            teamManager.setCardForTeam(cardOwner, card);
        }

        return success;
    }

    public static boolean fillCard(TeamManager manager, Team team, BingoCard card)
    {
        List<?> itemNames = data.getConfig().getList("cards." + team.getDisplayName());
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
            Team completedBy = manager.getTeamByName((String) itemMap.get(itemName));

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

        for (Team t : manager.getActiveTeams().keySet())
        {
            List<Map<String, String>> items = new ArrayList<>();
            BingoCard card = manager.getActiveTeams().get(t);

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
            data.getConfig().set("cards." + t.getDisplayName(), items);
        }

        for (Team t : manager.getActiveTeams().keySet())
        {
            data.getConfig().set("teams." + t.getName(), t.getEntries().stream().toList());
        }

        data.saveConfig();
    }

    public static void markCardEnded(boolean value)
    {
        BingoReloaded.print("ENDING GAME");
        data.getConfig().set("ended", value);
        data.saveConfig();
    }

    public static Team getActiveTeamOfPlayer(Player player, TeamManager manager)
    {
        String name = player.getName();
        for (Team t : manager.getActiveTeams().keySet())
        {
            if (data.getConfig().getStringList("teams." + t.getName()).contains(name))
            {
                return t;
            }
        }
        return null;
    }
}
