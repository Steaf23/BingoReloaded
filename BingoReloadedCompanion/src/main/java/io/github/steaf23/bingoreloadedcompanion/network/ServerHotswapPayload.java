package io.github.steaf23.bingoreloadedcompanion.network;


import com.google.common.collect.ImmutableList;
import io.github.steaf23.bingoreloaded.protocol.data.TaskSlot;
import io.github.steaf23.bingoreloaded.protocol.payload.BingoReloadedPayloads;
import io.github.steaf23.bingoreloadedcompanion.BingoReloadedCompanion;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ServerHotswapPayload(ImmutableList<TaskSlot> holders) implements CustomPacketPayload {

	public static final Type<ServerHotswapPayload> ID = new Type<>(
			Identifier.fromNamespaceAndPath(BingoReloadedCompanion.ADDON_ID, BingoReloadedPayloads.UPDATE_HOTSWAP_CARD.key().value())
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, ServerHotswapPayload> CODEC = StreamCodecs.usingByteCodec(
			(buf, payload) -> {
				BingoReloadedPayloads.UPDATE_HOTSWAP_CARD.codec().encode(buf, payload.holders);
			}, // Packet will not be sent, only received.
			buf -> new ServerHotswapPayload(ImmutableList.copyOf(BingoReloadedPayloads.UPDATE_HOTSWAP_CARD.codec().decode(buf)))
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
