package io.github.steaf23.bingoreloaded.gameloop;

import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.gui.base.MenuManager;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public interface BingoGameManager
{
    @Nullable
    BingoSession getSession(String worldName);

    MenuManager getMenuManager();

    ConfigData getConfig();

    void onDisable();
}
