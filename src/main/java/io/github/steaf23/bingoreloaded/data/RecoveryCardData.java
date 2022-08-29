package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.MessageSender;
import io.github.steaf23.bingoreloaded.gui.cards.BingoCard;
import io.github.steaf23.bingoreloaded.gui.cards.CardBuilder;
import io.github.steaf23.bingoreloaded.gui.cards.CardSize;
import io.github.steaf23.bingoreloaded.item.tasks.AbstractBingoTask;
import io.github.steaf23.bingoreloaded.item.tasks.ItemTask;
import io.github.steaf23.bingoreloaded.BingoGame;
import io.github.steaf23.bingoreloaded.BingoGamemode;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.player.TeamManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class RecoveryCardData
{
    private static final YmlDataManager data = new YmlDataManager("recovered.yml");

    public static boolean loadCards(BingoGame game)
    {
        boolean success = false;
        if (data.getConfig().getBoolean("ended")) return false;

        MessageSender.log(ChatColor.GREEN + "The last game did not finish, attempting to recover bingo card...");
        BingoGamemode mode = BingoGamemode.fromDataString(data.getConfig().getString("gamemode"));
        CardSize size = CardSize.fromWidth(data.getConfig().getInt("size"));

        if (game.getTeamManager().getActiveTeams().size() == 0)
        {
            MessageSender.log(ChatColor.RED + "Could not resume game, no teams have joined the last game?!");
            return false;
        }

        // add each team's card back
        ConfigurationSection cards = data.getConfig().getConfigurationSection("cards");
        if (cards == null) return false;
        for (String key : cards.getKeys(false))
        {
            BingoTeam cardOwner = game.getTeamManager().getTeamByName(key);

            BingoCard card = CardBuilder.fromMode(mode, size, game);
            success = fillCard(game.getTeamManager(), cardOwner, card);
            game.getTeamManager().setCardForTeam(cardOwner, card);
        }

        return success;
    }

    public static boolean fillCard(TeamManager manager, BingoTeam team, BingoCard card)
    {
        List<?> itemNames = data.getConfig().getList("cards." + team.getName());
        if (itemNames == null) return false;
        if (itemNames.size() != card.size.fullCardSize) return false;

        List<AbstractBingoTask> items = new ArrayList<>();
        for (Object entry : itemNames)
        {
            String itemName = "";
            if (!(entry instanceof HashMap<?, ?> itemMap)) continue;
            Optional<?> mapEntry = itemMap.keySet().stream().findAny();
            if (mapEntry.isPresent())
            {
                itemName = (String) mapEntry.get();
            }

            AbstractBingoTask item = new ItemTask(Material.getMaterial(itemName));
            BingoTeam completedBy = manager.getTeamByName((String) itemMap.get(itemName));

            if (completedBy != null)
            {
                item.complete(completedBy, 0);
            }
            items.add(item);
        }

        card.tasks = items;
        return true;
    }

    public static void saveCards(TeamManager manager, BingoGamemode mode, CardSize size)
    {
        data.getConfig().set("gamemode", mode.getDataName());
        data.getConfig().set("size", size.cardSize);

        data.getConfig().set("cards", null);

        for (BingoTeam t : manager.getActiveTeams())
        {
            List<Map<String, String>> items = new ArrayList<>();

            for (int i = 0; i < t.card.tasks.size(); i++)
            {
                AbstractBingoTask slot = t.card.tasks.get(i);

                if (slot.getWhoCompleted() == null)
                {
                    items.add(new HashMap<>() {{put(slot.item.getType().name(), "");}});
                }
                else
                {
                    items.add(new HashMap<>() {{put(slot.item.getType().name(), slot.getWhoCompleted().getName());}});
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
