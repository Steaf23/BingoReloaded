package io.github.steaf23.bingoreloaded.data.record;

import io.github.steaf23.bingoreloaded.data.TeamData;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record GameRecord(UUID settingsId, Map<String, TeamRecord> teams, String winningTeam, Date timestamp) {

	public record TeamRecord(int score, TeamData.TeamTemplate team, List<ParticipantRecord> participants) {
	}

	public record ParticipantRecord(int contribution, String displayName, UUID id) {
	}
}


