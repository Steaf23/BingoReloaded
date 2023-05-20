package io.github.steaf23.bingoreloaded;

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
    private BingoGame game;
    private SettingsPreviewBoard settingsBoard;

    public BingoSession(String worldName, ConfigData config)
    {
        this.worldName = worldName;
        this.config = config;
        this.settingsBuilder = new BingoSettingsBuilder(this);
        settingsBuilder.fromOther(new BingoSettingsData().getSettings(config.defaultSettings));
        this.scoreboard = new BingoScoreboard(this, config.showPlayerInScoreboard);
        this.teamManager = new TeamManager(scoreboard.getTeamBoard(), this);
        this.game = null;
        this.settingsBoard = new SettingsPreviewBoard();
        settingsBoard.showSettings(settingsBuilder.view());

        BingoReloadedCore.scheduleTask((t) -> {
            this.teamManager.addVirtualPlayerToTeam("testPlayer", "orange");
        }, 10);

    }

    public boolean isRunning()
    {
        return game != null;
    }

    public BingoGame game()
    {
        return game;
    }

    public void startGame()
    {
        if (isRunning())
        {
            return;
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

        teamManager.getParticipants().forEach(p ->
                p.gamePlayer().ifPresent(player -> settingsBoard.clearPlayerBoard(player)));

        // The game is started in the constructor
        game = new BingoGame(this, config);
    }

    public void endGame()
    {
        if (game == null) return;

        game.end(null);
    }

    public void removeParticipant(@NonNull BingoParticipant player)
    {
        teamManager.removeMemberFromTeam(player);
    }

    public void handleParticipantLeft(final BingoParticipantLeaveEvent event)
    {
        if (!(event.participant instanceof BingoPlayer player))
            return;

        if (player.offline().isOnline())
        {
            player.takeEffects(true);
            new TranslatedMessage(BingoTranslation.LEAVE).send(player.asOnlinePlayer().get());
        }

        if (game != null)
            game.playerQuit(player);
    }

    public void handleParticipantJoined(final BingoParticipantJoinEvent event)
    {
        if (!(event.participant instanceof BingoPlayer player))
            return;

        if (isRunning())
            player.giveEffects(settingsBuilder.view().effects(), config.gracePeriod);
        else
        {
            player.gamePlayer().ifPresent(p -> settingsBoard.applyToPlayer(p));
        }
    }

    public void handleGameEnded(final BingoEndedEvent event)
    {
        game = null;
    }

    public void handleSettingsUpdated(final BingoSettingsUpdatedEvent event)
    {
        settingsBoard.handleSettingsUpdated(event);
    }
}
