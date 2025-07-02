package io.github.steaf23.bingoreloaded.lib.scoreboard;

import io.github.steaf23.bingoreloaded.lib.api.PlayerHandle;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerHUD
{
    protected final SidebarHUD sidebar;
    private final UUID playerId;

    public PlayerHUD(UUID player, SidebarHUD sidebar) {
        this.playerId = player;
        this.sidebar = sidebar;
    }

    public void update() {
        PlayerHandle player = Bukkit.getPlayer(playerId);
        if (player == null) {
            return;
        }

        sidebar.applyToPlayer(player);
    }

    public void removeFromPlayer() {
        sidebar.removeAll();
    }

    public UUID getPlayerId() {
        return playerId;
    }
}
