package io.github.steaf23.bingoreloaded.api;

import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;

public interface TeamDisplay {

	TeamDisplay DUMMY_DISPLAY = new TeamDisplay() {
		@Override
		public void update() {
		}

		@Override
		public void clearTeamsForPlayer(PlayerHandle player) {
		}

		@Override
		public void reset() {
		}
	};

	void update();

	void clearTeamsForPlayer(PlayerHandle player);

	void reset();
}
