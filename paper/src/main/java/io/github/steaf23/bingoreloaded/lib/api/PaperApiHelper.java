package io.github.steaf23.bingoreloaded.lib.api;

import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

public class PaperApiHelper {

	private PaperApiHelper(){};

	public static @Nullable WorldPosition worldPosFromLocation(@Nullable Location location) {
		if (location == null) {
			return null;
		}
		return new WorldPosition(new WorldHandlePaper(location.getWorld()), location.x(), location.y(), location.z());
	}

	public static Location locationFromWorldPos(WorldPosition location) {
		return new Location(((WorldHandlePaper)location.world()).handle(), location.x(), location.y(), location.z());
	}


}
