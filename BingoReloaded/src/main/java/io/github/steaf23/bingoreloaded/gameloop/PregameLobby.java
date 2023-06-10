package io.github.steaf23.bingoreloaded.gameloop;

import io.github.steaf23.bingoreloaded.settings.SettingsPreviewBoard;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.event.BingoParticipantJoinEvent;
import io.github.steaf23.bingoreloaded.event.BingoParticipantLeaveEvent;
import io.github.steaf23.bingoreloaded.event.BingoSettingsUpdatedEvent;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PregameLobby implements GamePhase
{
    // Each player can cast a single vote for all categories, To keep track of this a VoteTicket will be made for every player that votes on something
    public class VoteTicket
    {
        public String gamemode = "";
        public String kit = "";
        public String card = "";
    }

    private final BingoSession session;
    private SettingsPreviewBoard settingsBoard;
    private final Map<UUID, VoteTicket> votes;

    public PregameLobby(BingoSession session)
    {
        this.session = session;
        this.settingsBoard = new SettingsPreviewBoard();
        settingsBoard.showSettings(session.settingsBuilder.view());
        this.votes = new HashMap<>();
    }

    public void voteGamemode(String gamemode, Player player)
    {
        if (session.teamManager.getBingoParticipant(player) == null)
        {
            Message.sendDebug("NO TEAM? Join one to vote!", player);
            return;
        }

        VoteTicket ticket = votes.getOrDefault(player.getUniqueId(), new VoteTicket());

        if (!gamemode.equals(ticket.gamemode))
        {
            ticket.gamemode = gamemode;
        }
        votes.put(player.getUniqueId(), ticket);
    }

    public void voteCard(String card, Player player)
    {
        if (session.teamManager.getBingoParticipant(player) == null)
        {
            new TranslatedMessage(BingoTranslation.VOTE_NO_TEAM).send(player);
            return;
        }

        VoteTicket ticket = votes.getOrDefault(player.getUniqueId(), new VoteTicket());

        if (!card.equals(ticket.card))
        {
            ticket.card = card;
        }
        votes.put(player.getUniqueId(), ticket);
    }

    public void voteKit(String kit, Player player)
    {
        if (session.teamManager.getBingoParticipant(player) == null)
        {
            new TranslatedMessage(BingoTranslation.VOTE_NO_TEAM).send(player);
            return;
        }

        VoteTicket ticket = votes.getOrDefault(player.getUniqueId(), new VoteTicket());

        if (!kit.equals(ticket.kit))
        {
            ticket.kit = kit;
        }
        votes.put(player.getUniqueId(), ticket);
    }

    public VoteTicket getVoteResult()
    {
        VoteTicket outcome = new VoteTicket();

        Map<String, Integer> gamemodes = new HashMap<>();
        Map<String, Integer> kits = new HashMap<>();
        Map<String, Integer> cards = new HashMap<>();

        for (UUID player : votes.keySet())
        {
            VoteTicket ticket = votes.get(player);
            gamemodes.put(ticket.gamemode, gamemodes.getOrDefault(ticket.gamemode, 0) + 1);
            kits.put(ticket.kit, kits.getOrDefault(ticket.kit, 0) + 1);
            cards.put(ticket.card, cards.getOrDefault(ticket.card, 0) + 1);
        }

        outcome.gamemode = getKeyWithHighestValue(gamemodes);
        outcome.kit = getKeyWithHighestValue(kits);
        outcome.card = getKeyWithHighestValue(cards);
        Message.log(outcome.gamemode + " " + outcome.kit + " " + outcome.card);

        return outcome;
    }

    private String getKeyWithHighestValue(Map<String, Integer> values)
    {
        String recordKey = "";
        for (var k : values.keySet())
        {
            if (recordKey.isEmpty() || values.get(k) > values.get(recordKey))
            {
                recordKey = k;
            }
        }
        return recordKey;
    }

    @Override
    public void handleParticipantJoined(final BingoParticipantJoinEvent event)
    {
        if (!(event.participant instanceof BingoPlayer player))
            return;

        player.gamePlayer().ifPresent(p -> settingsBoard.applyToPlayer(p));
    }

    @Override
    public void handleParticipantLeave(BingoParticipantLeaveEvent event)
    {
        votes.remove(event.participant.getId());
    }

    @Override
    public void handleSettingsUpdated(final BingoSettingsUpdatedEvent event)
    {
        settingsBoard.handleSettingsUpdated(event);
    }
}
