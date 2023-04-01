package io.github.steaf23.bingoreloaded.gui.cards;

import io.github.steaf23.bingoreloaded.BingoGame;
import io.github.steaf23.bingoreloaded.Message;
import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.event.BingoCardSlotCompleteEvent;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.item.tasks.AbstractBingoTask;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.player.TeamManager;
import io.github.steaf23.bingoreloaded.util.FlexibleColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;

public class LockoutBingoCard extends BingoCard
{
    public int teamCount;
    public TeamManager teamManager;
    public int currentMaxTasks;

    public LockoutBingoCard(CardSize size, BingoGame game)
    {
        super(size, game);
        this.teamManager = game.getTeamManager();
        this.currentMaxTasks = size.fullCardSize;
        this.teamCount = teamManager.getActiveTeams().size();

        InventoryItem cardInfo = new InventoryItem(0, Material.MAP, TranslationData.itemName("menu.card.info_lockout"), TranslationData.itemDescription("menu.card.info_lockout"));
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
        new Message("game.team.dropped")
                .arg(FlexibleColor.fromName(team.getName()).getTranslation()).color(team.getColor()).bold()
                .sendAll();
        team.outOfTheGame = true;
        for (AbstractBingoTask task : tasks)
        {
            if (task.getWhoCompleted().equals(team))
            {
                task.voidTask();
                currentMaxTasks--;
            }
        }
//        for (Player p : teamManager.getPlayersOfTeam(team))
//        {
//            p.setGameMode(GameMode.SPECTATOR);
//        }
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
