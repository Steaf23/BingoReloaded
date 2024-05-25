package io.github.steaf23.easymenulib.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerHUD
{
    protected final SidebarHUD sidebar;
    private final UUID playerId;

    public PlayerHUD(UUID player) {
        this(player, new SidebarHUD(""));
    }

    public PlayerHUD(UUID player, SidebarHUD sidebar) {
        this.playerId = player;
        this.sidebar = sidebar;
    }

    public void update() {
        Player player = Bukkit.getPlayer(playerId);
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
