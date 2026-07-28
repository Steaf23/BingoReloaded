package io.github.steaf23.bingoreloaded.lib.inventory;


import io.github.steaf23.bingoreloaded.lib.api.platform.GameContext;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;

public interface MenuBoard {

	void open(Menu menu, PlayerHandle player);

	void close(Menu menu, PlayerHandle player);

	void closeAll(PlayerHandle player);

	GameContext context();
}
