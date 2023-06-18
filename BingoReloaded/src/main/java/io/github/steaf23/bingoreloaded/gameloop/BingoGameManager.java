package io.github.steaf23.bingoreloaded.gameloop;

import io.github.steaf23.bingoreloaded.gui.base2.MenuManager;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public interface BingoGameManager
{
    @Nullable
    BingoSession getSession(Player player);

    MenuManager getMenuManager();

    void onDisable();
}
