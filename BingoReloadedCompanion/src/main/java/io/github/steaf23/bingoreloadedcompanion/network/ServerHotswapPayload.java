package io.github.steaf23.bingoreloadedcompanion.network;


import com.google.common.collect.ImmutableList;
import io.github.steaf23.bingoreloaded.protocol.payload.BingoReloadedPayloads;
import io.github.steaf23.bingoreloadedcompanion.BingoReloadedCompanion;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public class ServerHotswapPayload implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<ServerHotswapPayload> ID = new CustomPacketPayload.Type<>(
			Identifier.fromNamespaceAndPath(BingoReloadedCompanion.ADDON_ID, BingoReloadedPayloads.UPDATE_HOTSWAP_CARD.key().value())
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, ServerHotswapPayload> CODEC = StreamCodecs.usingByteCodec(
			(buf, payload) -> {
				BingoReloadedPayloads.UPDATE_HOTSWAP_CARD.codec().encode(buf, payload.holders);
			}, // Packet will not be sent, only received.
			buf -> new ServerHotswapPayload(ImmutableList.copyOf(BingoReloadedPayloads.UPDATE_HOTSWAP_CARD.codec().decode(buf)))
	);


	public final ImmutableList<io.github.steaf23.bingoreloaded.protocol.data.TaskSlot> holders;

	public ServerHotswapPayload(ImmutableList<io.github.steaf23.bingoreloaded.protocol.data.TaskSlot> holders) {
		this.holders = holders;
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
