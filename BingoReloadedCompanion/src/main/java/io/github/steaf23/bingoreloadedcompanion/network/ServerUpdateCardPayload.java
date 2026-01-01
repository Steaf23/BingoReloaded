package io.github.steaf23.bingoreloadedcompanion.network;

import io.github.steaf23.bingoreloadedcompanion.BingoReloadedCompanion;
import io.github.steaf23.bingoreloadedcompanion.card.BingoCard;
import io.github.steaf23.bingoreloadedcompanion.card.BingoGamemode;
import io.github.steaf23.bingoreloadedcompanion.card.Task;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public class ServerUpdateCardPayload implements CustomPacketPayload {

	private final BingoCard card;

	public static final CustomPacketPayload.Type<ServerUpdateCardPayload> ID = new CustomPacketPayload.Type<>(
			Identifier.fromNamespaceAndPath(BingoReloadedCompanion.ADDON_ID, "update_card")
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, ServerUpdateCardPayload> CODEC = StreamCodec.ofMember(
			(payload, buf) -> {}, // Not needed since we will only receive this packet, not send it.
			buf -> {
				boolean validCard = buf.readBoolean();
				if (!validCard) {
					return new ServerUpdateCardPayload(null);
				}

				String gamemodeStr = PayloadHelper.readString(buf);
				Identifier gamemodeId = Identifier.parse(gamemodeStr);
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
					Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(itemId));
					tasks.add(new Task(completion, Identifier.parse(taskType), item, requiredAmount));
				}

				BingoCard card = new BingoCard(gamemode, size, tasks);

				return new ServerUpdateCardPayload(card);
			});

	public ServerUpdateCardPayload(BingoCard card) {
		this.card = card;
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}

	public @Nullable BingoCard getCard() {
		return card;
	}
}
