package io.github.steaf23.bingoreloadedcompanion.network;

import io.github.steaf23.bingoreloadedcompanion.BingoReloadedCompanion;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public class RequestAllTasksPayload implements CustomPacketPayload {
	private final Identifier taskType;

	public static final CustomPacketPayload.Type<RequestAllTasksPayload> ID = new CustomPacketPayload.Type<>(
			Identifier.fromNamespaceAndPath(BingoReloadedCompanion.ADDON_ID, "request_all_tasks")
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, RequestAllTasksPayload> CODEC = StreamCodec.ofMember(
			(payload, buf) -> {
				PayloadHelper.writeString(payload.taskType.toString(), buf);
			},
			buf -> null // Packet will only be sent, not received
	);

	public RequestAllTasksPayload(Identifier taskType) {
		this.taskType = taskType;
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
