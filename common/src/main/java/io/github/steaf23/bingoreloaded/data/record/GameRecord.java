package io.github.steaf23.bingoreloaded.data.record;

import io.github.steaf23.bingoreloaded.data.TeamData;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record GameRecord(String settingsId, SettingsType settingsType, Map<String, TeamRecord> teams, String winningTeam, Date timestamp, long playTime) {

	public record TeamRecord(int score, TeamData.TeamTemplate team, List<ParticipantRecord> participants) {
	}

	public record ParticipantRecord(int contribution, String displayName, UUID id) {
	}

	public enum SettingsType {
		PRESET("preset"),
		CUSTOM("custom"),
		;

		public final String configName;

		SettingsType(String configName) {
			this.configName = configName;
		}

		public static SettingsType fromString(String str) {
			return switch (str) {
				case "preset" -> PRESET;
				default -> CUSTOM;
			};
		}
	}
}


