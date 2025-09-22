package io.github.steaf23.bingoreloaded.api.network.packets;

import io.github.steaf23.bingoreloaded.cards.hotswap.ExpiringHotswapTask;
import io.github.steaf23.bingoreloaded.cards.hotswap.HotswapTaskHolder;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class HotswapTasksWriter implements DataWriter<List<HotswapTaskHolder>> {

	public static final HotswapTasksWriter WRITER = new HotswapTasksWriter();

	@Override
	public void write(List<HotswapTaskHolder> hotswapTaskHolders, DataOutputStream to) throws IOException {
		to.writeInt(hotswapTaskHolders.size());
		for (HotswapTaskHolder holder : hotswapTaskHolders) {
			to.writeLong(holder.getFullTime());
			to.writeLong(holder.getCurrentTime());
			to.writeBoolean(holder.isRecovering());
			to.writeBoolean(holder instanceof ExpiringHotswapTask);
		}

	}
}
