package io.github.steaf23.bingoreloadedcompanion.network;

import io.github.steaf23.bingoreloaded.protocol.data.card.CustomCard;
import io.github.steaf23.bingoreloaded.protocol.payload.BingoReloadedPayloads;
import io.github.steaf23.bingoreloadedcompanion.BingoReloadedCompanion;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ClientUpsertCreatorCardPayload(CustomCard card) implements CustomPacketPayload {

	public static final Type<ClientUpsertCreatorCardPayload> ID = new Type<>(
			Identifier.fromNamespaceAndPath(BingoReloadedCompanion.ADDON_ID, BingoReloadedPayloads.CLIENT_UPSERT_CREATOR_CARD.key().value())
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, ClientUpsertCreatorCardPayload> CODEC = StreamCodec.composite(
			StreamCodecs.fromByteCodec(BingoReloadedPayloads.CLIENT_UPSERT_CREATOR_CARD.codec()), ClientUpsertCreatorCardPayload::card,
			ClientUpsertCreatorCardPayload::new);


	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}

}
