package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.lib.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum BingoSound implements Sound.Type {

	GO_UP_WAND_USED("go_up_wand_used"),
	HOTSWAP_TASK_ADDED("hotswap_task_added"),
	HOTSWAP_TASK_EXPIRED("hotswap_task_expired"),
	COUNTDOWN_TICK_1("countdown_tick_1"),
	COUNTDOWN_TICK_2("countdown_tick_2"),
	GAME_ENDED("game_ended"),
	GAME_WON("game_won"),
	DEATHMATCH_INITIATED("deathmatch_initiated"),
	DEATHMATCH_REVEAL("deathmatch_reveal"),
	TASK_COMPLETED("task_completed"),
	START_COUNTDOWN_FINISHED_1("start_countdown_finished_1"),
	START_COUNTDOWN_FINISHED_2("start_countdown_finished_2"),
	;

	private final String dataKey;
	@Nullable
	private Key soundKey;

	BingoSound(@Subst("minecraft:invalid") String key) {
		this.dataKey = key;
	}

	public static void setSounds(DataAccessor sounds) {
		for (BingoSound s : BingoSound.values()) {
			if (!sounds.contains(s.dataKey)) {
				ConsoleMessenger.error("Did not find a sound for " + s.dataKey + ", maybe you made a mistake whilst editing the sounds.yml file.");
				continue;
			}
			@Subst("minecraft:invalid") String value = sounds.getString(s.dataKey, "minecraft:invalid");
			s.soundKey = Key.key(value);
		}
	}

	@Override
	public @NotNull Key key() {
		return soundKey == null ? Key.key("minecraft:invalid") : soundKey;
	}
}
