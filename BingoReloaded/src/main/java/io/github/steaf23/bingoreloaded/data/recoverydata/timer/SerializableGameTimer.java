package io.github.steaf23.bingoreloaded.data.recoverydata.timer;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;
import org.bukkit.configuration.serialization.SerializableAs;



@SerializableAs("CounterTimer")
public interface SerializableGameTimer {
    GameTimer toGameTimer(BingoSession session);
}
