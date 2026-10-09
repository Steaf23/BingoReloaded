package io.github.steaf23.bingoreloaded.api;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.GameManager;
import io.github.steaf23.bingoreloaded.lib.api.ActionUser;
import io.github.steaf23.bingoreloaded.lib.api.platform.GameContext;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;

import java.util.Optional;

public record BingoCommandSource(GameContext context, ActionUser user) {

	public GameManager gameManager() {
		return context.gameManager();
	}

	public Optional<BingoSession> getSession() {
		if (user instanceof PlayerHandle player) {
			return Optional.ofNullable(context.gameManager().getSessionFromWorld(player.world()));
		}

		return Optional.empty();
	}

	public Optional<BingoSession> getSessionByName(String name) {
		return Optional.ofNullable(context.gameManager().getSession(name));
	}

}
