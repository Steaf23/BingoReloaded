package io.github.steaf23.bingoreloaded.gameloop.phase;

import io.github.steaf23.bingoreloaded.event.*;
import io.github.steaf23.bingoreloaded.gameloop.SessionMember;
import org.bukkit.event.player.PlayerInteractEvent;

public interface GamePhase extends SessionMember
{
    /**
     * To be called when this phase needs to (forcefully) end.
     */
    void end();
    void handlePlayerJoinedSessionWorld(final PlayerJoinedSessionWorldEvent event);
    void handlePlayerLeftSessionWorld(final PlayerLeftSessionWorldEvent event);
    void handleSettingsUpdated(final BingoSettingsUpdatedEvent event);
    void handlePlayerInteract(final PlayerInteractEvent event);
}
