package io.github.steaf23.bingoreloadedcompanion.network;

import io.github.steaf23.bingoreloaded.protocol.payload.BingoReloadedPayloads;
import io.github.steaf23.bingoreloadedcompanion.BingoReloadedCompanion;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ClientGetCreatorListPayload(String listName) implements CustomPacketPayload {

	public static final Type<ClientGetCreatorListPayload> ID = new Type<>(
			Identifier.fromNamespaceAndPath(BingoReloadedCompanion.ADDON_ID, BingoReloadedPayloads.CLIENT_GET_CREATOR_LIST.key().value())
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, ClientGetCreatorListPayload> CODEC = StreamCodec.composite(
			StreamCodecs.fromByteCodec(BingoReloadedPayloads.CLIENT_GET_CREATOR_LIST.codec()), ClientGetCreatorListPayload::listName,
			ClientGetCreatorListPayload::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
