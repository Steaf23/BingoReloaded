package io.github.steaf23.bingoreloaded.protocol.data;

import io.github.steaf23.bingoreloaded.protocol.codec.ByteCodec;
import io.github.steaf23.bingoreloaded.protocol.data.card.CustomCard;
import io.github.steaf23.bingoreloaded.protocol.data.task.TaskFormatting;

import java.util.List;

public record CreatorContext(CreatorTaskSupplier taskSupplier, TaskFormatting formatting, List<CustomCard> cards) {
	public static final ByteCodec<CreatorContext> CODEC = ByteCodec.create(
			(buf, context) -> {
				CreatorTaskSupplier.CODEC.encode(buf, context.taskSupplier);
				TaskFormatting.CODEC.encode(buf, context.formatting);
				CustomCard.CODEC.list().encode(buf, context.cards);
			}, (buf) -> new CreatorContext(
					CreatorTaskSupplier.CODEC.decode(buf),
					TaskFormatting.CODEC.decode(buf),
					CustomCard.CODEC.list().decode(buf)
			));
}
