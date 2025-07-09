package io.github.steaf23.bingoreloaded.api;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.lib.api.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.StatisticHandle;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.bingoreloaded.util.timer.CountdownTimer;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.Nullable;

public class BingoEvents {

	private BingoEvents() {
	}

	public record PlayerSessionEvent(BingoSession session, PlayerHandle player) {

	}

	public record TaskProgressCompleted(BingoSession session, GameTask task) {

	}

	public record DeathmatchTaskCompleted(BingoSession session, GameTask task) {

	}

	public record TeamParticipantEvent(BingoSession session, BingoParticipant participant, @Nullable BingoTeam team,
								boolean autoTeam) {

	}

	public record GameEnded(BingoSession session, long totalGameTime, @Nullable BingoTeam winningTeam) {

	}

	public record PlaySound(BingoSession session, Sound sound) {

	}

	public record SettingsUpdated(BingoSession session, BingoSettings newSettings) {

	}

	public record StatisticCompleted(BingoSession session, StatisticHandle statistic, BingoParticipant participant) {

	}

	public record CountdownTimerFinished(BingoSession session, CountdownTimer timer) {

	}
}
