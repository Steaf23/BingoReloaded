package io.github.steaf23.bingoreloadedcompanion.network;

import io.github.steaf23.bingoreloaded.protocol.data.card.CustomList;
import io.github.steaf23.bingoreloaded.protocol.payload.BingoReloadedPayloads;
import io.github.steaf23.bingoreloadedcompanion.BingoReloadedCompanion;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ServerCreatorListPayload(CustomList list) implements CustomPacketPayload {

	public static final Type<ServerCreatorListPayload> ID = new Type<>(
			Identifier.fromNamespaceAndPath(BingoReloadedCompanion.ADDON_ID, BingoReloadedPayloads.CREATOR_LIST.key().value())
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, ServerCreatorListPayload> CODEC = StreamCodec.composite(
			StreamCodecs.fromByteCodec(BingoReloadedPayloads.CREATOR_LIST.codec()), ServerCreatorListPayload::list,
			ServerCreatorListPayload::new);


	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
