package io.github.steaf23.bingoreloaded.api.network;

import io.github.steaf23.bingoreloaded.cards.TaskCard;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import org.jetbrains.annotations.Nullable;

public interface BingoClientManager {

	boolean playerHasClient(PlayerHandle player);

	void updateCard(PlayerHandle player, @Nullable TaskCard card);

	void playerLeavesServer(PlayerHandle player);
}
