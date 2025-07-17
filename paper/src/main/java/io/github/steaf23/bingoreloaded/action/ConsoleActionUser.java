package io.github.steaf23.bingoreloaded.action;

import io.github.steaf23.bingoreloaded.lib.api.ActionUser;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConsoleActionUser implements ActionUser {

	private final ConsoleCommandSender console;

	public ConsoleActionUser(ConsoleCommandSender console) {
		this.console = console;
	}

	@Override
	public boolean hasPermission(String permission) {
		return console.hasPermission(permission);
	}

	@Override
	public @NotNull Iterable<? extends Audience> audiences() {
		return List.of(console);
	}
}
