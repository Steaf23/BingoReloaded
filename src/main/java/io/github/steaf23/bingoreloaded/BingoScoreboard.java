package io.github.steaf23.bingoreloaded;


import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.event.BingoPlayerJoinEvent;
import io.github.steaf23.bingoreloaded.event.BingoPlayerLeaveEvent;
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
                updatePlayerScoreboard(p);
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

    @EventHandler
    public void onPlayerJoinsEvent(final BingoPlayerJoinEvent event)
    {
        if (!event.worldName.equals(worldName))
            return;

        Message.log("Player " + event.player.asOnlinePlayer().get().getDisplayName() + " joined the world", worldName);

        updatePlayerScoreboard(event.player);
    }

    @EventHandler
    public void onPlayerLeavesEvent(final BingoPlayerLeaveEvent event)
    {
        if (!event.worldName.equals(worldName))
            return;

        Message.log("Player " + event.player.asOnlinePlayer().get().getDisplayName() + " left the world", worldName);

        updatePlayerScoreboard(event.player);
    }


    private void updatePlayerScoreboard(BingoPlayer player)
    {
        if (player.gamePlayer().isPresent())
        {
            setPlayerScoreboard(player, itemCountBoard);
        }
        else if (player.asOnlinePlayer().isPresent() && player.asOnlinePlayer().get().getScoreboard().equals(itemCountBoard))
        {
            setPlayerScoreboard(player, Bukkit.getScoreboardManager().getMainScoreboard());
        }
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
