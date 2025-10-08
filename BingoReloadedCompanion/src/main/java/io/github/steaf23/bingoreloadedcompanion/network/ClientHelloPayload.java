package io.github.steaf23.bingoreloadedcompanion.network;

import io.github.steaf23.bingoreloadedcompanion.BingoReloadedCompanion;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class ClientHelloPayload implements CustomPayload {

	public static final CustomPayload.Id<ClientHelloPayload> ID = new CustomPayload.Id<>(
			Identifier.of(BingoReloadedCompanion.ADDON_ID, "hello")
	);

	public static final PacketCodec<RegistryByteBuf, ClientHelloPayload> CODEC = PacketCodec.of(
			(payload, buf) -> {},
			buf -> new ClientHelloPayload()
	);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
