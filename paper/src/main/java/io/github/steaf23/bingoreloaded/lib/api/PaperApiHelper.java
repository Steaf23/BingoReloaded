package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformServer;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

public class PaperApiHelper {

	private PaperApiHelper(){};

	public static @Nullable GlobalPosition worldPosFromLocation(@Nullable Location location) {
		if (location == null) {
			return null;
		}
		return new GlobalPosition(location.getWorld().key(), location.x(), location.y(), location.z(), location.getPitch(), location.getYaw());
	}

	public static Location locationFromWorldPos(WorldHandle world, Position location) {
		return new Location(((WorldHandlePaper)world).handle(), location.x(), location.y(), location.z(), 0.0f, 0.0f);
	}


}
