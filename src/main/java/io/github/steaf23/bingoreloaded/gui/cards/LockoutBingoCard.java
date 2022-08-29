package io.github.steaf23.bingoreloaded.gui.cards;

import io.github.steaf23.bingoreloaded.BingoGame;
import io.github.steaf23.bingoreloaded.MessageSender;
import io.github.steaf23.bingoreloaded.item.BingoCardSlotCompleteEvent;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.item.tasks.AbstractBingoTask;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.player.TeamManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class LockoutBingoCard extends BingoCard
{
    public int teamCount;
    public TeamManager teamManager;
    public int currentMaxTasks;

    public LockoutBingoCard(CardSize size, BingoGame game, TeamManager manager)
    {
        super(size, game);
        this.teamManager = manager;
        this.currentMaxTasks = size.fullCardSize;
        this.teamCount = teamManager.getActiveTeams().size();

        InventoryItem cardInfo = new InventoryItem(0, Material.MAP, "Lockout Bingo Card", "Complete the most items to win.", "When an item has been completed", "it cannot be complete by any other team.");
        addOption(cardInfo);
    }

    @Override
    public LockoutBingoCard copy()
    {
        return this;
    }

    @Override
    public boolean hasBingo(BingoTeam team)
    {
        if (teamCount < 2)
        {
            return true;
        }
        int completeCount = getCompleteCount(team);
        return completeCount >= Math.floor(currentMaxTasks / (double) teamCount) + 1;
    }

    @EventHandler
    public void onCardSlotCompleteEvent(final BingoCardSlotCompleteEvent event)
    {
        // get the completeCount of the team with the most items.
        BingoTeam leadingTeam = teamManager.getLeadingTeam();
        BingoTeam losingTeam = teamManager.getLosingTeam();

        int itemsLeft = size.fullCardSize - getTotalCompleteCount();

        // if amount on items cannot get up to amount of items of the team with the most items, this team cannot win anymore.
        if (itemsLeft + getCompleteCount(losingTeam) < getCompleteCount(leadingTeam))
        {
            dropTeam(losingTeam);
        }
    }

    public void dropTeam(BingoTeam team)
    {
        MessageSender.sendAll("game.team.dropped",team.getColor() + team.getName());
        team.outOfTheGame = true;
        for (AbstractBingoTask task : tasks)
        {
            if (task.getWhoCompleted().equals(team))
            {
                task.voidTask();
                currentMaxTasks--;
            }
        }
        for (Player p : teamManager.getPlayersOfTeam(team))
        {
            p.setGameMode(GameMode.SPECTATOR);
        }
        teamCount--;
    }

    public int getTotalCompleteCount()
    {
        int total = 0;
        for (BingoTeam t : teamManager.getActiveTeams())
        {
            total += getCompleteCount(t);
        }
        return total;
    }
}
