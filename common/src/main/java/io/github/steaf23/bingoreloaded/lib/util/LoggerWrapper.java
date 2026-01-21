package io.github.steaf23.bingoreloaded.lib.util;

import net.kyori.adventure.text.Component;

public interface LoggerWrapper {
	void info(Component message);
	void warn(Component message);
	void error(Component message);
}
