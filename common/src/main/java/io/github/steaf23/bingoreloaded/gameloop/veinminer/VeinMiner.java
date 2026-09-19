package io.github.steaf23.bingoreloaded.gameloop.veinminer;

import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.lib.api.GlobalPosition;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;

public interface VeinMiner {

	VeinMiner DISABLED = new VeinMiner() {
		@Override
		public void playerBreaksBlock(PlayerHandle player, BingoGame game, GlobalPosition blockPos, StackHandle tool) {

		}
	};

	void playerBreaksBlock(PlayerHandle player, BingoGame game, GlobalPosition blockPos, StackHandle tool);
}
