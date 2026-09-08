package io.github.steaf23.bingoreloadedcompanion.network;

import io.github.steaf23.bingoreloaded.protocol.codec.ByteCodec;
import io.github.steaf23.bingoreloaded.protocol.payload.BingoReloadedPayloads;
import io.github.steaf23.bingoreloadedcompanion.BingoReloadedCompanion;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ClientHelloPayload() implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<ClientHelloPayload> ID = new CustomPacketPayload.Type<>(
			Identifier.fromNamespaceAndPath(BingoReloadedCompanion.ADDON_ID, BingoReloadedPayloads.CLIENT_HELLO.key().value())
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, ClientHelloPayload> CODEC = StreamCodecs.fromByteCodec(ByteCodec.unit(new ClientHelloPayload()));

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
