package io.github.steaf23.bingoreloaded.gameloop.phase;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.event.BingoSettingsUpdatedEvent;
import io.github.steaf23.bingoreloaded.event.PlayerJoinedSessionWorldEvent;
import io.github.steaf23.bingoreloaded.event.PlayerLeftSessionWorldEvent;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;
import io.github.steaf23.bingoreloaded.util.timer.CountdownTimer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PostGamePhase implements GamePhase
{
    private final CountdownTimer timer;
    private final BingoSession session;

    public PostGamePhase(BingoSession session, int durationSeconds) {
        this.session = session;
        this.timer = new CountdownTimer(durationSeconds, session);
    }

    @Override
    public @Nullable BingoSession getSession() {
        return session;
    }

    @Override
    public void setup() {
        if (timer.getStartTime() <= 0) {
            session.prepareNextGame();
            timer.stop();
            return;
        }
        timer.start();
        timer.addNotifier(this::onTimerTicks);
        restartMessage(timer.getTime()).sendAll(session);
    }

    @Override
    public void end() {
        for (BingoTeam team : session.teamManager.getActiveTeams()) {
            team.card = null;
        }
    }

    @Override
    public void handlePlayerJoinedSessionWorld(PlayerJoinedSessionWorldEvent event) {
        restartMessage(this.timer.getTime()).send(event.getPlayer());
    }

    @Override
    public void handlePlayerLeftSessionWorld(PlayerLeftSessionWorldEvent event) {

    }

    @Override
    public void handleSettingsUpdated(BingoSettingsUpdatedEvent event) {

    }

    @Override
    public void handlePlayerInteract(PlayerInteractEvent event) {
        BingoParticipant participant = session.teamManager.getPlayerAsParticipant(event.getPlayer());
        if (participant == null || participant.sessionPlayer().isEmpty())
            return;

        if (event.getItem() == null || event.getItem().getType().isAir())
            return;

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (PlayerKit.CARD_ITEM.isCompareKeyEqual(event.getItem())) {
            // Show bingo card to player
            event.setCancelled(true);
            participant.showCard(null);
        }
    }

    private void onTimerTicks(long timeLeft) {
        if (timeLeft == 0) {
            session.prepareNextGame();
            timer.stop();
        }
        else if (timeLeft == 5) {
            restartMessage(timeLeft).sendAll(session);
        }
    }

    public Message restartMessage(long timeLeft) {
        return new TranslatedMessage(BingoTranslation.POST_GAME_START).color(ChatColor.RED).arg("" + timeLeft).color(ChatColor.BLUE);
    }
}
