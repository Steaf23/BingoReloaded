package io.github.steaf23.bingoreloaded.data.serializers;

import io.github.steaf23.bingoreloaded.data.TeamData;
import io.github.steaf23.bingoreloaded.data.record.GameRecord;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorageSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GameRecordSerializer implements DataStorageSerializer<GameRecord> {

	@Override
	public void toDataStorage(@NotNull DataStorage storage, @NotNull GameRecord value) {
		storage.setUUID("settings", value.settingsId());
		storage.setString("winning_team", value.winningTeam());
		storage.setLong("timestamp", value.timestamp().getTime());
		storage.setLong("game_time", value.playTime());

		for (String teamId : value.teams().keySet()) {
			GameRecord.TeamRecord team = value.teams().get(teamId);
			DataStorage teamStore = storage.createNew();
			writeTeam(teamStore, team);
			storage.setStorage("teams." + teamId, teamStore);
		}
	}

	@Override
	public @Nullable GameRecord fromDataStorage(@NotNull DataStorage storage) {
		UUID settings = storage.getUUID("settings");
		String winningTeam = storage.getString("winning_team", "");
		long time = storage.getLong("timestamp", 0);
		Date date = new Date(time);
		long gameTime = storage.getLong("game_time", 0);

		Map<String, GameRecord.TeamRecord> teams = new HashMap<>();
		for (String teamId : storage.getStorage("teams").getKeys()) {
			GameRecord.TeamRecord team = readTeam(storage.getStorage("teams." + teamId));
			teams.put(teamId, team);
		}

		return new GameRecord(settings, teams, winningTeam, date, gameTime);
	}

	private GameRecord.TeamRecord readTeam(DataStorage storage) {
		int score = storage.getInt("score", 0);
		TeamData.TeamTemplate team = storage.getSerializable("team", TeamData.TeamTemplate.class);

		List<GameRecord.ParticipantRecord> participants = new ArrayList<>();
		for (DataStorage participant : storage.getList("participants")) {
			int contribution = participant.getInt("contribution", 0);
			String displayName = participant.getString("name", "");
			UUID playerId = participant.getUUID("uuid");

			GameRecord.ParticipantRecord playerRecord = new GameRecord.ParticipantRecord(contribution, displayName, playerId);
			participants.add(playerRecord);
		}

		return new GameRecord.TeamRecord(score, team, participants);
	}

	private void writeTeam(DataStorage storage, GameRecord.TeamRecord team) {
		storage.setInt("score", team.score());
		storage.setSerializable("team", TeamData.TeamTemplate.class, team.team());


		List<DataStorage> participants = new ArrayList<>();
		for (GameRecord.ParticipantRecord participant : team.participants()) {
			DataStorage wrapper = storage.createNew();
			wrapper.setInt("contribution", participant.contribution());
			wrapper.setString("name", participant.displayName());
			wrapper.setUUID("uuid", participant.id());
			participants.add(wrapper);
		}

		storage.setList("participants", participants);
	}
}
