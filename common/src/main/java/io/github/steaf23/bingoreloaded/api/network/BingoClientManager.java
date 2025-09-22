package io.github.steaf23.bingoreloaded.api.network;

import io.github.steaf23.bingoreloaded.cards.TaskCard;
import io.github.steaf23.bingoreloaded.cards.hotswap.HotswapTaskHolder;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface BingoClientManager {

	boolean playerHasClient(PlayerHandle player);

	void updateCard(PlayerHandle player, @Nullable TaskCard card);

	void updateHotswapContext(PlayerHandle player, @NotNull List<HotswapTaskHolder> holders);

	void playerLeavesServer(PlayerHandle player);

	class DisabledClientManager implements BingoClientManager {

		@Override
		public boolean playerHasClient(PlayerHandle player) {
			return false;
		}

		@Override
		public void updateCard(PlayerHandle player, @Nullable TaskCard card) {

		}

		@Override
		public void updateHotswapContext(PlayerHandle player, @NotNull List<HotswapTaskHolder> holders) {

		}

		@Override
		public void playerLeavesServer(PlayerHandle player) {

		}
	}
}
