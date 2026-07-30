package io.github.steaf23.bingoreloaded.lib.api.platform;

import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.inventory.Menu;
import io.github.steaf23.bingoreloaded.lib.inventory.MenuStack;

public interface PlatformMenus {

	void show(Menu menu, PlayerHandle player);

	void close(PlayerHandle playerHandle);

	void remove(Menu menu, PlayerHandle playerHandle);
}
