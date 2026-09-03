package io.github.steaf23.bingoreloaded.protocol.data;

import io.github.steaf23.bingoreloaded.protocol.codec.ByteCodec;
import io.github.steaf23.bingoreloaded.protocol.data.task.Task;
import net.kyori.adventure.key.Key;

import java.util.List;

public record BingoCard(BingoGamemode mode, int size, List<Task> tasks) {

	public static final ByteCodec<BingoCard> CODEC = ByteCodec.create(
			(buf, card) -> {
				ByteCodec.KEY.encode(buf, card.mode().key());
				buf.writeInt(card.size());
				Task.CODEC.list().encode(buf, card.tasks());
			}, (buf) -> {
				Key gamemodeId = ByteCodec.KEY.decode(buf);
				BingoGamemode gamemode = BingoGamemode.fromIdentifier(gamemodeId, false);

				int size = buf.readInt();
				List<Task> tasks = Task.CODEC.list().decode(buf);

				return new BingoCard(gamemode, size, tasks);
			});
}
