package io.github.steaf23.bingoreloaded.api.network.packets;

import io.github.steaf23.bingoreloaded.cards.hotswap.ExpiringHotswapTask;
import io.github.steaf23.bingoreloaded.cards.hotswap.HotswapTaskSlot;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class HotswapTasksWriter implements DataWriter<List<HotswapTaskSlot>> {

	public static final HotswapTasksWriter WRITER = new HotswapTasksWriter();

	@Override
	public void write(List<HotswapTaskSlot> hotswapTaskSlots, DataOutputStream to) throws IOException {
		to.writeInt(hotswapTaskSlots.size());
		for (HotswapTaskSlot holder : hotswapTaskSlots) {
			to.writeLong(holder.getFullTime());
			to.writeLong(holder.getCurrentTime());
			to.writeBoolean(holder.isRecovering());
			to.writeBoolean(holder instanceof ExpiringHotswapTask);
		}

	}
}
