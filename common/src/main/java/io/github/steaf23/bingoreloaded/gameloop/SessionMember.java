package io.github.steaf23.bingoreloaded.gameloop;

import org.jetbrains.annotations.Nullable;

public interface SessionMember
{
    @Nullable
    BingoSession getSession();

    void setup();
}
