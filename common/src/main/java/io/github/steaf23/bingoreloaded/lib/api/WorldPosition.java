package io.github.steaf23.bingoreloaded.lib.api;

import org.jetbrains.annotations.NotNull;

public class WorldPosition extends Position {

	private @NotNull WorldHandle world;

	public WorldPosition(WorldPosition fromOther) {
		super(fromOther);
		this.world = fromOther.world;
	}

	public WorldPosition(@NotNull WorldHandle world, Position fromOther) {
		super(fromOther);
		this.world = world;
	}

	public WorldPosition(@NotNull WorldHandle world, double x, double y, double z) {
		super(x, y, z);
		this.world = world;
	}

	public void takeFrom(WorldPosition position) {
		super.takeFrom(position);
		this.world = position.world;
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

	@Override
	public WorldPosition setX(double x) {
		super.setX(x);
		return this;
	}

	@Override
	public WorldPosition setY(double y) {
		super.setY(y);
		return this;
	}

	@Override
	public WorldPosition setZ(double z) {
		super.setZ(z);
		return this;
	}

	@Override
	public WorldPosition moveXBlocks(int amount) {
		super.moveXBlocks(amount);
		return this;
	}

	@Override
	public WorldPosition moveYBlocks(int amount) {
		super.moveYBlocks(amount);
		return this;
	}

	@Override
	public WorldPosition moveZBlocks(int amount) {
		super.moveZBlocks(amount);
		return this;
	}

	@Override
	public WorldPosition add(Position other) {
		return (WorldPosition) super.add(other);
	}

	@Override
	public WorldPosition floor() {
		return (WorldPosition)super.floor();
	}
}
