package io.github.steaf23.bingoreloaded.lib.api;

import net.kyori.adventure.audience.ForwardingAudience;

import java.util.Collection;

public interface ActionUser extends ForwardingAudience {

	boolean hasPermission(String permission);

	default boolean hasAnyPermission(Collection<String> permission) {
		for (String p : permission) {
			if (hasPermission(p)) {
				return true;
			}
		}

		return false;
	}
}
