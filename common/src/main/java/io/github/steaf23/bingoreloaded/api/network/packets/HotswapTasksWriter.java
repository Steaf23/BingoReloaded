package io.github.steaf23.bingoreloaded.api.network.packets;

import io.github.steaf23.bingoreloaded.cards.slot.ExpiringTickingTask;
import io.github.steaf23.bingoreloaded.cards.slot.TickingTaskSlot;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class HotswapTasksWriter implements DataWriter<List<TickingTaskSlot>> {

	public static final HotswapTasksWriter WRITER = new HotswapTasksWriter();

	@Override
	public void write(List<TickingTaskSlot> tickingTaskSlots, DataOutputStream to) throws IOException {
		to.writeInt(tickingTaskSlots.size());
		for (TickingTaskSlot holder : tickingTaskSlots) {
			to.writeLong(holder.getFullTime());
			to.writeLong(holder.getCurrentTime());
			to.writeBoolean(holder.isRecovering());
			to.writeBoolean(holder instanceof ExpiringTickingTask);
		}

	}
}
