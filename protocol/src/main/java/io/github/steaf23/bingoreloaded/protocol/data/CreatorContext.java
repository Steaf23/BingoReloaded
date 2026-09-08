package io.github.steaf23.bingoreloaded.protocol.data;

import io.github.steaf23.bingoreloaded.protocol.codec.ByteCodec;import io.github.steaf23.bingoreloaded.protocol.data.card.CustomCard;import io.github.steaf23.bingoreloaded.protocol.data.card.CustomList;

import java.util.List;

public record CreatorContext(CreatorTaskSupplier taskSupplier, List<CustomCard> cards) {
	public static final ByteCodec<CreatorContext> CODEC = ByteCodec.create(
			(buf, context) -> {
				CreatorTaskSupplier.CODEC.encode(buf, context.taskSupplier);
				CustomCard.CODEC.list().encode(buf, context.cards);
			}, (buf) -> new CreatorContext(
					CreatorTaskSupplier.CODEC.decode(buf),
					CustomCard.CODEC.list().decode(buf)
			));
}
