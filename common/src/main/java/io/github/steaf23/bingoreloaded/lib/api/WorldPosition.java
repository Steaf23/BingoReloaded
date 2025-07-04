package io.github.steaf23.bingoreloaded.lib.api;

import org.jetbrains.annotations.NotNull;

public class WorldPosition {

	private @NotNull WorldHandle world;
	private double x;
	private double y;
	private double z;

	public WorldPosition(WorldPosition fromOther) {
		this.world = fromOther.world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public WorldPosition(@NotNull WorldHandle world, double x, double y, double z) {
		this.world = world;
	}

	public void takeFrom(WorldPosition position) {
		this.world = position.world;
	}

	public WorldPosition setX(double x) {
		this.x = x;
		return this;
	}

	public WorldPosition setY(double y) {
		this.y = y;
		return this;
	}

	public WorldPosition setZ(double z) {
		this.z = z;
		return this;
	}

	public double x() {
		return x;
	}

	public double y() {
		return y;
	}

	public double z() {
		return z;
	}

	public int blockX() {
		return (int) x;
	}

	public int blockY() {
		return (int) y;
	}

	public int blockZ() {
		return (int) z;
	}

	public WorldPosition moveXBlocks(int amount) {
		this.x += amount;
		return this;
	}

	public WorldPosition moveYBlocks(int amount) {
		this.y += amount;
		return this;
	}

	public WorldPosition moveZBlocks(int amount) {
		this.z += amount;
		return this;
	}

	public WorldHandle world() {
		return world;
	}

	public void setWorld(@NotNull WorldHandle world) {
		this.world = world;
	}

	@Override
	public WorldPosition clone() {
		return new WorldPosition(this);
	}
}
