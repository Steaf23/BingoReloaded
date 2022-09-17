package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.gui.cards.BingoCard;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.scoreboard.Team;

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
}