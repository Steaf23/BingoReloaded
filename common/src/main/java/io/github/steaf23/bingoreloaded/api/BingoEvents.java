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

public interface BingoEvents {
	record PlayerEvent(BingoSession session, PlayerHandle player){}

	record TaskProgressCompletedEvent(BingoSession session, GameTask task) {}
	record TeamParticipantEvent(BingoSession session, BingoParticipant participant, @Nullable BingoTeam team, boolean autoTeam){}

	record GameEnded(BingoSession session, long totalGameTime, @Nullable BingoTeam winningTeam) {}
	record PlaySound(BingoSession session, Sound sound) {}
	record SettingsUpdated(BingoSession session, BingoSettings newSettings){}
	record StatisticCompleted(BingoSession session, StatisticHandle statistic, BingoParticipant participant){}
	record CountdownTimerFinished(BingoSession session, CountdownTimer timer){}
}
