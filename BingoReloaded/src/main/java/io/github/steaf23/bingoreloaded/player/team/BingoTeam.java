package io.github.steaf23.bingoreloaded.player.team;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.cards.TaskCard;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class BingoTeam implements ForwardingAudience
{
    private BingoSession session;
    private TaskCard card;
    public boolean outOfTheGame = false;
    private final String id;
    private final TextColor color;
    private final Component name;
    private final Component prefix;
    public Location teamLocation = null;

    public BingoTeam(BingoSession session, String identifier, TextColor color, Component name, Component prefix) {
        this.session = session;
        this.id = identifier;
        this.card = null;
        this.color = color;
        this.name = name;
        this.prefix = prefix;
    }

    public Optional<TaskCard> getCard() {
        return Optional.of(card);
    }

    public void setCard(TaskCard card) {
        this.card = card;
    }

    public String getIdentifier() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }

    public TextColor getColor() {
        return color;
    }

    public Component getName() {
        return name;
    }

    public Component getColoredName() {
        return name.color(color).decorate(TextDecoration.BOLD);
    }

    public Set<BingoParticipant> getMembers() {
        return getGameTeam().getEntries().stream()
                .map(p -> session.participantMap.get(Bukkit.getOfflinePlayer(p).getUniqueId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public void addMember(BingoParticipant player) {
        getGameTeam().addPlayer(Bukkit.getOfflinePlayer(player.getId()));
    }

    public void removeMember(@NotNull BingoParticipant player) {
        getGameTeam().removePlayer(Bukkit.getOfflinePlayer(player.getId()));
    }

    public boolean hasMember(UUID memberId) {
        return getGameTeam().hasPlayer(Bukkit.getOfflinePlayer(memberId));
    }

    public int getCompleteCount() {
        if (card == null) {
            ConsoleMessenger.bug("Cannot get complete count of team " + getColoredName(), this);
            return 0;
        }
        return card.getCompleteCount(this);
    }

    public Set<String> getMemberNames() {
        return getGameTeam().getEntries();
    }

    public Component getPrefix() {
        return prefix;
    }

    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        return getMembers();
    }

    public Team getGameTeam() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getTeam(id);
        if(team == null) {
            team = scoreboard.registerNewTeam(id);
            team.color(NamedTextColor.nearestTo(this.color));
        }
        return team;
    }
}