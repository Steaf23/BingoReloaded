package io.github.steaf23.bingoreloaded.lib.api;

import com.hypixel.hytale.logger.HytaleLogger;
import io.github.steaf23.bingoreloaded.lib.util.LoggerWrapper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class HytaleLoggerWrapper implements LoggerWrapper {

	private final HytaleLogger logger = HytaleLogger.get("BingoReloaded");

	@Override
	public void info(@NotNull Component msg) {
		logger.atInfo().log(PlainTextComponentSerializer.plainText().serialize(msg));
	}

	@Override
	public void warn(@NotNull Component msg) {
		logger.atWarning().log(PlainTextComponentSerializer.plainText().serialize(msg));
	}

	@Override
	public void error(@NotNull Component msg) {
		logger.atSevere().log(PlainTextComponentSerializer.plainText().serialize(msg));
	}
}
