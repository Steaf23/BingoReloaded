package io.github.steaf23.bingoreloaded.data.teleportgrid;

import io.github.steaf23.bingoreloaded.lib.data.core.DataStorageSerializer;

import java.awt.*;

public record TeleportationGrid(Point cellSize, String finishedCommand, Point center, Point size, boolean skipOceanBiomes) {

	public record Point(int x, int z) {
		public static final DataStorageSerializer<Point> SERIALIZER = DataStorageSerializer.of(Point.class,
				(storage, value) -> {
					storage.setInt("x", value.x);
					storage.setInt("z", value.x);
				}, storage -> new Point(storage.getInt("x", 0), storage.getInt("z", 0)));
	}

	public static final DataStorageSerializer<TeleportationGrid> SERIALIZER = DataStorageSerializer.of(TeleportationGrid.class,
			(storage, value) -> {
				storage.setSerializable("cellSize", Point.SERIALIZER, value.cellSize);
				storage.setString("sendCommandWhenGridFinished", value.finishedCommand);
				storage.setSerializable("center", Point.SERIALIZER, value.center);
				storage.setSerializable("size", Point.SERIALIZER, value.size);
			}, storage -> {
				return new TeleportationGrid(
						storage.getSerializable("cellSize", Point.SERIALIZER, new Point(500, 500)),
						storage.getString("sendCommandWhenGridFinished", ""),
						storage.getSerializable("center", Point.SERIALIZER, new Point(0, 0)),
						storage.getSerializable("size", Point.SERIALIZER, new Point(50, 50)),
						storage.getBoolean("skipOceanBiomes", true)
				);
			});

}
