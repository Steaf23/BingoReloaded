package io.github.steaf23.bingoreloadedcompanion.network;

import io.github.steaf23.bingoreloadedcompanion.BingoReloadedCompanion;
import io.github.steaf23.bingoreloadedcompanion.card.BingoCard;
import io.github.steaf23.bingoreloadedcompanion.card.BingoGamemode;
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
			(payload, buf) -> {}, // Not needed since we will only receive this packet, not send it.
			buf -> {
				boolean validCard = buf.readBoolean();
				if (!validCard) {
					return new ServerUpdateCardPayload(null);
				}

				String gamemodeStr = PayloadHelper.readString(buf);
				Identifier gamemodeId = Identifier.of(gamemodeStr);
				BingoGamemode gamemode = BingoGamemode.fromId(gamemodeId, false);

				int size = buf.readInt();
				int tasksSize = buf.readInt();

				List<Task> tasks = new ArrayList<>();
				for (int i = 0; i < tasksSize; i++) {
					boolean completed = buf.readBoolean();
					Task.TaskCompletion completion;
					if (completed) {
						String player = PayloadHelper.readString(buf);
						String team = PayloadHelper.readString(buf);
						int color = buf.readInt();
						completion = new Task.TaskCompletion(true, player, team, color);
					} else {
						completion = Task.TaskCompletion.INCOMPLETE;
					}

					String taskType = PayloadHelper.readString(buf);
					int requiredAmount = buf.readInt();
					String itemId = PayloadHelper.readString(buf);
					Item item = Registries.ITEM.get(Identifier.of(itemId));
					tasks.add(new Task(completion, Identifier.of(taskType), item, requiredAmount));
				}

				BingoCard card = new BingoCard(gamemode, size, tasks);

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
