package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import org.bukkit.event.Cancellable;

public class PrepareNextBingoGameEvent extends BingoEvent
{
    public PrepareNextBingoGameEvent(BingoSession session) {
        super(session);
    }
}
