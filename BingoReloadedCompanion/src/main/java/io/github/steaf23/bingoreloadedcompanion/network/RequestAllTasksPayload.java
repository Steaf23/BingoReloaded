package io.github.steaf23.bingoreloadedcompanion.network;

import io.github.steaf23.bingoreloadedcompanion.BingoReloadedCompanion;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class RequestAllTasksPayload implements CustomPayload {
	private final Identifier taskType;

	public static final CustomPayload.Id<RequestAllTasksPayload> ID = new CustomPayload.Id<>(
			Identifier.of(BingoReloadedCompanion.ADDON_ID, "request_all_tasks")
	);

	public static final PacketCodec<RegistryByteBuf, RequestAllTasksPayload> CODEC = PacketCodec.of(
			(payload, buf) -> {
				PayloadHelper.writeString(payload.taskType.toString(), buf);
			},
			buf -> null // Packet will only be sent, not received
	);

	public RequestAllTasksPayload(Identifier taskType) {
		this.taskType = taskType;
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
