package io.github.steaf23.bingoreloaded.lib.api;

public interface DimensionType {

	DimensionType OVERWORLD = of("minecraft:overworld");
	DimensionType NETHER = of("minecraft:nether");
	DimensionType THE_END = of("minecraft:the_end");

	static DimensionType of(String name) {

	}
}
