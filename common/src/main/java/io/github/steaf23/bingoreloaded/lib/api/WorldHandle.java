package io.github.steaf23.bingoreloaded.lib.api;

import java.util.List;
import java.util.UUID;

public interface WorldHandle {

	UUID uniqueId();
	List<PlayerHandle> players();
	WorldPosition spawnPoint();
}
