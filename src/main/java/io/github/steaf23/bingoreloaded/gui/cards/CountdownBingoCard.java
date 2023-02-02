package io.github.steaf23.bingoreloaded.gui.cards;

import io.github.steaf23.bingoreloaded.BingoGame;
import io.github.steaf23.bingoreloaded.GameWorldManager;
import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.event.CountdownTimerFinishedEvent;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.item.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.player.TeamManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CountdownBingoCard extends CompleteBingoCard
{
    public CountdownBingoCard(CardSize size)
    {
        super(size);
        InventoryItem cardInfoItem = new InventoryItem(0, Material.MAP, TranslationData.itemName("menu.card.info_countdown"), TranslationData.itemDescription("menu.card.info_countdown"));
        addOption(cardInfoItem);
    }

    @EventHandler
    public void onCountdownFinished(final CountdownTimerFinishedEvent event)
    {
        BingoGame game = GameWorldManager.get().getActiveGame(event.worldName);
        if (game == null)
            return;

        if (!game.getWorldName().equals(event.worldName))
        {
            return;
        }
        Set<BingoTeam> tiedTeams = new HashSet<>();
        TeamManager teamManager = game.getTeamManager();
        tiedTeams.add(teamManager.getLeadingTeam());

        int leadingPoints = teamManager.getCompleteCount(teamManager.getLeadingTeam());
        for (BingoTeam team : teamManager.getActiveTeams())
        {
            if (teamManager.getCompleteCount(team) == leadingPoints)
            {
                tiedTeams.add(team);
            }
            else
            {
                team.outOfTheGame = true;
            }
        }

        if (tiedTeams.size() == 1)
        {
            game.bingo(teamManager.getLeadingTeam());
        }
        else
        {
            game.startDeathMatch(3);
        }
    }

    @Override
    public CountdownBingoCard copy()
    {
        CountdownBingoCard card = new CountdownBingoCard(this.size);
        List<BingoTask> newTasks = new ArrayList<>();
        for (BingoTask item : tasks)
        {
            newTasks.add(item.copy());
        }
        card.tasks = newTasks;
        return card;
    }
}
