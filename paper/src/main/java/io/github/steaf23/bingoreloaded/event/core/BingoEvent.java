package io.github.steaf23.bingoreloaded.event.core;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;

public abstract class BingoEvent extends Event
{
    private final BingoSession session;

    protected BingoEvent(BingoSession session)
    {
        this.session = session;
    }

    public BingoSession getSession() {
        return session;
    }
}
