package io.github.steaf23.bingoreloaded.protocol.data;

import io.github.steaf23.bingoreloaded.protocol.codec.ByteCodec;
import io.github.steaf23.bingoreloaded.protocol.data.task.TaskDefinition;
import net.kyori.adventure.key.Key;

import java.util.List;

public record CreatorTaskSupplier(
		List<TaskDefinition> advancements,
		List<TaskDefinition> statistics,
		List<Key> availableItems, List<Key> availableBlocks, List<Key> availableEntities) {

	public static final ByteCodec<CreatorTaskSupplier> CODEC = ByteCodec.create(
			(buf, supplier) -> {
				TaskDefinition.CODEC.list().encode(buf, supplier.advancements());
				TaskDefinition.CODEC.list().encode(buf, supplier.statistics());
				ByteCodec.KEY.list().encode(buf, supplier.availableItems());
				ByteCodec.KEY.list().encode(buf, supplier.availableBlocks());
				ByteCodec.KEY.list().encode(buf, supplier.availableEntities());
			}, (buf) -> new CreatorTaskSupplier(
					TaskDefinition.CODEC.list().decode(buf),
					TaskDefinition.CODEC.list().decode(buf),
					ByteCodec.KEY.list().decode(buf),
					ByteCodec.KEY.list().decode(buf),
					ByteCodec.KEY.list().decode(buf)
			));
}
