package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.event.core.BingoEvent;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;

public class PrepareNextBingoGameEvent extends BingoEvent
{
    public PrepareNextBingoGameEvent(BingoSession session) {
        super(session);
    }
}
