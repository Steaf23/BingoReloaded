package io.github.steaf23.bingoreloadedcompanion.network;

import io.github.steaf23.bingoreloadedcompanion.BingoReloadedCompanion;
import io.github.steaf23.bingoreloadedcompanion.card.taskslot.ItemTask;
import io.github.steaf23.bingoreloadedcompanion.card.taskslot.TaskSlot;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public class EditTaskListPayload implements CustomPacketPayload {

	private final List<? extends TaskSlot> tasks;

	public static final CustomPacketPayload.Type<EditTaskListPayload> ID = new CustomPacketPayload.Type<>(
			Identifier.fromNamespaceAndPath(BingoReloadedCompanion.ADDON_ID, "edit_list")
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, EditTaskListPayload> CODEC = StreamCodec.ofMember(
			(payload, buf) -> {
				List<? extends TaskSlot> tasks = payload.tasks;
				buf.writeInt(tasks.size());

				for (TaskSlot t : tasks) {
					PayloadHelper.writeString(BuiltInRegistries.ITEM.getKey(t.item()).toString(), buf);
					buf.writeByte(t.completeCount());
				}
			},
			buf -> {
				int listSize = buf.readInt();

				List<TaskSlot> tasks = new ArrayList<>();
				for (int i = 0; i < listSize; i++) {
					Identifier id = Identifier.parse(PayloadHelper.readString(buf));
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
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}

	public List<? extends TaskSlot> tasks() {
		return tasks;
	}
}
