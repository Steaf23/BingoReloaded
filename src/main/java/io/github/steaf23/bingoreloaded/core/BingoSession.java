package io.github.steaf23.bingoreloaded.core;

import io.github.steaf23.bingoreloaded.core.data.BingoCardsData;
import io.github.steaf23.bingoreloaded.core.event.BingoEndedEvent;
import io.github.steaf23.bingoreloaded.core.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.core.player.TeamManager;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;
import net.md_5.bungee.api.ChatColor;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * This class represents a session of bingo games on a single world(group).
 * A game world can/ should only have 1 session as dictated by the BingoGameManager
 */
public class BingoSession
{
    public final String worldName;
    public final BingoSettings settings;
    public final BingoScoreboard scoreboard;
    public final TeamManager teamManager;
    private BingoGame game;

    public BingoSession(String worldName, int maxTeamMembers)
    {
        this.worldName = worldName;
        this.settings = new BingoSettings(maxTeamMembers);
        this.scoreboard = new BingoScoreboard(this);
        this.teamManager = new TeamManager(scoreboard.getTeamBoard(), this);
        this.game = null;
    }

    public boolean isRunning()
    {
        return game != null;
    }

    public BingoGame game()
    {
        return game;
    }

    public BingoSettings settings()
    {
        return settings;
    }

    public void startGame()
    {
        BingoCardsData cardsData = new BingoCardsData();
        if (!cardsData.getCardNames().contains(settings.card))
        {
            new TranslatedMessage("game.start.no_card").color(ChatColor.RED).arg(settings.card).sendAll(this);
            return;
        }

        if (teamManager.getParticipants().size() == 0)
        {
            Message.log("Could not start bingo since no players have joined!", worldName);
            return;
        }
        teamManager.updateActivePlayers();

        // The game is started in the constructor
        game = new BingoGame(this);
    }

    public void endGame()
    {
        if (game == null) return;

        game.end(null);
    }

    public void removePlayer(@NonNull BingoPlayer player)
    {
        if (player.offline().isOnline())
        {
            player.takeEffects(true);
        }

        if (!teamManager.getParticipants().contains(player)) return;

        teamManager.removePlayerFromTeam(player);

        if (game != null)
            game.playerQuit(player);
    }

    public void handleGameEnded(final BingoEndedEvent event)
    {
        if (this != event.session) return;

        game = null;
    }
}
