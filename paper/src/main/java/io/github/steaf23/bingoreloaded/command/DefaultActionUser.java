package io.github.steaf23.bingoreloaded.command;

import io.github.steaf23.bingoreloaded.lib.api.ActionUser;
import net.kyori.adventure.audience.Audience;

import java.util.List;

public class DefaultActionUser implements ActionUser {

	public final boolean restricted;

	public DefaultActionUser(boolean restricted) {
		this.restricted = restricted;
	}

	@Override
	public boolean hasPermission(String permission) {
		return !restricted;
	}

	@Override
	public Iterable<? extends Audience> audiences() {
		return List.of();
	}
}
