package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.gui.cards.BingoCard;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.Arrays;
import java.util.List;

public class BingoTeam
{
    public final Team team;
    public BingoCard card;
    public boolean outOfTheGame = false;

    private ChatColor color;

    public BingoTeam(Team team, BingoCard card, ChatColor color)
    {
        this.team = team;
        this.card = card;
        this.color = color;
    }

    public String getName()
    {
        return team.getDisplayName();
    }

    public ChatColor getColor()
    {
        return color;
    }

    public List<OfflinePlayer> getPlayers()
    {
        return Arrays.stream(Bukkit.getOfflinePlayers()).filter(p -> team.getEntries().contains(p.getName())).toList();
    }
}