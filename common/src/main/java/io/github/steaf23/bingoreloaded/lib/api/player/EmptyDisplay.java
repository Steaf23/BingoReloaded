package io.github.steaf23.bingoreloaded.lib.api.player;

import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformServer;
import io.github.steaf23.bingoreloaded.lib.menu.InfoMenu;

public class EmptyDisplay implements SharedDisplay {

	@Override
	public void update(PlatformServer server, InfoMenu info) {

	}

	@Override
	public void addPlayer(PlayerHandle player) {

	}

	@Override
	public void removePlayer(PlayerHandle player) {

	}

	@Override
	public void clearPlayers() {

	}
}
