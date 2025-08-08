package io.github.steaf23.bingoreloaded.lib.menu;

import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SidebarHUD {
	private final Set<UUID> subscribers;
	private final Scoreboard board;
	private final Objective sidebar;

	public SidebarHUD(Component initialTitle) {
		this.board = Bukkit.getScoreboardManager().getNewScoreboard();
		this.sidebar = board.registerNewObjective("info", Criteria.DUMMY, initialTitle);
		sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
		sidebar.numberFormat(NumberFormat.blank());
		this.subscribers = new HashSet<>();

		for (int i = 0; i < 15; i++) {
			Team team = board.registerNewTeam("LINE_" + i);
			team.addEntry(getEntry(i));
			setText(i, null);
		}
	}

	public void clear() {
		for (int i = 0; i < 15; i++) {
			setText(i, null);
		}
	}

	public void applyToPlayer(Player player) {
		subscribers.add(player.getUniqueId());
		player.setScoreboard(board);
	}

	public void removeFromPlayer(Player player) {
		subscribers.remove(player.getUniqueId());
		player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
	}

	public void setTitle(Component title) {
		sidebar.displayName(title);
	}

	public void setText(int lineNumber, @Nullable Component text) {
		if (lineNumber < 0 || lineNumber > 14)
		{
			ConsoleMessenger.warn("Line index " + lineNumber + " out of range for text display (use 0-14)");
			return;
		}

		Team team = board.getTeam("LINE_" + lineNumber);
		team.prefix(text);
		if (text == null)
			board.resetScores(getEntry(lineNumber));
		else
			sidebar.getScore(getEntry(lineNumber)).setScore(0);
	}

	public boolean isAppliedToPlayer(Player player) {
		return subscribers.contains(player.getUniqueId());
	}

	public void removeAll() {
		for (UUID sub : subscribers) {
			Player player = Bukkit.getPlayer(sub);
			if (player != null) {
				removeFromPlayer(player);
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
