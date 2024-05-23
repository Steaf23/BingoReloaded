package io.github.steaf23.easymenulib.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SidebarHUD implements HeadsUpDisplay
{
    private final Set<UUID> subscribers;
    private Scoreboard board;
    private Objective sidebar;

    public SidebarHUD(HUDRegistry registry, String initialTitle) {
        this.board = Bukkit.getScoreboardManager().getNewScoreboard();
        this.sidebar = board.registerNewObjective("info", Criteria.DUMMY, initialTitle);
        sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.subscribers = new HashSet<>();

        for (int i = 0; i < 15; i++) {
            Team team = board.registerNewTeam("LINE_" + i);
            team.addEntry(getEntry(i));
            setText(i, "");
        }

        registry.addDisplay(this);
    }

    public void clear() {
        for (int i = 0; i < 15; i++) {
            setText(i, "");
        }
    }

    @Override
    public void subscribePlayer(Player player) {
        subscribers.add(player.getUniqueId());
        player.setScoreboard(board);
    }

    @Override
    public void unsubscribePlayer(Player player) {
        subscribers.remove(player.getUniqueId());
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    @Override
    public void setText(int lineNumber, String text) {
        if (lineNumber < 0 || lineNumber > 14)
        {
            Bukkit.getLogger().warning("Line index " + lineNumber + " out of range for text display (use 0-14)");
            return;
        }

        Team team = board.getTeam("LINE_" + lineNumber);
        team.setPrefix(text);
        if (text.isEmpty())
            board.resetScores(getEntry(lineNumber));
        else
            sidebar.getScore(getEntry(lineNumber)).setScore(0);
    }

    @Override
    public void setText(int pageNumber, int lineNumber, String text) {
        Bukkit.getLogger().info("SidebarHUD only contains 1 page");
        setText(lineNumber, text);
    }

    @Override
    public boolean isPlayerSubscribed(Player player) {
        return subscribers.contains(player.getUniqueId());
    }

    @Override
    public void unsubscribeAll() {
        for (UUID sub : subscribers) {
            Player player = Bukkit.getPlayer(sub);
            if (player != null) {
                unsubscribePlayer(player);
            }
        }
        subscribers.clear();
    }

    private String getEntry(int lineNumber) {
        if (lineNumber < 0 || lineNumber > 14) {
            return "";
        }
        return ChatColor.values()[lineNumber].toString();
    }
}
