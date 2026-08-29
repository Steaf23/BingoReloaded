package io.github.steaf23.bingoreloadedcompanion.network;

import io.github.steaf23.bingoreloadedcompanion.BingoReloadedCompanion;
import io.github.steaf23.bingoreloadedcompanion.card.BingoCard;
import io.github.steaf23.bingoreloadedcompanion.card.BingoGamemode;
import io.github.steaf23.bingoreloadedcompanion.card.Task;
import io.github.steaf23.bingoreloadedcompanion.card.taskslot.TaskDefinition;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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

				Identifier gamemodeId = PayloadHelper.ID_CODEC.decode(buf);
				BingoGamemode gamemode = BingoGamemode.fromId(gamemodeId, false);

				int size = buf.readInt();
				int tasksSize = buf.readInt();

				List<Task> tasks = new ArrayList<>();
				for (int i = 0; i < tasksSize; i++) {
					boolean completed = buf.readBoolean();
					Task.TaskCompletion completion;
					if (completed) {
						String player = PayloadHelper.STRING_CODEC.decode(buf);
						String team = PayloadHelper.STRING_CODEC.decode(buf);
						int color = buf.readInt();
						completion = new Task.TaskCompletion(true, player, team, color);
					} else {
						completion = Task.TaskCompletion.INCOMPLETE;
					}

					TaskDefinition task = TaskDefinition.CODEC.decode(buf);
					int requiredAmount = buf.readInt();
					tasks.add(new Task(task, completion, requiredAmount));
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
