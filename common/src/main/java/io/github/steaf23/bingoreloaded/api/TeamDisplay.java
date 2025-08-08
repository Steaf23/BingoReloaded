package io.github.steaf23.bingoreloaded.api;

import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;

public interface TeamDisplay {

	void update();

	void clearTeamsForPlayer(PlayerHandle player);

	void reset();
}
