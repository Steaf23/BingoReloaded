package io.github.steaf23.bingoreloaded.item;

import io.github.steaf23.bingoreloaded.data.config.ConfigurationOption;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import net.kyori.adventure.key.Key;

public record ItemCooldown(Key group, CooldownFunction cooldownSupplier) {

	@FunctionalInterface
	public interface CooldownFunction {
		double apply(BingoGame game);
	}

	public static ItemCooldown configurableCooldown(Key group, ConfigurationOption<Double> option) {
		return new ItemCooldown(group, game -> game.getConfig().getOptionValue(option));
	}
}
