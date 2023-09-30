package io.github.steaf23.bingoreloaded.gameloop;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface SessionMember
{
    @Nullable
    public BingoSession getSession();

    public void setup();
}
