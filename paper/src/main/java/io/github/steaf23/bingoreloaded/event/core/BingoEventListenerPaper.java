package io.github.steaf23.bingoreloaded.event.core;

import io.github.steaf23.bingoreloaded.api.BingoEventListener;
import io.github.steaf23.bingoreloaded.gameloop.GameManager;

public class BingoEventListenerPaper extends BingoEventListener {

    public BingoEventListenerPaper(GameManager gameManager, boolean disableAdvancements, boolean disableStatistics) {
        super(gameManager, disableAdvancements, disableStatistics);
    }
}
