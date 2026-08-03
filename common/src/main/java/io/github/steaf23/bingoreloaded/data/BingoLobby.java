package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.lib.api.WorldPosition;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorageSerializer;

public record BingoLobby(WorldPosition spawnPosition) {

	public static final DataStorageSerializer<BingoLobby> SERIALIZER = DataStorageSerializer.of(BingoLobby.class,
			(storage, value) -> {
				storage.setWorldPosition("spawn", value.spawnPosition());
			}, storage -> {
				WorldPosition spawn = storage.getWorldPosition("spawn");
				return new BingoLobby(spawn);
			});
}
