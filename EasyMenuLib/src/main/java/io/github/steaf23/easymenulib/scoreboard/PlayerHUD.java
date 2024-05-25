package io.github.steaf23.easymenulib.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerHUD
{
    protected final SidebarHUD sidebar;
    protected final boolean sidebarEnabled;
    private final UUID playerId;

    public PlayerHUD(UUID player, boolean enableSidebar) {
        this(player, enableSidebar, new SidebarHUD(""));
    }

    public PlayerHUD(UUID player, boolean enableSidebar, SidebarHUD sidebar) {
        this.playerId = player;
        this.sidebarEnabled = enableSidebar;
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
