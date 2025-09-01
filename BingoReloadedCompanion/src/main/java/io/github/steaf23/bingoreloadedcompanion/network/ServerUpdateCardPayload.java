package io.github.steaf23.bingoreloadedcompanion.network;

import io.github.steaf23.bingoreloadedcompanion.BingoReloadedCompanion;
import io.github.steaf23.bingoreloadedcompanion.card.BingoCard;
import io.github.steaf23.bingoreloadedcompanion.card.Task;
import net.minecraft.item.Item;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ServerUpdateCardPayload implements CustomPayload {

	private final BingoCard card;

	public static final CustomPayload.Id<ServerUpdateCardPayload> ID = new CustomPayload.Id<>(
			Identifier.of(BingoReloadedCompanion.ADDON_ID, "update_card")
	);

	public static final PacketCodec<RegistryByteBuf, ServerUpdateCardPayload> CODEC = PacketCodec.of(
			(payload, buf) -> {
				if (payload.card == null) {
					buf.writeBoolean(false);
					return;
				}

				buf.writeBoolean(true);
				BingoCard card = payload.card;

				buf.writeInt(card.size());
				buf.writeInt(card.tasks().size());

				for (Task task : card.tasks()) {
					buf.writeBoolean(task.completed());
					buf.writeString(Registries.ITEM.getId(task.itemType()).toString());
				}
			},
			buf -> {
				boolean validCard = buf.readBoolean();
				if (!validCard) {
					return new ServerUpdateCardPayload(null);
				}

				int size = buf.readInt();
				int tasksSize = buf.readInt();

				List<Task> tasks = new ArrayList<>();
				for (int i = 0; i < tasksSize; i++) {
					boolean completed = buf.readBoolean();

					short strSize = buf.readShort();
					byte[] bytes = new byte[strSize];
					for (int j = 0; j < strSize; j++) {
						bytes[j] = buf.readByte();
					}
					String itemId = new String(bytes);
					Item item = Registries.ITEM.get(Identifier.of(itemId));
					tasks.add(new Task(completed, item));
				}

				BingoCard card = new BingoCard(size, tasks);

				return new ServerUpdateCardPayload(card);
			});

	public ServerUpdateCardPayload(BingoCard card) {
		this.card = card;
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

	public @Nullable BingoCard getCard() {
		return card;
	}
}
