package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformServer;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorageSerializer;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GlobalPosition extends Position {

	public static final DataStorageSerializer<GlobalPosition> SERIALIZER = DataStorageSerializer.of(GlobalPosition.class,
			(storage, value) -> {
				storage.setKey("world", value.dimension());
				storage.setDouble("x", value.x());
				storage.setDouble("y", value.y());
				storage.setDouble("z", value.z());
//        storage.setFloat("yaw", value.getYaw());
//        storage.setFloat("pitch", value.getPitch());
			}, storage -> {
				Key id = storage.getKey("world");
				if (id == null) {
					return null;
				}

				double x = storage.getDouble("x", 0.0D);
				double y = storage.getDouble("y", 0.0D);
				double z = storage.getDouble("z", 0.0D);
				float yaw = storage.getFloat("yaw", 0.0f);
				float pitch = storage.getFloat("pitch", 0.0f);
				return new GlobalPosition(id, x, y, z);
			});

	private double pitch;
	private double yaw;

	private @NotNull Key dimension;

	public GlobalPosition(GlobalPosition fromOther) {
		super(fromOther);
		takeFrom(fromOther);
	}

	public GlobalPosition(@NotNull Key dimension, double pitch, double yaw, Position fromOther) {
		super(fromOther);
		this.pitch = pitch;
		this.yaw = yaw;
		this.dimension = dimension;
	}

	public GlobalPosition(@NotNull Key dimension, double x, double y, double z) {
		this(dimension, x, y, z, 0.0D, 0.0D);
	}

	public GlobalPosition(@NotNull WorldHandle world, double x, double y, double z) {
		this(world.key(), x, y, z, 0.0D, 0.0D);
	}

	public GlobalPosition(@NotNull Key dimension, double x, double y, double z, double pitch, double yaw) {
		super(x, y, z);
		this.pitch = pitch;
		this.yaw = yaw;
		this.dimension = dimension;
	}

	public @Nullable WorldHandle world(PlatformServer server) {
		return server.getWorld(dimension);
	}

	public void takeFrom(GlobalPosition position) {
		super.takeFrom(position);
		this.pitch = position.pitch;
		this.yaw = position.yaw;
		this.dimension = position.dimension;
	}

	public double pitch() {
		return pitch;
	}

	public GlobalPosition setPitch(double pitch) {
		this.pitch = pitch;
		return this;
	}

	public double yaw() {
		return yaw;
	}

	public GlobalPosition setYaw(double yaw) {
		this.yaw = yaw;
		return this;
	}

	public Key dimension() {
		return dimension;
	}

	public void setDimension(@NotNull Key dimension) {
		this.dimension = dimension;
	}

	@Override
	public GlobalPosition clone() {
		return new GlobalPosition(this);
	}

	@Override
	public GlobalPosition setX(double x) {
		super.setX(x);
		return this;
	}

	@Override
	public GlobalPosition setY(double y) {
		super.setY(y);
		return this;
	}

	@Override
	public GlobalPosition setZ(double z) {
		super.setZ(z);
		return this;
	}

	@Override
	public GlobalPosition moveXBlocks(int amount) {
		super.moveXBlocks(amount);
		return this;
	}

	@Override
	public GlobalPosition moveYBlocks(int amount) {
		super.moveYBlocks(amount);
		return this;
	}

	@Override
	public GlobalPosition moveZBlocks(int amount) {
		super.moveZBlocks(amount);
		return this;
	}

	@Override
	public GlobalPosition add(Position other) {
		return (GlobalPosition) super.add(other);
	}

	@Override
	public GlobalPosition floor() {
		return (GlobalPosition)super.floor();
	}
}
