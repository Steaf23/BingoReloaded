package io.github.steaf23.bingoreloaded.gameloop;

import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.gui.base.MenuManager;
import org.bukkit.World;

import javax.annotation.Nullable;
import java.util.UUID;

public interface SessionManager
{
    @Nullable
    BingoSession getSession(String sessionName);
    @Nullable
    BingoSession getSessionFromWorld(World world);

    MenuManager getMenuManager();

    ConfigData getConfig();

    void onDisable();

    int sessionCount();
}
