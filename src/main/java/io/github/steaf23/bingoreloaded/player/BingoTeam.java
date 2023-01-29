package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.gui.cards.BingoCard;

import io.github.steaf23.bingoreloaded.item.ItemText;
import io.github.steaf23.bingoreloaded.util.FlexColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.scoreboard.Team;

import java.util.HashSet;
import java.util.Set;

public class BingoTeam
{
    public final Team team;
    public BingoCard card;
    public Set<BingoPlayer> players;
    public boolean outOfTheGame = false;

    private FlexColor color;

    public BingoTeam(Team team, BingoCard card, FlexColor color)
    {
        this.team = team;
        this.card = card;
        this.color = color;
        this.players = new HashSet<>();
    }

    public String getName()
    {
        return color.name;
    }

    public FlexColor getColor()
    {
        return color;
    }

    public ItemText getColoredName()
    {
        return new ItemText(color.getTranslatedName(), color.chatColor, ChatColor.BOLD);
    }
}