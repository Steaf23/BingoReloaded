package io.github.steaf23.bingoreloaded.tasks;

import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorageSerializer;
import io.github.steaf23.bingoreloaded.tasks.data.TaskData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class GameTaskSerializer implements DataStorageSerializer<GameTask> {

	@Override
	public void toDataStorage(@NotNull DataStorage storage, @NotNull GameTask value) {
		storage.setBoolean("voided", value.isVoided());
		storage.setUUID("completed_by", value.getCompletedByPlayer().isPresent() ? value.getCompletedByPlayer().get().getId() : null);
		storage.setLong("completed_at", value.completedAt);
		storage.setSerializable("task", TaskData.class, value.data);
	}

	@Override
	public @Nullable GameTask fromDataStorage(@NotNull DataStorage storage) {

		boolean voided = storage.getBoolean("voided", false);
		UUID completedByUUID = storage.getUUID("completed_by");
		long timeStr = storage.getLong("completed_at", -1L);
		TaskData data = storage.getSerializable("task", TaskData.class);
		GameTask task = new GameTask(data, GameTask.TaskDisplayMode.UNIQUE_TASK_ITEMS);

		task.setVoided(voided);
		task.completedAt = timeStr;
		//TODO: implement completedBy deserialization (need access to teamManager to get participant object).

		return task;
	}
}
