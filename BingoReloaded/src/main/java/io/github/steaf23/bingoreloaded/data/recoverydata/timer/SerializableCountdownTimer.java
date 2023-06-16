package io.github.steaf23.bingoreloaded.data.recoverydata.timer;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.timer.CountdownTimer;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

@SerializableAs("CountdownTimer")
public record SerializableCountdownTimer (
        int time,
        int medThreshold,
        int lowThreshold
) implements ConfigurationSerializable, SerializableGameTimer {
    private static final String TIME_ID = "time";
    private static final String MED_ID = "med_threshold";
    private static final String LOW_ID = "low_threshold";

    public SerializableCountdownTimer(CountdownTimer countdownTimer) {
        this((int) countdownTimer.getTime(), countdownTimer.medThreshold, countdownTimer.lowThreshold);
    }

    public static SerializableCountdownTimer deserialize(Map<String, Object> data)
    {
        return new SerializableCountdownTimer(
                (Integer) data.getOrDefault(TIME_ID, 0),
                (Integer) data.getOrDefault(MED_ID, 0),
                (Integer) data.getOrDefault(LOW_ID, 0)
        );
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put(TIME_ID, time);data.put(MED_ID, medThreshold);
        data.put(LOW_ID, lowThreshold);

        return data;
    }

    public GameTimer toGameTimer(BingoSession session) {
        return new CountdownTimer(time, medThreshold, lowThreshold, session);
    }
}
