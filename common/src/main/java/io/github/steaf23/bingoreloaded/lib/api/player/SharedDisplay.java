package io.github.steaf23.bingoreloaded.lib.api.player;

import io.github.steaf23.bingoreloaded.lib.menu.InfoMenu;

public interface SharedDisplay {

	void update(InfoMenu info);

	void addPlayer(PlayerHandle player);

	void removePlayer(PlayerHandle player);

	void clearPlayers();
}
