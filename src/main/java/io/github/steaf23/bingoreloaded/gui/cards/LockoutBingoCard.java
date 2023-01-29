package io.github.steaf23.bingoreloaded.gui.cards;

import io.github.steaf23.bingoreloaded.BingoGame;
import io.github.steaf23.bingoreloaded.BingoMessage;
import io.github.steaf23.bingoreloaded.GameWorldManager;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.event.BingoCardTaskCompleteEvent;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.item.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.player.TeamManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;

public class LockoutBingoCard extends BingoCard
{
    public int teamCount;
    public int currentMaxTasks;

    public LockoutBingoCard(CardSize size, int teamCount)
    {
        super(size);
        this.currentMaxTasks = size.fullCardSize;
        this.teamCount = teamCount;

        InventoryItem cardInfo = new InventoryItem(0, Material.MAP, TranslationData.itemName("menu.card.info_lockout"), TranslationData.itemDescription("menu.card.info_lockout"));
        addOption(cardInfo);
    }

    // Lockout cards cannot be copied since it should be the same instance for every player.
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
    public void onCardSlotCompleteEvent(final BingoCardTaskCompleteEvent event)
    {
        BingoGame game = GameWorldManager.get().getActiveGame(event.getWorldName());
        if (game == null)
        {
            return;
        }

        TeamManager teamManager = game.getTeamManager();
        // get the completeCount of the team with the most items.
        BingoTeam leadingTeam = teamManager.getLeadingTeam();
        BingoTeam losingTeam = teamManager.getLosingTeam();

        int itemsLeft = size.fullCardSize - getTotalCompleteCount(teamManager);

        // if amount on items cannot get up to amount of items of the team with the most items, this team cannot win anymore.
        if (itemsLeft + getCompleteCount(losingTeam) < getCompleteCount(leadingTeam))
        {
            dropTeam(losingTeam, teamManager);
        }
    }

    public void dropTeam(BingoTeam team, TeamManager teamManager)
    {
        new BingoMessage("game.team.dropped")
                .arg(team.getColoredName().asLegacyString())
                .sendAll();
        team.outOfTheGame = true;
        for (BingoTask task : tasks)
        {
            if (task.completedBy.isPresent() &&task.completedBy.get().equals(team))
            {
                task.setVoided(true);
                currentMaxTasks--;
            }
        }
        for (BingoPlayer p : teamManager.getPlayersOfTeam(team))
        {
            p.player().setGameMode(GameMode.SPECTATOR);
        }
        teamCount--;
    }

    public int getTotalCompleteCount(TeamManager teamManager)
    {
        int total = 0;
        for (BingoTeam t : teamManager.getActiveTeams())
        {
            total += getCompleteCount(t);
        }
        return total;
    }
}
