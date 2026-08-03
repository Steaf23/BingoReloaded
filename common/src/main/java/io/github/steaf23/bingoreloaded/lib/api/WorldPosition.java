package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.lib.data.core.DataStorageSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class WorldPosition extends Position {

	public static final DataStorageSerializer<WorldPosition> SERIALIZER = DataStorageSerializer.of(WorldPosition.class,
			(storage, value) -> {
				storage.setUUID("world", value.world().uniqueId());
				storage.setDouble("x", value.x());
				storage.setDouble("y", value.y());
				storage.setDouble("z", value.z());
//        storage.setFloat("yaw", value.getYaw());
//        storage.setFloat("pitch", value.getPitch());
			}, storage -> {
				UUID id = storage.getUUID("world");
				if (id == null) {
					return null;
				}
				WorldHandle world = PlatformResolver.get().getWorld(id);
				if (world == null) {
					return null;
				}

				double x = storage.getDouble("x", 0.0D);
				double y = storage.getDouble("y", 0.0D);
				double z = storage.getDouble("z", 0.0D);
				float yaw = storage.getFloat("yaw", 0.0f);
				float pitch = storage.getFloat("pitch", 0.0f);
				return new WorldPosition(world, x, y, z);
			});

	private double pitch;
	private double yaw;

	private @NotNull WorldHandle world;

	public WorldPosition(WorldPosition fromOther) {
		super(fromOther);
		this.pitch = fromOther.pitch;
		this.yaw = fromOther.yaw;
		this.world = fromOther.world;
	}

	public WorldPosition(@NotNull WorldHandle world, double pitch, double yaw, Position fromOther) {
		super(fromOther);
		this.pitch = pitch;
		this.yaw = yaw;
		this.world = world;
	}

	public WorldPosition(@NotNull WorldHandle world, double x, double y, double z) {
		this(world, x, y, z, 0.0D, 0.0D);
	}

	public WorldPosition(@NotNull WorldHandle world, double x, double y, double z, double pitch, double yaw) {
		super(x, y, z);
		this.pitch = pitch;
		this.yaw = yaw;
		this.world = world;
	}

	public void takeFrom(WorldPosition position) {
		super.takeFrom(position);
		this.pitch = position.pitch;
		this.yaw = position.yaw;
		this.world = position.world;
	}

	public double pitch() {
		return pitch;
	}

	public WorldPosition setPitch(double pitch) {
		this.pitch = pitch;
		return this;
	}

	public double yaw() {
		return yaw;
	}

	public WorldPosition setYaw(double yaw) {
		this.yaw = yaw;
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
