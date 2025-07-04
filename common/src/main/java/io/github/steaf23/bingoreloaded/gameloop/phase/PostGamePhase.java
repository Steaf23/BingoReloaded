package io.github.steaf23.bingoreloaded.gameloop.phase;

import io.github.steaf23.bingoreloaded.api.BingoEvents;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.lib.api.InteractAction;
import io.github.steaf23.bingoreloaded.lib.api.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.StackHandle;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.bingoreloaded.util.timer.CountdownTimer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.Nullable;

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
        sendRestartMessage(timer.getTime(), session);
    }

    @Override
    public void end() {
        for (BingoTeam team : session.teamManager.getActiveTeams()) {
            team.setCard(null);
        }
    }

    @Override
    public void handlePlayerJoinedSessionWorld(BingoEvents.PlayerEvent event) {
        sendRestartMessage(this.timer.getTime(), event.player());
    }

    @Override
    public void handlePlayerLeftSessionWorld(BingoEvents.PlayerEvent event) {

    }

    @Override
    public void handleSettingsUpdated(BingoEvents.SettingsUpdated event) {

    }

    @Override
    public boolean handlePlayerInteract(final PlayerHandle player, StackHandle stack, InteractAction action) {
        BingoParticipant participant = session.teamManager.getPlayerAsParticipant(player);
        if (participant == null || participant.sessionPlayer().isEmpty())
            return false;

        if (stack == null || stack.type().isAir())
            return false;

        if (!action.rightClick())
            return false;

        if (PlayerKit.CARD_ITEM.isCompareKeyEqual(stack)) {
            // Show bingo card to player
            participant.showCard(null);
            return true;
        }
        return false;
    }

    private void onTimerTicks(long timeLeft) {
        if (timeLeft == 0) {
            session.prepareNextGame();
            timer.stop();
        }
        else if (timeLeft == 5) {
            sendRestartMessage(timeLeft, session);
        }
    }

    public void sendRestartMessage(long timeLeft, Audience audience) {
        BingoMessage.POST_GAME_START.sendToAudience(audience, NamedTextColor.RED, Component.text(timeLeft).color(NamedTextColor.BLUE));
    }
}
