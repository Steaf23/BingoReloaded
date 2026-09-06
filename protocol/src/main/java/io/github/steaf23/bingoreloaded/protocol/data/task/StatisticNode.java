package io.github.steaf23.bingoreloaded.protocol.data.task;

import io.github.steaf23.bingoreloaded.protocol.codec.ByteCodec;
import net.kyori.adventure.key.Key;

public record StatisticNode(TaskId.Statistic statistic, String name, boolean isCustom, Key customIcon) {

	public static final ByteCodec<StatisticNode> CODEC = ByteCodec.create(
			(buf, node) -> {
				TaskId.CODEC.encode(buf, node.statistic);
				ByteCodec.STRING.encode(buf, node.name);
				ByteCodec.BOOL.encode(buf, node.isCustom);
				ByteCodec.KEY.encode(buf, node.customIcon);
			}, (buf) -> new StatisticNode(
					(TaskId.Statistic)TaskId.CODEC.decode(buf),
					ByteCodec.STRING.decode(buf),
					ByteCodec.BOOL.decode(buf),
					ByteCodec.KEY.decode(buf))
	);
}
