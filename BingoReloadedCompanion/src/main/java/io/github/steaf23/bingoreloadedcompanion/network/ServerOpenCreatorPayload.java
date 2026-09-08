package io.github.steaf23.bingoreloadedcompanion.network;

import io.github.steaf23.bingoreloaded.protocol.data.CreatorContext;
import io.github.steaf23.bingoreloaded.protocol.payload.BingoReloadedPayloads;
import io.github.steaf23.bingoreloadedcompanion.BingoReloadedCompanion;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ServerOpenCreatorPayload(CreatorContext creatorContext) implements CustomPacketPayload {

	public static final Type<ServerOpenCreatorPayload> ID = new Type<>(
			Identifier.fromNamespaceAndPath(BingoReloadedCompanion.ADDON_ID, BingoReloadedPayloads.OPEN_CREATOR.key().value())
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, ServerOpenCreatorPayload> CODEC = StreamCodec.composite(
			StreamCodecs.fromByteCodec(BingoReloadedPayloads.OPEN_CREATOR.codec()), ServerOpenCreatorPayload::creatorContext,
			ServerOpenCreatorPayload::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
