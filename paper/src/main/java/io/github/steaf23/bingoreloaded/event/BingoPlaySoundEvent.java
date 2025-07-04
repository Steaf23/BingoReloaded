package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.event.core.BingoEvent;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import net.kyori.adventure.sound.Sound;

public class BingoPlaySoundEvent extends BingoEvent
{
    private final Sound sound;
    public BingoPlaySoundEvent(BingoSession session, Sound sound) {
        super(session);
        this.sound = sound;
    }

    public BingoPlaySoundEvent(BingoSession session, Sound.Type soundType) {
        this(session, Sound.sound(soundType, Sound.Source.UI, 0.8f, 1.0f));
    }

    public Sound getSound() {
        return sound;
    }

}
