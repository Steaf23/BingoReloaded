package io.github.steaf23.bingoreloadedcompanion.network;

import io.github.steaf23.bingoreloaded.protocol.data.CreatorTaskSupplier;
import io.github.steaf23.bingoreloaded.protocol.payload.BingoReloadedPayloads;
import io.github.steaf23.bingoreloadedcompanion.BingoReloadedCompanion;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public class ServerOpenCreatorCreatorPayload implements CustomPacketPayload {

	private final CreatorTaskSupplier taskSupplier;

	public static final CustomPacketPayload.Type<ServerOpenCreatorCreatorPayload> ID = new CustomPacketPayload.Type<>(
			Identifier.fromNamespaceAndPath(BingoReloadedCompanion.ADDON_ID, BingoReloadedPayloads.OPEN_CREATOR.key().value())
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, ServerOpenCreatorCreatorPayload> CODEC = StreamCodec.composite(
			StreamCodecs.fromByteCodec(BingoReloadedPayloads.OPEN_CREATOR.codec()), ServerOpenCreatorCreatorPayload::taskSupplier,
			ServerOpenCreatorCreatorPayload::new);

	public ServerOpenCreatorCreatorPayload(CreatorTaskSupplier taskSupplier) {
		this.taskSupplier = taskSupplier;
	}

	public CreatorTaskSupplier taskSupplier() {
		return taskSupplier;
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
