package io.github.steaf23.bingoreloadedcompanion.network;

import io.github.steaf23.bingoreloadedcompanion.BingoReloadedCompanion;
import io.github.steaf23.bingoreloadedcompanion.card.taskslot.ItemTask;
import io.github.steaf23.bingoreloadedcompanion.card.taskslot.TaskSlot;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class EditTaskListPayload implements CustomPayload {

	private final List<? extends TaskSlot> tasks;

	public static final CustomPayload.Id<EditTaskListPayload> ID = new CustomPayload.Id<>(
			Identifier.of(BingoReloadedCompanion.ADDON_ID, "edit_list")
	);

	public static final PacketCodec<RegistryByteBuf, EditTaskListPayload> CODEC = PacketCodec.of(
			(payload, buf) -> {
				List<? extends TaskSlot> tasks = payload.tasks;
				buf.writeInt(tasks.size());

				for (TaskSlot t : tasks) {
					PayloadHelper.writeString(Registries.ITEM.getId(t.item()).toString(), buf);
					buf.writeByte(t.completeCount());
				}
			},
			buf -> {
				int listSize = buf.readInt();

				List<TaskSlot> tasks = new ArrayList<>();
				for (int i = 0; i < listSize; i++) {
					Identifier id = Identifier.of(PayloadHelper.readString(buf));
					int count = buf.readByte();

					tasks.add(new ItemTask(id, count));
				}

				return new EditTaskListPayload(tasks);
			}
	);

	public EditTaskListPayload(List<? extends TaskSlot> tasks) {
		this.tasks = tasks;
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

	public List<? extends TaskSlot> tasks() {
		return tasks;
	}
}
