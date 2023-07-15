package io.github.steaf23.bingoreloaded.gameloop;

import io.github.steaf23.bingoreloaded.event.*;
import org.bukkit.event.player.PlayerInteractEvent;

public interface GamePhase
{
    void setup();
    /**
     * To be called when this phase needs to (forcefully) end.
     */
    void end();
    void handlePlayerJoinedSessionWorld(final PlayerJoinedSessionWorldEvent event);
    void handlePlayerLeftSessionWorld(final PlayerLeftSessionWorldEvent event);
    void handleSettingsUpdated(final BingoSettingsUpdatedEvent event);
    void handlePlayerInteract(final PlayerInteractEvent event);
}
