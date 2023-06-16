package io.github.steaf23.bingoreloaded.data.recoverydata.timer;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.util.timer.CounterTimer;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@SerializableAs("CounterTimer")
public class SerializableCounterTimer extends SerializableGameTimer implements ConfigurationSerializable {

    public SerializableCounterTimer(CounterTimer counterTimer) {
        super(counterTimer);
    }

    public SerializableCounterTimer(Map<String, Object> data) {
        super(data);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        return super.serialize();
    }

    @Override
    public GameTimer toGameTimer(BingoSession session) {
        CounterTimer timer = new CounterTimer();
        timer.updateTime(time);
        return timer;
    }
}
