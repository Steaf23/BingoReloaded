package io.github.steaf23.bingoreloaded.lib.events;

import io.github.steaf23.bingoreloaded.lib.api.PaperApiHelper;
import io.github.steaf23.bingoreloaded.lib.api.PlayerHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.StackHandlePaper;
import io.github.steaf23.bingoreloaded.lib.event.PlatformEventDispatcher;
import io.github.steaf23.bingoreloaded.lib.event.EventResult;
import io.github.steaf23.bingoreloaded.lib.event.EventResults;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class EventListenerPaper implements Listener {

	private final JavaPlugin plugin;
	private final PlatformEventDispatcher dispatcher;

	EventListenerPaper(JavaPlugin plugin, PlatformEventDispatcher dispatcher) {
		this.plugin = plugin;
		this.dispatcher = dispatcher;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void handlePlayerMoveEvent(final PlayerMoveEvent event) {
		EventResult<?> result = dispatcher.sendPlayerMove(new PlayerHandlePaper(event.getPlayer()),
				PaperApiHelper.worldPosFromLocation(event.getFrom()),
				PaperApiHelper.worldPosFromLocation(event.getTo()));

		if (!result.cancel())
			return;

		Location newLoc = event.getTo();
        newLoc.setX(event.getFrom().getX());
        newLoc.setZ(event.getFrom().getZ());
        event.setTo(newLoc);
	}

	@EventHandler
	public void handlePlayerDeathEvent(final PlayerDeathEvent event) {
		List<? extends StackHandle> drops = event.getDrops().stream()
				.map(StackHandlePaper::new)
				.toList();
		EventResult<EventResults.PlayerDeathResult> result = dispatcher.sendPlayerDeath(new PlayerHandlePaper(event.getPlayer()), drops);

		event.getDrops().clear();

		if (result.data() == null || !result.data().keepInventory()) {
			event.getDrops().addAll(drops.stream()
					.map(s -> ((StackHandlePaper) s).handle())
					.toList());
		} else {
			event.setKeepInventory(true);
		}

		event.setCancelled(result.cancel());
	}

	@EventHandler
	public void handlePlayerRespawnEvent(final PlayerRespawnEvent event) {
		EventResult<EventResults.PlayerRespawnResult> result = dispatcher.sendPlayerRespawn(
				new PlayerHandlePaper(event.getPlayer()),
				event.isBedSpawn(),
				event.isAnchorSpawn());

		var data = result.data();
		if (data != null && data.overwriteSpawnPoint()) {
			if (data.newSpawnPoint() == null) {
				ConsoleMessenger.bug("New spawnpoint cannot be null when respawing player!", this);
				return;
			}
			event.setRespawnLocation(PaperApiHelper.locationFromWorldPos(data.newSpawnPoint()));
		}
	}

	// We need the game manager to handle us first to make sure no player information gets lost by accident.
	@EventHandler(priority = EventPriority.HIGHEST)
	public void handlePlayerTeleportEvent(final PlayerTeleportEvent event) {

	}
}
