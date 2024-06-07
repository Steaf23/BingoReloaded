package io.github.steaf23.bingoreloaded.player.team;

import io.github.steaf23.bingoreloaded.cards.BingoCard;

import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.util.Message;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BingoTeam
{
    // Team used to display prefixes next to player display names
    public final Team team;
    private BingoCard card;
    public boolean outOfTheGame = false;
    private final String id;
    private final ChatColor color;
    private final String name;

    private Set<BingoParticipant> members;

    public BingoTeam(Team team, ChatColor color, String name) {
        this.id = team.getName();
        this.team = team;
        this.card = null;
        this.color = color;
        this.name = name;
        this.members = new HashSet<>();
    }

    public @Nullable BingoCard getCard() {
        return card;
    }

    public void setCard(BingoCard card) {
        this.card = card;
    }

    public String getIdentifier() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }

    public ChatColor getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public BaseComponent getColoredName() {
        return new ComponentBuilder(name)
                .color(color)
                .bold(true).build();
    }

    public Set<BingoParticipant> getMembers() {
        return members;
    }

    public void addMember(BingoParticipant player) {
        members.add(player);
        player.setTeam(this);
        team.addEntry(player.getDisplayName());
    }

    public void removeMember(BingoParticipant player) {
        members.remove(player);
        // Team is not registered anymore, if we try to unregister again, we will get errors
        if (team.getScoreboard().getTeam(id) != null) {
            team.removeEntry(player.getDisplayName());
        }
        player.setTeam(null);
    }

    public boolean hasMember(UUID memberId) {
        for (BingoParticipant member : members) {
            if (member.getId().equals(memberId)) {
                return true;
            }
        }
        return false;
    }

    public int getCompleteCount() {
        if (card == null) {
            Message.error("Cannot get complete count of team " + getColoredName() + " (Please report!)");
            return 0;
        }
        return card.getCompleteCount(this);
    }

    public Team getScoreboardTeam()
    {
        return team;
    }
}