package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import org.bukkit.Sound;

public class BingoPlaySoundEvent extends BingoEvent
{
    private final Sound sound;
    private final float loudness;
    private final float pitch;

    public BingoPlaySoundEvent(BingoSession session, Sound sound, float loudness, float pitch) {
        super(session);
        this.sound = sound;
        this.loudness = loudness;
        this.pitch = pitch;
    }

    public BingoPlaySoundEvent(BingoSession session, Sound sound) {
        this(session, sound, 0.8f, 1.0f);
    }

    public Sound getSound() {
        return sound;
    }

    public float getLoudness() {
        return loudness;
    }

    public float getPitch() {
        return pitch;
    }
}
