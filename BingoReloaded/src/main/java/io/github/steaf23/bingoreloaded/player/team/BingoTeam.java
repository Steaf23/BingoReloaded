package io.github.steaf23.bingoreloaded.player.team;

import io.github.steaf23.bingoreloaded.cards.BingoCard;

import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BingoTeam implements ForwardingAudience
{
    private BingoCard card;
    public boolean outOfTheGame = false;
    private final String id;
    private final TextColor color;
    private final String name;
    private final Component prefix;

    private final Set<BingoParticipant> members;

    public BingoTeam(String identifier, TextColor color, String name, Component prefix) {
        this.id = identifier;
        this.card = null;
        this.color = color;
        this.name = name;
        this.members = new HashSet<>();
        this.prefix = prefix;
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

    public TextColor getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public Component getColoredName() {
        return Component.text(name).color(color).decorate(TextDecoration.BOLD);
    }

    public Set<BingoParticipant> getMembers() {
        return members;
    }

    public void addMember(BingoParticipant player) {
        members.add(player);
        player.setTeam(this);
    }

    public boolean removeMember(@NotNull BingoParticipant player) {
        boolean success = members.remove(player);
        player.setTeam(null);
        return success;
    }

    public boolean removeMember(@NotNull UUID uuid) {
        for (BingoParticipant participant : members) {
            if (participant.getId().equals(uuid)) {
                boolean success = members.remove(participant);
                participant.setTeam(null);
                return success;
            }
        }
        return false;
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
            ConsoleMessenger.bug("Cannot get complete count of team " + getColoredName(), this);
            return 0;
        }
        return card.getCompleteCount(this);
    }

    public Set<String> getMemberNames() {
        return members.stream()
                .map(participant -> {
                    if (participant.sessionPlayer().isEmpty()) {
                        return participant.getDisplayName();
                    } else {
                        return participant.sessionPlayer().get().getName();
                    }
                }).collect(Collectors.toSet());
    }

    public Component getPrefix() {
        return prefix;
    }

    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        return getMembers();
    }
}