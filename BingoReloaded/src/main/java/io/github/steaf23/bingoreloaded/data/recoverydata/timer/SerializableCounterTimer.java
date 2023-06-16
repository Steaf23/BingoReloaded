package io.github.steaf23.bingoreloaded.data.recoverydata.timer;

import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.data.recoverydata.bingocard.SerializableCardSize;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.timer.CounterTimer;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

@SerializableAs("CounterTimer")
public record SerializableCounterTimer(
        int time
) implements ConfigurationSerializable, SerializableGameTimer {
    private static final String TIME_ID = "time";

    public SerializableCounterTimer(CounterTimer counterTimer) {
        this((int) counterTimer.getTime());
    }

    public static SerializableCounterTimer deserialize(Map<String, Object> data)
    {
        return new SerializableCounterTimer((Integer) data.getOrDefault(TIME_ID, 0));
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put(TIME_ID, time);

        return data;
    }

    @Override
    public GameTimer toGameTimer(BingoSession session) {
        CounterTimer timer = new CounterTimer();
        timer.updateTime(time);
        return timer;
    }
}
