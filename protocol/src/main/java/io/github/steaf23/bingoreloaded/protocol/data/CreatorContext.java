package io.github.steaf23.bingoreloaded.protocol.data;

import io.github.steaf23.bingoreloaded.protocol.codec.ByteCodec;
import io.github.steaf23.bingoreloaded.protocol.data.card.CustomCard;
import io.github.steaf23.bingoreloaded.protocol.data.task.TaskFormatting;
import net.kyori.adventure.text.format.TextColor;

import java.util.List;
import java.util.Map;

public record CreatorContext(CreatorTaskSupplier taskSupplier, TaskFormatting formatting, List<CustomCard> cards, Map<String, TextColor> tags) {

	public static final ByteCodec<Map<String, TextColor>> TAGS_CODEC = ByteCodec.STRING.mapWithValues(ByteCodec.COLOR);

	public static final ByteCodec<CreatorContext> CODEC = ByteCodec.create(
			(buf, context) -> {
				CreatorTaskSupplier.CODEC.encode(buf, context.taskSupplier);
				TaskFormatting.CODEC.encode(buf, context.formatting);
				CustomCard.CODEC.list().encode(buf, context.cards);
				TAGS_CODEC.encode(buf, context.tags);
			}, (buf) -> new CreatorContext(
					CreatorTaskSupplier.CODEC.decode(buf),
					TaskFormatting.CODEC.decode(buf),
					CustomCard.CODEC.list().decode(buf),
					TAGS_CODEC.decode(buf)
			));
}
