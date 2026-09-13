package io.github.steaf23.bingoreloaded.protocol.data.task;

import io.github.steaf23.bingoreloaded.protocol.codec.ByteCodec;import io.github.steaf23.bingoreloaded.protocol.message.MessageParser;
import net.kyori.adventure.text.Component;

public record TaskFormatting(String itemName,
							 String advancementName,
							 String advancementDescription,
							 String statisticName,
							 String statisticItem,
							 String statisticKillEntity,
							 String statisticKilledByEntity) {

	public static final ByteCodec<TaskFormatting> CODEC = ByteCodec.create(
			(buf, formatting) -> {
				ByteCodec.STRING.encode(buf, formatting.itemName);
				ByteCodec.STRING.encode(buf, formatting.advancementName);
				ByteCodec.STRING.encode(buf, formatting.advancementDescription);
				ByteCodec.STRING.encode(buf, formatting.statisticName);
				ByteCodec.STRING.encode(buf, formatting.statisticItem);
				ByteCodec.STRING.encode(buf, formatting.statisticKillEntity);
				ByteCodec.STRING.encode(buf, formatting.statisticKilledByEntity);
			}, (buf) -> new TaskFormatting(
					ByteCodec.STRING.decode(buf),
					ByteCodec.STRING.decode(buf),
					ByteCodec.STRING.decode(buf),
					ByteCodec.STRING.decode(buf),
					ByteCodec.STRING.decode(buf),
					ByteCodec.STRING.decode(buf),
					ByteCodec.STRING.decode(buf))
	);

	public Component itemNameComponent(Component item, int taskCount) {
		return MessageParser.createPhrase(itemName, item, Component.text(taskCount));
	}

	public Component advancementNameComponent(Component advancementTitle) {
		return MessageParser.createPhrase(advancementName, advancementTitle);
	}

	public Component advancementDescriptionComponent(Component advancementDesc) {
		return MessageParser.createPhrase(advancementDescription, advancementDesc);
	}

	public Component statisticNameComponent(Component statisticText, int count, Component units, int countMultiplier) {
		return MessageParser.createPhrase(statisticName,
				statisticText,
				Component.text(count * countMultiplier),
				units);
	}

	public Component statisticItemComponent(Component statisticText, Component item, int count) {
		return MessageParser.createPhrase(statisticItem,
				statisticText,
				item,
				Component.text(count));
	}

	public Component statisticKillEntityComponent(EntityStatisticResolver statisticText, Component entity, int count) {
		Component[] inPlaceArgs = new Component[]{Component.text(count), Component.empty()};
		return MessageParser.createPhrase(statisticKillEntity,
				statisticText.resolve(inPlaceArgs),
				entity);
	}

	public Component statisticKilledByEntityComponent(EntityStatisticResolver statisticText, Component entity, int count) {
		Component[] inPlaceArgs = new Component[]{Component.empty(), Component.text(count), Component.empty()};
		return MessageParser.createPhrase(statisticKilledByEntity,
				statisticText.resolve(inPlaceArgs),
				entity);
	}

	@FunctionalInterface
	public interface EntityStatisticResolver {
		Component resolve(Component[] inPlaceArgs);
	}

}
