package io.github.steaf23.bingoreloaded.data.recoverydata.timer;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.util.timer.CounterTimer;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("CounterTimer")
public abstract class SerializableGameTimer implements ConfigurationSerializable {
    Long time;
    private final String TIME_ID = "time";

    public SerializableGameTimer(GameTimer timer) {
        time = timer.getTime();
    }

    public SerializableGameTimer(Map<String, Object> data) {
        time = (Long)data.getOrDefault(TIME_ID, 0.0);;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put(TIME_ID, time);

        return data;
    }

    abstract public GameTimer toGameTimer(BingoSession session);
}
