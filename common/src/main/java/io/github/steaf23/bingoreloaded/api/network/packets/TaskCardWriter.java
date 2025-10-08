package io.github.steaf23.bingoreloaded.api.network.packets;

import io.github.steaf23.bingoreloaded.cards.BingoTaskCard;
import io.github.steaf23.bingoreloaded.cards.CompleteTaskCard;
import io.github.steaf23.bingoreloaded.cards.HotswapTaskCard;
import io.github.steaf23.bingoreloaded.cards.LockoutTaskCard;
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
			switch (taskCard) {
				case BingoTaskCard ignored:
					DataWriter.writeString("bingoreloaded:gamemode/bingo", to);
					break;
				case CompleteTaskCard ignored:
					DataWriter.writeString("bingoreloaded:gamemode/complete", to);
					break;
				case LockoutTaskCard ignored:
					DataWriter.writeString("bingoreloaded:gamemode/lockout", to);
					break;
				case HotswapTaskCard ignored:
					DataWriter.writeString("bingoreloaded:gamemode/hotswap", to);
					break;
				default:
					throw new IllegalStateException("Unexpected value: " + taskCard);
			}

			taskCard.write(to);
		}
	}
}
