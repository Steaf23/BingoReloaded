package io.github.steaf23.bingoreloaded.lib.api.player;

import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformServer;
import io.github.steaf23.bingoreloaded.lib.menu.InfoMenu;

public interface SharedDisplay {

	void update(PlatformServer server, InfoMenu info);

	void addPlayer(PlayerHandle player);

	void removePlayer(PlayerHandle player);

	void clearPlayers();
}
