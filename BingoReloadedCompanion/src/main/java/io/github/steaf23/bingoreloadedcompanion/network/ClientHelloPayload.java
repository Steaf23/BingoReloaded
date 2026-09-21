package io.github.steaf23.bingoreloadedcompanion.network;

import io.github.steaf23.bingoreloaded.protocol.data.ClientSettings;
import io.github.steaf23.bingoreloaded.protocol.payload.BingoReloadedPayloads;
import io.github.steaf23.bingoreloadedcompanion.BingoReloadedCompanion;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ClientHelloPayload(ClientSettings settings) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<ClientHelloPayload> ID = new CustomPacketPayload.Type<>(
			Identifier.fromNamespaceAndPath(BingoReloadedCompanion.ADDON_ID, BingoReloadedPayloads.CLIENT_HELLO.key().value())
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, ClientHelloPayload> CODEC = StreamCodec.composite(
			StreamCodecs.fromByteCodec(BingoReloadedPayloads.CLIENT_HELLO.codec()), ClientHelloPayload::settings,
			ClientHelloPayload::new);


	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
