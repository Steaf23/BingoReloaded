package io.github.steaf23.bingoreloaded.data.config;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record GameSetup(int maxConcurrentGames, LobbyMode lobbyMode, Optional<Key> lobbyWorldKey)  {

	public enum LobbyMode {
		GAME_WORLD("GAME_WORLD"),
		SEPARATE_WORLD("SEPARATE_WORLD"),
		VIRTUAL("VIRTUAL"),
		;

		public final String configName;

		LobbyMode(String configName) {
			this.configName = configName;
		}
	}

	public GameSetup {
		if (maxConcurrentGames < 1) {
			throw new IllegalArgumentException("maxConcurrentGames must be positive");
		}
		switch (lobbyMode) {
			case VIRTUAL -> {
				if (lobbyWorldKey.isPresent()) {
					throw new IllegalArgumentException("Virtual lobby does not support a lobby world key");
				}
			}
			case GAME_WORLD -> {
				if (maxConcurrentGames > 1) {
					throw new IllegalArgumentException("Game world lobby cannot have more than 1 concurrent game");
				}
			}
			case SEPARATE_WORLD -> {
			}
		}
	}

	public boolean hasExistingLobbyWorld() {
		return lobbyWorldKey.isPresent();
	}

	public static Key getDefaultWorldKey() {
		return BingoReloaded.resourceKey("bingo");
	}

	public static Key getDefaultLobbyWorldKey() {
		return BingoReloaded.resourceKey("lobby");
	}

	public static GameSetup virtualLobby(int maxConcurrentGames) {
		return new GameSetup(maxConcurrentGames, LobbyMode.VIRTUAL, Optional.empty());
	}

	public static GameSetup singleWorld(@NotNull Key worldKey) {
		return new GameSetup(1, LobbyMode.GAME_WORLD, Optional.of(worldKey));
	}

	public boolean canPlayMultipleGamesAtOnce() {
		return maxConcurrentGames() > 1;
	}

	public boolean hasPhysicalLobby() {
		return lobbyMode != LobbyMode.VIRTUAL;
	}
}