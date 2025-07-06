package io.github.steaf23.bingoreloaded.lib.api;

import org.bukkit.block.Biome;

public class BiomeTypePaper implements BiomeType {
	private final Biome biome;

	public BiomeTypePaper(Biome biome) {
		this.biome = biome;
	}

	public Biome handle() {
		return null;
	}

	@Override
	public boolean isOcean() {
		return biome == Biome.OCEAN ||
				biome == Biome.RIVER ||
				biome == Biome.DEEP_COLD_OCEAN ||
				biome == Biome.COLD_OCEAN ||
				biome == Biome.DEEP_OCEAN ||
				biome == Biome.FROZEN_OCEAN ||
				biome == Biome.DEEP_FROZEN_OCEAN ||
				biome == Biome.LUKEWARM_OCEAN ||
				biome == Biome.DEEP_LUKEWARM_OCEAN ||
				biome == Biome.WARM_OCEAN;
	}

	@Override
	public boolean isRiver() {
		return biome == Biome.RIVER;
	}
}
