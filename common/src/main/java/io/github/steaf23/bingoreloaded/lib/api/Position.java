package io.github.steaf23.bingoreloaded.lib.api;

import java.util.Random;

public class Position {
	private double x;
	private double y;
	private double z;

	public Position(Position fromOther) {
		x = fromOther.x;
		y = fromOther.y;
		z = fromOther.z;
	}

	public Position(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Position setX(double x) {
		this.x = x;
		return this;
	}

	public Position setY(double y) {
		this.y = y;
		return this;
	}

	public Position setZ(double z) {
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

	public Position moveXBlocks(int amount) {
		this.x += amount;
		return this;
	}

	public Position moveYBlocks(int amount) {
		this.y += amount;
		return this;
	}

	public Position moveZBlocks(int amount) {
		this.z += amount;
		return this;
	}

	public void takeFrom(Position other) {
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
	}

	@Override
	protected Position clone() {
		return new Position(this);
	}

	public Position add(Position other) {
		this.x += other.x;
		this.y += other.y;
		this.z += other.z;
		return this;
	}

	public Position multiply(double value) {
		this.x *= value;
		this.y *= value;
		this.z *= value;
		return this;
	}

	public Position multiply(double valueX, double valueY, double valueZ) {
		this.x *= valueX;
		this.y *= valueY;
		this.z *= valueZ;
		return this;
	}

	public Position multiply(Position position) {
		return multiply(position.x(), position.y(), position.z());
	}

	public static Position random() {
		Random random = new Random();
		return new Position(random.nextDouble(), random.nextDouble(), random.nextDouble());
	}
}
