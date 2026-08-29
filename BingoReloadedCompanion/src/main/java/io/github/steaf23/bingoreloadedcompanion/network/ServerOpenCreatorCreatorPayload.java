package io.github.steaf23.bingoreloadedcompanion.network;

import io.github.steaf23.bingoreloadedcompanion.BingoReloadedCompanion;
import io.github.steaf23.bingoreloadedcompanion.card.taskslot.TaskDefinition;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ServerOpenCreatorCreatorPayload implements CustomPacketPayload {

	private final List<TaskDefinition> tasks;

	public static final CustomPacketPayload.Type<ServerOpenCreatorCreatorPayload> ID = new CustomPacketPayload.Type<>(
			Identifier.fromNamespaceAndPath(BingoReloadedCompanion.ADDON_ID, "open_creator")
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, ServerOpenCreatorCreatorPayload> CODEC = StreamCodec.ofMember(
			(payload, buf) -> {}, // Packet will not be sent, only received.
			buf -> {

				int count = buf.readInt();
				System.out.println("Received task count: " + count);
				System.out.println("Bytes remaining: " + buf.readableBytes());

				List<TaskDefinition> tasks = new ArrayList<>();
				for (int i = 0; i < count; i ++) {
					tasks.add(TaskDefinition.CODEC.decode(buf));
					System.out.println("Read " + i + " tasks");
				}
				return new ServerOpenCreatorCreatorPayload(tasks);
			}
	);


	public ServerOpenCreatorCreatorPayload(List<TaskDefinition> tasks) {
		this.tasks = tasks;
	}

	public List<TaskDefinition> tasks() {
		return tasks;
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
