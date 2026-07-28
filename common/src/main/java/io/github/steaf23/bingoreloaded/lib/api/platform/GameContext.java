package io.github.steaf23.bingoreloaded.lib.api.platform;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.config.ConfigurationOption;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.GameManager;
import io.github.steaf23.bingoreloaded.lib.api.BingoReloadedRuntime;
import org.jetbrains.annotations.Nullable;

public record GameContext(PlatformServer server, BingoReloaded bingo) {

	public @Nullable BingoSession getSession(String sessionName) {
		return bingo.getGameManager().getSession(sessionName);
	}

	public <D> D getConfigOption(ConfigurationOption<D> option) {
		return bingo.getGameManager().getGameConfig().getOptionValue(option);
	}

	public BingoReloadedRuntime runtime() {
		return bingo.getGameManager().getRuntime();
	}

	public GameManager gameManager() {
		return bingo.getGameManager();
	}

	public PlatformTaskScheduler taskScheduler() {
		return runtime().taskScheduler();
	}

	public PlatformInventories inventories() {
		return server.inventories();
	}
}
