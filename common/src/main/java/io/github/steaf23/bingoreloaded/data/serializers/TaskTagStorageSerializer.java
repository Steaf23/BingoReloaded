package io.github.steaf23.bingoreloaded.data.serializers;

import io.github.steaf23.bingoreloaded.data.TaskTagData;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorageSerializer;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TaskTagStorageSerializer implements DataStorageSerializer<TaskTagData.TaskTag> {

	@Override
	public void toDataStorage(@NotNull DataStorage storage, @NotNull TaskTagData.TaskTag value) {
		storage.setString("color", value.color().asHexString());
	}

	@Override
	public @Nullable TaskTagData.TaskTag fromDataStorage(@NotNull DataStorage storage) {
		return new TaskTagData.TaskTag(TextColor.fromHexString(storage.getString("color", "#808080")));
	}
}
