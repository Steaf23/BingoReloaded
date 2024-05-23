package io.github.steaf23.easymenulib.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class HUDRegistry implements Listener
{
    private final List<SidebarHUD> displays;

    public HUDRegistry() {
        displays = new ArrayList<>();
    }

    public void addDisplay(SidebarHUD display) {
        displays.add(display);
    }

    @EventHandler
    public void handlePlayerJoined(final PlayerJoinEvent event) {
        for (HeadsUpDisplay hud : displays) {
            if (hud.isPlayerSubscribed(event.getPlayer())) {
                // resubscribe after joining back to potentially update display if needed
                hud.subscribePlayer(event.getPlayer());
            }
        }
    }
}
