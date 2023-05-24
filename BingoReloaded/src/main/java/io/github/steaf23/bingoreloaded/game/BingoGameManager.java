package io.github.steaf23.bingoreloaded.game;

import io.github.steaf23.bingoreloaded.event.BingoEventListener;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public interface BingoGameManager
{
    @Nullable
    BingoSession getSession(Player player);

    void onDisable();
}
