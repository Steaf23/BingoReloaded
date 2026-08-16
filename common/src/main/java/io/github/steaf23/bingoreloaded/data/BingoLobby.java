package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.lib.api.GlobalPosition;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorageSerializer;

public record BingoLobby(GlobalPosition spawnPosition) {

	public static final DataStorageSerializer<BingoLobby> SERIALIZER = DataStorageSerializer.of(BingoLobby.class,
			(storage, value) -> {
				storage.setGlobalPosition("spawn", value.spawnPosition());
			}, storage -> {
				GlobalPosition spawn = storage.getGlobalPosition("spawn");
				return new BingoLobby(spawn);
			});
}
