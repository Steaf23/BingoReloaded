package io.github.steaf23.bingoreloaded.data.teleportspiral;

import io.github.steaf23.bingoreloaded.lib.data.core.DataStorageSerializer;

public record TeleportationSpiral(
    Point center,
    Point size,
    boolean skipOceanBiomes
) {
    public record Point(int x, int z) {
        @Override
        public String toString() {
            return "(%d, %d)".formatted(x, z);
        }

        public static final DataStorageSerializer<Point> SERIALIZER =
            DataStorageSerializer.of(
                (storage, value) -> {
                    storage.setInt("x", value.x);
                    storage.setInt("z", value.z);
                },
                storage ->
                    new Point(storage.getInt("x", 0), storage.getInt("z", 0))
            );
    }

    public static final DataStorageSerializer<TeleportationSpiral> SERIALIZER =
        DataStorageSerializer.of(
            (storage, value) -> {
                storage.setSerializable(
                    "center",
                    Point.SERIALIZER,
                    value.center
                );
                storage.setSerializable("size", Point.SERIALIZER, value.size);
                storage.getBoolean("skipOceanBiomes", true);
            },
            storage -> {
                return new TeleportationSpiral(
                    storage.getSerializable(
                        "center",
                        Point.SERIALIZER,
                        new Point(0, 0)
                    ),
                    storage.getSerializable(
                        "size",
                        Point.SERIALIZER,
                        new Point(20000, 20000)
                    ),
                    storage.getBoolean("skipOceanBiomes", true)
                );
            }
        );
}
