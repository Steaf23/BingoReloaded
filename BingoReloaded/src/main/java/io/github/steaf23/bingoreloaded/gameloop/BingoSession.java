package io.github.steaf23.bingoreloaded.gameloop;

import io.github.steaf23.bingoreloaded.*;
import io.github.steaf23.bingoreloaded.data.BingoCardsData;
import io.github.steaf23.bingoreloaded.data.BingoSettingsData;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.event.BingoEndedEvent;
import io.github.steaf23.bingoreloaded.event.BingoParticipantJoinEvent;
import io.github.steaf23.bingoreloaded.event.BingoParticipantLeaveEvent;
import io.github.steaf23.bingoreloaded.event.BingoSettingsUpdatedEvent;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.TeamManager;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;
import net.md_5.bungee.api.ChatColor;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * This class represents a session of bingo games on a single world(group).
 * A game world can/ should only have 1 session since bingo events for a session are propagated through the world
 */
public class BingoSession
{
    public final String worldName;
    public final BingoSettingsBuilder settingsBuilder;
    public final BingoScoreboard scoreboard;
    public final TeamManager teamManager;
    private final ConfigData config;
    private GamePhase phase;

    public BingoSession(String worldName, ConfigData config)
    {
        this.worldName = worldName;
        this.config = config;
        this.settingsBuilder = new BingoSettingsBuilder(this);
        settingsBuilder.fromOther(new BingoSettingsData().getSettings(config.defaultSettingsPreset));
        this.scoreboard = new BingoScoreboard(this, config.showPlayerInScoreboard);
        this.teamManager = new TeamManager(scoreboard.getTeamBoard(), this);
        this.phase = new PregameLobby(this);

        BingoReloaded.scheduleTask((t) -> {
            this.teamManager.addVirtualPlayerToTeam("testPlayer", "orange");
        }, 10);

    }

    public boolean isRunning()
    {
        return phase instanceof BingoGame;
    }

    public GamePhase phase()
    {
        return phase;
    }

    public void startGame()
    {
        if (isRunning())
        {
            return;
        }

        PregameLobby lobby = ((PregameLobby) phase);

        if (config.useVoteSystem)
        {
            PregameLobby.VoteTicket voteResult = lobby.getVoteResult();
            settingsBuilder.applyVoteResult(voteResult);
        }

        BingoCardsData cardsData = new BingoCardsData();
        BingoSettings settings = settingsBuilder.view();
        if (!cardsData.getCardNames().contains(settings.card()))
        {
            new TranslatedMessage(BingoTranslation.NO_CARD).color(ChatColor.RED).arg(settings.card()).sendAll(this);
            return;
        }

        if (teamManager.getParticipants().size() == 0)
        {
            Message.log("Could not start bingo since no players have joined!", worldName);
            return;
        }
        teamManager.updateActivePlayers();

        scoreboard.updateTeamScores();

        // The game is started in the constructor
        phase = new BingoGame(this, config);
    }

    public void endGame()
    {
        if (!isRunning()) return;

        ((BingoGame)phase).end(null);
    }

    public void removeParticipant(@NonNull BingoParticipant player)
    {
        teamManager.removeMemberFromTeam(player);
    }

    public void handleParticipantLeave(final BingoParticipantLeaveEvent event)
    {
        if (!(event.participant instanceof BingoPlayer player))
            return;

        if (player.offline().isOnline())
        {
            player.takeEffects(true);
            new TranslatedMessage(BingoTranslation.LEAVE).send(player.asOnlinePlayer().get());
        }

        phase.handleParticipantLeave(event);
    }

    public void handleParticipantJoined(final BingoParticipantJoinEvent event)
    {
        phase.handleParticipantJoined(event);
    }

    public void handleGameEnded(final BingoEndedEvent event)
    {
        phase = new PregameLobby(this);
    }

    public void handleSettingsUpdated(final BingoSettingsUpdatedEvent event)
    {
        phase.handleSettingsUpdated(event);
    }
}
