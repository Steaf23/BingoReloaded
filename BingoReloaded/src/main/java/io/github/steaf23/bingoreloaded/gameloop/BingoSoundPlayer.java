package io.github.steaf23.bingoreloaded.gameloop;

import io.github.steaf23.bingoreloaded.event.BingoPlaySoundEvent;
import org.bukkit.Sound;

public class BingoSoundPlayer
{
    private final BingoSession session;

    public BingoSoundPlayer(BingoSession session) {
        this.session = session;
    }

    public void playSoundToEveryone(Sound sound, float loudness ,float pitch) {
        session.teamManager.getParticipants().forEach(p ->{
            p.sessionPlayer().ifPresent(player -> {
                player.playSound(player, sound, loudness, pitch);
            });
        });
    }
}
