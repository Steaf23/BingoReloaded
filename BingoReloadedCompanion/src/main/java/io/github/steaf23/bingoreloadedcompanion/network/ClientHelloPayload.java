package io.github.steaf23.bingoreloadedcompanion.network;

import io.github.steaf23.bingoreloadedcompanion.BingoReloadedCompanion;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public class ClientHelloPayload implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<ClientHelloPayload> ID = new CustomPacketPayload.Type<>(
			Identifier.fromNamespaceAndPath(BingoReloadedCompanion.ADDON_ID, "hello")
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, ClientHelloPayload> CODEC = StreamCodec.ofMember(
			(payload, buf) -> {},
			buf -> new ClientHelloPayload()
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
