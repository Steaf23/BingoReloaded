package io.github.steaf23.bingoreloaded.gameloop;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.event.BingoSettingsUpdatedEvent;
import io.github.steaf23.bingoreloaded.event.PlayerJoinedSessionWorldEvent;
import io.github.steaf23.bingoreloaded.event.PlayerLeftSessionWorldEvent;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;
import io.github.steaf23.bingoreloaded.util.timer.CountdownTimer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.player.PlayerInteractEvent;

public class PostGamePhase implements GamePhase
{
    private final CountdownTimer timer;
    private final BingoSession session;

    public PostGamePhase(BingoSession session, int durationSeconds) {
        this.session = session;
        this.timer = new CountdownTimer(durationSeconds, session);
    }

    @Override
    public void setup() {
        if (timer.getTime() <= 0) {
            session.prepareNextGame();
            timer.stop();
            return;
        }
        timer.start();
        timer.setNotifier(this::onTimerTicks);
        restartMessage(timer.getTime()).sendAll(session);
    }

    @Override
    public void end() {
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
