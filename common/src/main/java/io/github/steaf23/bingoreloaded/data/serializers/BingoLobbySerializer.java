package io.github.steaf23.bingoreloaded.data.serializers;

import io.github.steaf23.bingoreloaded.data.BingoLobby;
import io.github.steaf23.bingoreloaded.lib.api.WorldPosition;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorageSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BingoLobbySerializer implements DataStorageSerializer<BingoLobby> {

	@Override
	public void toDataStorage(@NotNull DataStorage storage, @NotNull BingoLobby value) {
		storage.setWorldPosition("spawn", value.spawnPosition());
	}

	@Override
	public @Nullable BingoLobby fromDataStorage(@NotNull DataStorage storage) {
		WorldPosition spawn = storage.getWorldPosition("spawn");
		return new BingoLobby(spawn);
	}
}
