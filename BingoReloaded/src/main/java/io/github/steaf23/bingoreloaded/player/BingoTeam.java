package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.cards.BingoCard;

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
    public boolean outOfTheGame = false;

    private Set<BingoParticipant> members;


    private FlexColor color;

    public BingoTeam(Team team, BingoCard card, FlexColor color)
    {
        this.team = team;
        this.card = card;
        this.color = color;
        this.members = new HashSet<>();
    }

    public String getName()
    {
        return color.name;
    }

    @Override
    public String toString()
    {
        return getName();
    }

    public FlexColor getColor()
    {
        return color;
    }

    public ItemText getColoredName()
    {
        return new ItemText(color.getTranslatedName(), color.chatColor, ChatColor.BOLD);
    }

    public Set<BingoParticipant> getMembers()
    {
        return members;
    }

    public void addMember(BingoParticipant player)
    {
        members.add(player);
        team.addEntry(player.getId().toString());
    }

    public void removeMember(BingoParticipant player)
    {
        members.remove(player);
        team.removeEntry(player.getId().toString());
    }
}