package io.github.steaf23.bingoreloaded.gameloop;

import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.gui.base.MenuManager;

import javax.annotation.Nullable;

public interface SessionManager
{
    @Nullable
    BingoSession getSession(String sessionId);

    MenuManager getMenuManager();

    ConfigData getConfig();

    void onDisable();

    int sessionCount();
}
