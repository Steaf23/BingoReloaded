package io.github.steaf23.bingoreloaded.data.recoverydata.timer;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.util.timer.CountdownTimer;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@SerializableAs("CountdownTimer")
public class SerializableCountdownTimer extends SerializableGameTimer implements ConfigurationSerializable {
    private int medThreshold;
    private final String MED_ID = "med_threshold";
    private int lowThreshold;
    private final String LOW_ID = "low_threshold";
    public SerializableCountdownTimer(CountdownTimer countdownTimer) {
        super(countdownTimer);
        medThreshold = countdownTimer.medThreshold;
        lowThreshold = countdownTimer.lowThreshold;
    }

    public SerializableCountdownTimer(Map<String, Object> data) {
        super(data);
        medThreshold = (Integer) data.getOrDefault(MED_ID, 0);
        lowThreshold = (Integer) data.getOrDefault(LOW_ID, 0);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = super.serialize();

        data.put(MED_ID, medThreshold);
        data.put(LOW_ID, lowThreshold);

        return data;
    }

    @Override
    public GameTimer toGameTimer(BingoSession session) {
        return new CountdownTimer(time.intValue(), medThreshold, lowThreshold, session);
    }
}
