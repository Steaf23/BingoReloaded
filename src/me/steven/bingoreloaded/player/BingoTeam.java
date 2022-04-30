package me.steven.bingoreloaded.player;

import me.steven.bingoreloaded.gui.cards.BingoCard;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

public class BingoTeam
{
    public BingoCard card;
    public boolean outOfTheGame = false;

    public final Team team;

    public BingoTeam(Team team, BingoCard card)
    {
        this.team = team;
        this.card = card;
    }

    public String getName()
    {
        return team.getDisplayName();
    }

    public ChatColor getColor()
    {
        return team.getColor();
    }
}