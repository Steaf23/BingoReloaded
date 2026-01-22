package io.github.steaf23.bingoreloaded.platform;

import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.tick.TickingSystem;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.steaf23.bingoreloaded.lib.api.HytaleServerSoftware;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TaskTicker extends TickingSystem<EntityStore> {

	private final HytaleServerSoftware server;
	private final JavaPlugin plugin;
	private final List<TickingTask> tasks;

	public TaskTicker(HytaleServerSoftware server, JavaPlugin plugin) {
		this.server = server;
		this.plugin = plugin;
		this.tasks = new ArrayList<>();

		plugin.getEntityStoreRegistry().registerSystem(this);
	}

	@Override
	public void tick(float timeSinceLastTick, int index, @NotNull Store<EntityStore> store) {
		World world = store.getExternalData().getWorld();
		for (TickingTask ticker : new ArrayList<>(tasks)) {
			if (world.getWorldConfig().getUuid().equals(ticker.world())) {
				ticker.tick(timeSinceLastTick);
			}
		}
	}

	public void addTask(TickingTask ticker) {
		tasks.add(ticker);
	}

	public void cancelTask(TickingTask ticker) {
		tasks.remove(ticker);
	}
}
