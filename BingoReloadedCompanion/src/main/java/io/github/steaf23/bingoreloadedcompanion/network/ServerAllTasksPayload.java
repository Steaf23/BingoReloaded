package io.github.steaf23.bingoreloadedcompanion.network;

import io.github.steaf23.bingoreloadedcompanion.BingoReloadedCompanion;
import io.github.steaf23.bingoreloadedcompanion.card.taskslot.TaskSlot;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.List;

public class ServerAllTasksPayload implements CustomPacketPayload {

	private final Identifier taskType;
	private final List<? extends TaskSlot> tasks;

	public static final CustomPacketPayload.Type<ServerAllTasksPayload> ID = new CustomPacketPayload.Type<>(
			Identifier.fromNamespaceAndPath(BingoReloadedCompanion.ADDON_ID, "all_tasks")
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, RequestAllTasksPayload> CODEC = StreamCodec.ofMember(
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
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
