package io.github.steaf23.bingoreloaded.lib.api;

public interface BiomeType {
	boolean isOcean();
	boolean isRiver();

//	/**
//	 * Counts RIVER as ocean biome!
//	 *
//	 * @param biome biome to check
//	 * @return true if this plugin consider biome to be an ocean-like biome
//	 */
//	private static boolean isOceanBiome(BiomeType biome) {
//		return biome == Biome.OCEAN ||
//				biome == Biome.RIVER ||
//				biome == Biome.DEEP_COLD_OCEAN ||
//				biome == Biome.COLD_OCEAN ||
//				biome == Biome.DEEP_OCEAN ||
//				biome == Biome.FROZEN_OCEAN ||
//				biome == Biome.DEEP_FROZEN_OCEAN ||
//				biome == Biome.LUKEWARM_OCEAN ||
//				biome == Biome.DEEP_LUKEWARM_OCEAN ||
//				biome == Biome.WARM_OCEAN;
//	}
}
