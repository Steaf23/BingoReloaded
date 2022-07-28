package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.gui.cards.BingoCard;
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