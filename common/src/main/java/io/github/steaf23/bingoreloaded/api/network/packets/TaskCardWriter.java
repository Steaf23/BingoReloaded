package io.github.steaf23.bingoreloaded.api.network.packets;

import io.github.steaf23.bingoreloaded.cards.TaskCard;
import org.jetbrains.annotations.Nullable;

import java.io.DataOutputStream;
import java.io.IOException;

public class TaskCardWriter implements DataWriter<TaskCard> {

	public static final TaskCardWriter WRITER = new TaskCardWriter();

	@Override
	public void write(@Nullable TaskCard taskCard, DataOutputStream to) throws IOException {
		if (taskCard == null) {
			to.writeBoolean(false);
		}
		else {
			to.writeBoolean(true);
			taskCard.write(to);
		}
	}
}
