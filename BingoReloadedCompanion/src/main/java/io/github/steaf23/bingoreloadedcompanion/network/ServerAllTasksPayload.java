package io.github.steaf23.bingoreloadedcompanion.network;

import io.github.steaf23.bingoreloadedcompanion.BingoReloadedCompanion;
import io.github.steaf23.bingoreloadedcompanion.card.taskslot.TaskSlot;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.List;

public class ServerAllTasksPayload implements CustomPayload {

	private final Identifier taskType;
	private final List<? extends TaskSlot> tasks;

	public static final CustomPayload.Id<ServerAllTasksPayload> ID = new CustomPayload.Id<>(
			Identifier.of(BingoReloadedCompanion.ADDON_ID, "all_tasks")
	);

	public static final PacketCodec<RegistryByteBuf, RequestAllTasksPayload> CODEC = PacketCodec.of(
			(payload, buf) -> {}, // Packet will not be sent, only received.
			buf -> {
				String taskType = PayloadHelper.readString(buf);

				return null;
			}
	);


	public ServerAllTasksPayload(Identifier taskType, List<? extends TaskSlot> tasks) {
		this.taskType = taskType;
		this.tasks = tasks;
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
