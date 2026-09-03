package io.github.steaf23.bingoreloadedcompanion.network;

import io.github.steaf23.bingoreloaded.protocol.payload.BingoReloadedPayloads;
import io.github.steaf23.bingoreloadedcompanion.BingoReloadedCompanion;
import io.github.steaf23.bingoreloaded.protocol.data.BingoCard;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ServerUpdateCardPayload implements CustomPacketPayload {

	private final BingoCard card;

	public static final CustomPacketPayload.Type<ServerUpdateCardPayload> ID = new CustomPacketPayload.Type<>(
			Identifier.fromNamespaceAndPath(BingoReloadedCompanion.ADDON_ID, BingoReloadedPayloads.UPDATE_CARD.key().value())
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, ServerUpdateCardPayload> CODEC = StreamCodecs.usingByteCodec(
			(buf, in) -> BingoReloadedPayloads.UPDATE_CARD.codec().encode(buf, Optional.ofNullable(in.card)),
			(buf) -> new ServerUpdateCardPayload(BingoReloadedPayloads.UPDATE_CARD.codec().decode(buf).orElse(null)));

	public ServerUpdateCardPayload(BingoCard card) {
		this.card = card;
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}

	public @Nullable BingoCard getCard() {
		return card;
	}
}
