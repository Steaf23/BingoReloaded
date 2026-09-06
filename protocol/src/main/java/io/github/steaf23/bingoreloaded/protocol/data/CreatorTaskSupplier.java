package io.github.steaf23.bingoreloaded.protocol.data;

import io.github.steaf23.bingoreloaded.protocol.codec.ByteCodec;
import io.github.steaf23.bingoreloaded.protocol.data.task.AdvancementNode;
import io.github.steaf23.bingoreloaded.protocol.data.task.StatisticNode;
import net.kyori.adventure.key.Key;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record CreatorTaskSupplier(
		Set<Key> items,
		Set<Key> validBlocks,
		Set<Key> validEntityTypes,
		// Advancements
		Map<Key, AdvancementNode> advancements,
		// Statistics
		List<StatisticNode> statistics
) {

	public static final ByteCodec<CreatorTaskSupplier> CODEC = ByteCodec.create(
			(buf, supplier) -> {
				ByteCodec.KEY.hashSet().encode(buf, supplier.items);
				ByteCodec.KEY.hashSet().encode(buf, supplier.validBlocks);
				ByteCodec.KEY.hashSet().encode(buf, supplier.validEntityTypes);
				ByteCodec.KEY.mapWithValues(AdvancementNode.CODEC).encode(buf, supplier.advancements);
				StatisticNode.CODEC.list().encode(buf, supplier.statistics);
			}, (buf) -> new CreatorTaskSupplier(
					ByteCodec.KEY.hashSet().decode(buf),
					ByteCodec.KEY.hashSet().decode(buf),
					ByteCodec.KEY.hashSet().decode(buf),
					ByteCodec.KEY.mapWithValues(AdvancementNode.CODEC).decode(buf),
					StatisticNode.CODEC.list().decode(buf)
			));
}
