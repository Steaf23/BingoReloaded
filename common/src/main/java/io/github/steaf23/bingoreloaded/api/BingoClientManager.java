package io.github.steaf23.bingoreloaded.api;

import io.github.steaf23.bingoreloaded.cards.TaskCard;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.protocol.data.CreatorTaskSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface BingoClientManager {

	boolean playerHasClient(PlayerHandle player);

	void updateCard(PlayerHandle player, @Nullable TaskCard card);

	void updateHotswapContext(PlayerHandle player, @NotNull List<io.github.steaf23.bingoreloaded.protocol.data.TaskSlot> holders);

	void playerLeavesServer(PlayerHandle player);

	void openCreator(PlayerHandle player, @NotNull CreatorTaskSupplier tasks);

	class DisabledClientManager implements BingoClientManager {

		@Override
		public boolean playerHasClient(PlayerHandle player) {
			return false;
		}

		@Override
		public void updateCard(PlayerHandle player, @Nullable TaskCard card) {

		}

		@Override
		public void updateHotswapContext(PlayerHandle player, @NotNull List<io.github.steaf23.bingoreloaded.protocol.data.TaskSlot> holders) {

		}

		@Override
		public void playerLeavesServer(PlayerHandle player) {

		}

		@Override
		public void openCreator(PlayerHandle player, @NotNull CreatorTaskSupplier tasks) {

		}
	}
}
