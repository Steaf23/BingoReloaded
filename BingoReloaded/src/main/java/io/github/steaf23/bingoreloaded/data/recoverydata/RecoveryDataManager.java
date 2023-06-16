package io.github.steaf23.bingoreloaded.data.recoverydata;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.cards.BingoCard;
import io.github.steaf23.bingoreloaded.data.YmlDataManager;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.tasks.statistics.StatisticTracker;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;

public class RecoveryDataManager {
    private final YmlDataManager data = BingoReloaded.createYmlDataManager("recovery.yml");

    public void saveRecoveryData(BingoCard bingoCard, GameTimer timer, BingoSettings settings, StatisticTracker statisticTracker)
    {
        data.getConfig().set("recovery_data", new SerializableRecoveryData(bingoCard, timer, settings, statisticTracker));
        data.saveConfig();
    }

    public void clearRecoveryData() {
        data.getConfig().set("recovery_data", null);
        data.saveConfig();
    }

    /**
     * Loads recovery information from recovery.yml of the game ongoing prior to server shutting down
     * @param session
     * @return the saved recovery data
     */
    public RecoveryData loadRecoveryData(BingoSession session)
    {
        SerializableRecoveryData serializableRecoveryData = data.getConfig().getSerializable("recovery_data", SerializableRecoveryData.class);
        if (serializableRecoveryData == null) {
            return null;
        }
        data.getConfig().set("recovery_data", null);
        data.saveConfig();
        return serializableRecoveryData.toRecoveryData(session);
    }
}
