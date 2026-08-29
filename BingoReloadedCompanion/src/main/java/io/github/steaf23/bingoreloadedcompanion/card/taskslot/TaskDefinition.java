package io.github.steaf23.bingoreloadedcompanion.card.taskslot;

import io.github.steaf23.bingoreloadedcompanion.network.PayloadHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record TaskDefinition(
		TaskId id,
		String name,
		String description,
		Identifier iconItem,
		Identifier category,
		int maxCount) {

	public static final StreamCodec<RegistryFriendlyByteBuf, TaskDefinition> CODEC = StreamCodec.composite(
			TaskId.STREAM_CODEC, TaskDefinition::id,
			PayloadHelper.STRING_CODEC, TaskDefinition::name,
			PayloadHelper.STRING_CODEC, TaskDefinition::description,
			PayloadHelper.ID_CODEC, TaskDefinition::iconItem,
			PayloadHelper.ID_CODEC, TaskDefinition::category,
			ByteBufCodecs.INT, TaskDefinition::maxCount,
			TaskDefinition::new);

	public TaskType type() {
		return id.type();
	}
}
