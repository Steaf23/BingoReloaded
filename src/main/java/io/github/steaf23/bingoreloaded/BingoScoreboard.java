package io.github.steaf23.bingoreloaded;


import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.event.BingoEndedEvent;
import io.github.steaf23.bingoreloaded.event.BingoParticipantsUpdatedEvent;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.player.TeamManager;
import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class BingoScoreboard implements Listener
{
    private final Scoreboard itemCountBoard;
    private final TeamManager teamManager;

    public String worldName;

    public BingoScoreboard(String worldName)
    {
        this.itemCountBoard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.teamManager = new TeamManager(itemCountBoard, worldName);
        this.worldName = worldName;

        Objective itemObjective = itemCountBoard.registerNewObjective("item_count", "bingo_item_count", TranslationData.translate("menu.completed"));
        itemObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

        reset();
        Bukkit.getPluginManager().registerEvents(this, BingoReloaded.get());
    }

    public void updateItemCount()
    {
        if (!GameWorldManager.get().isGameWorldActive(worldName))
            return;

        BingoReloaded.scheduleTask(task ->
        {
            Objective objective = itemCountBoard.getObjective("item_count");
            if (objective == null)
                return;

            for (BingoTeam t : teamManager.getActiveTeams())
            {
                if (t.card != null)
                {
                    objective.getScore("" + t.getColor().chatColor).setScore(t.card.getCompleteCount(t));
                }
            }

            for (BingoPlayer p : teamManager.getParticipants())
            {
                if (p.gamePlayer().isPresent())
                {
                    setPlayerScoreboard(p, itemCountBoard);
                    continue;
                }
                else if (p.asOnlinePlayer().isPresent() && p.asOnlinePlayer().get().getScoreboard().equals(itemCountBoard))
                {
                    setPlayerScoreboard(p, Bukkit.getScoreboardManager().getMainScoreboard());
                }
            }
        });
    }

    public void reset()
    {
        BingoReloaded.scheduleTask(task -> {
            for (String entry : itemCountBoard.getEntries())
            {
                itemCountBoard.resetScores(entry);
            }

            for (BingoPlayer p : teamManager.getParticipants())
            {
                if (p.gamePlayer().isPresent())
                    setPlayerScoreboard(p, Bukkit.getScoreboardManager().getMainScoreboard());
            }
        });
    }

    public TeamManager getTeamManager()
    {
        return teamManager;
    }

    //TODO: Change to onPlayerRemoved/Added
    @EventHandler
    public void onParticipantsUpdated(final BingoParticipantsUpdatedEvent event)
    {
        if (!event.worldName.equals(worldName))
            return;

        updateItemCount();
    }

    private void setPlayerScoreboard(BingoPlayer player, Scoreboard scoreboard)
    {
        try
        {
            if (player.asOnlinePlayer().isPresent())
            {
                player.asOnlinePlayer().get().setScoreboard(scoreboard);
                player.asOnlinePlayer().get().setScoreboard(scoreboard);
            }
        }
        catch (IllegalStateException exc)
        {
            Message.warn("Cannot set scoreboard of invalid player: " + player.playerName());
        }
    }
}
