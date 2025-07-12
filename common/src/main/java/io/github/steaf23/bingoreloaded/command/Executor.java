package io.github.steaf23.bingoreloaded.command;

import net.kyori.adventure.audience.Audience;

public interface Executor {
	boolean execute(Audience executor, String... arguments);
}
