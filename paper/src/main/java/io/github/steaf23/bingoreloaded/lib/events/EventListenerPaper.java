package io.github.steaf23.bingoreloaded.lib.events;

import io.github.steaf23.bingoreloaded.lib.api.AdvancementHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.EntityTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.InteractAction;
import io.github.steaf23.bingoreloaded.lib.api.PaperApiHelper;
import io.github.steaf23.bingoreloaded.lib.api.statistics.StatisticHandle;
import io.github.steaf23.bingoreloaded.lib.api.statistics.VanillaStatistic;
import io.github.steaf23.bingoreloaded.lib.api.statistics.VanillaStatistics;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformServer;
import io.github.steaf23.bingoreloaded.lib.api.PlayerHandlePaper;
import io.github.steaf23.bingoreloaded.lib.event.EventResult;
import io.github.steaf23.bingoreloaded.lib.event.EventResults;
import io.github.steaf23.bingoreloaded.lib.event.PlatformEventDispatcher;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class EventListenerPaper implements Listener {

	private final JavaPlugin plugin;
	private final PlatformServer server;
	private final PlatformEventDispatcher dispatcher;

	public EventListenerPaper(JavaPlugin plugin, PlatformServer server, PlatformEventDispatcher dispatcher) {
		this.plugin = plugin;
		this.server = server;
		this.dispatcher = dispatcher;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void handlePlayerMoveEvent(final PlayerMoveEvent event) {
		EventResult<?> result = dispatcher.sendPlayerMove(new PlayerHandlePaper(server, event.getPlayer()),
				PaperApiHelper.worldPosFromLocation(event.getFrom()),
				PaperApiHelper.worldPosFromLocation(event.getTo()));

		if (!result.consume())
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
		EventResult<EventResults.PlayerDeathResult> result = dispatcher.sendPlayerDeath(new PlayerHandlePaper(server, event.getPlayer()), drops);

		event.getDrops().clear();

		if (result.data() == null || !result.data().keepInventory()) {
			event.getDrops().addAll(drops.stream()
					.map(s -> ((StackHandlePaper) s).handle())
					.toList());
		} else {
			event.setKeepInventory(true);
		}

		event.setCancelled(result.consume());
	}

	@EventHandler
	public void handlePlayerRespawnEvent(final PlayerRespawnEvent event) {
		EventResult<EventResults.PlayerRespawnResult> result = dispatcher.sendPlayerRespawn(
				new PlayerHandlePaper(server, event.getPlayer()),
				event.isBedSpawn(),
				event.isAnchorSpawn());

		var data = result.data();
		if (data != null && data.overwriteSpawnPoint()) {
			if (data.newSpawnPoint() == null) {
				ConsoleMessenger.bug("New spawnpoint cannot be null when respawing player!", this);
				return;
			}
			event.setRespawnLocation(PaperApiHelper.locationFromWorldPos(server.getWorld(data.newSpawnPoint().dimension()), data.newSpawnPoint()));
		}
	}

	// We need the game manager to handle us first to make sure no player information gets lost by accident.
	@EventHandler(priority = EventPriority.HIGHEST)
	public void handlePlayerTeleportEvent(final PlayerTeleportEvent event) {
		EventResult<?> result = dispatcher.sendPlayerTeleport(
				new PlayerHandlePaper(server, event.getPlayer()),
				PaperApiHelper.worldPosFromLocation(event.getFrom()),
				PaperApiHelper.worldPosFromLocation(event.getTo()));

		if (result.consume()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void handlePlayerPortalEvent(final PlayerPortalEvent event) {
		EventResult<EventResults.PlayerMoveResult> result = dispatcher.sendPlayerPortal(
				new PlayerHandlePaper(server, event.getPlayer()),
				PaperApiHelper.worldPosFromLocation(event.getFrom()),
				PaperApiHelper.worldPosFromLocation(event.getTo()));

		if (result.consume()) {
			event.setCancelled(true);
		}

		if (result.data() == null) {
			return;
		}

		if (!result.data().overwritePosition() || result.data().newPosition() == null) {
			return;
		}

		event.setTo(PaperApiHelper.locationFromWorldPos(server.getWorld(result.data().newPosition().dimension()), result.data().newPosition()));
	}

	@EventHandler
	public void handlePlayerDroppedStackEvent(final PlayerDropItemEvent event) {
		EventResult<?> result = dispatcher.sendPlayerDroppedStack(
				new PlayerHandlePaper(server, event.getPlayer()),
				new StackHandlePaper(event.getItemDrop().getItemStack()));

		if (result.consume()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void handlePlayerItemDamaged(final PlayerItemDamageEvent event) {
		EventResult<?> result = dispatcher.sendPlayerStackDamaged(
				new PlayerHandlePaper(server, event.getPlayer()),
				new StackHandlePaper(event.getItem()));

		if (result.consume()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void handlePlayerInteracted(final PlayerInteractEvent event) {

		if (event.getClickedBlock() != null && event.getClickedBlock().getType().isInteractable()) {
			return;
		}

		EventResult<?> result = dispatcher.sendPlayerInteracted(
				new PlayerHandlePaper(server, event.getPlayer()),
				new StackHandlePaper(event.getItem()),
				new InteractAction(event.getAction().isLeftClick(), event.getAction().isRightClick(), event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_AIR));

		if (result.consume()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void handlePlayerDamageEvent(final EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player)) {
			return;
		}

		if (event.getCause() != EntityDamageEvent.DamageCause.FALL) {
			return;
		}

		EventResult<?> result = dispatcher.sendPlayerFallDamage(new PlayerHandlePaper(server, player));

		if (result.consume()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void handlePlayerJoinEvent(final PlayerJoinEvent event) {
		dispatcher.sendPlayerJoinsServer(new PlayerHandlePaper(server, event.getPlayer()));
	}

	@EventHandler
	public void handlePlayerQuitEvent(final PlayerQuitEvent event) {
		dispatcher.sendPlayerQuitsServer(new PlayerHandlePaper(server, event.getPlayer()));
	}

	@EventHandler
	public void handlePlayerBreakBlockEvent(final BlockBreakEvent event) {
		EventResult<?> result = dispatcher.sendPlayerBreaksBlock(
				new PlayerHandlePaper(server, event.getPlayer()),
				PaperApiHelper.worldPosFromLocation(event.getBlock().getLocation()),
				ItemTypePaper.of(event.getBlock().getType()));

		if (result.consume()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void handlePlayerPlaceBlockEvent(final BlockPlaceEvent event) {
		EventResult<?> result = dispatcher.sendPlayerPlacesBlock(
				new PlayerHandlePaper(server, event.getPlayer()),
				PaperApiHelper.worldPosFromLocation(event.getBlock().getLocation()),
				ItemTypePaper.of(event.getBlock().getType()));

		if (result.consume()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void handlePlayerStatisticIncrementEvent(final PlayerStatisticIncrementEvent event) {
		VanillaStatistic stat = VanillaStatistics.fromKey(event.getStatistic().getKey());
		EventResult<?> result = dispatcher.sendPlayerStatisticIncrement(
				new PlayerHandlePaper(server, event.getPlayer()),
				new StatisticHandle(stat,
						event.getEntityType() == null ? null : new EntityTypePaper(event.getEntityType()),
						event.getMaterial() == null ? null : new ItemTypePaper(event.getMaterial())),
				event.getNewValue());

		if (result.consume()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void handlePlayerAdvancementEvent(final PlayerAdvancementDoneEvent event) {
		dispatcher.sendPlayerAdvancementDone(
				new PlayerHandlePaper(server, event.getPlayer()),
				new AdvancementHandlePaper(event.getAdvancement()));
	}

	@EventHandler
	public void handlePlayerPickupItemEvent(final EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player player)) {
			return;
		}

		EventResult<EventResults.PlayerPickupResult> result = dispatcher.sendPlayerPickupStack(
				new PlayerHandlePaper(server, player),
				new StackHandlePaper(event.getItem().getItemStack()),
				PaperApiHelper.worldPosFromLocation(event.getItem().getLocation()));

		if (result.consume()) {
			event.setCancelled(true);
		}

		if (result.data() == null) {
			return;
		}

		if (result.data().removeItem()) {
			event.getItem().getItemStack().setAmount(0);
			return;
		}

		if (result.data().overwriteItem() && result.data().newItem() != null) {
			event.getItem().setItemStack(((StackHandlePaper)result.data().newItem()).handle());
		}
	}

	@EventHandler
	public void handlePlayerInventoryClick(final InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player player)) {
			return;
		}

		EventResult<?> result = dispatcher.sendPlayerInventoryClick(
				new PlayerHandlePaper(server, player),
				new StackHandlePaper(event.getWhoClicked().getItemOnCursor()),
				event.getSlotType() == InventoryType.SlotType.RESULT,
				event.isShiftClick());

		if (result.consume()) {
			event.setCancelled(true);
		}
	}
}
