package io.github.steaf23.bingoreloaded.data.record;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.TeamData;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.lib.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.lib.util.ComponentUtils;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public class GameRecordData {

	private final DataAccessor data = BingoReloaded.getDataAccessor("data/history");

	List<GameRecord> getGamesFilteredBy(Predicate<GameRecord> filter) {
		return getAllRecords().stream().filter(filter).toList();
	}

	List<GameRecord> getAllRecords() {
		return data.getSerializableList("games", GameRecord.class);
	}

	BingoSettings settingsFromGame(GameRecord record) {
		return data.getSerializable("settings." + record.settingsId().toString(), BingoSettings.class);
	}

	public void saveGame(BingoGame game, @Nullable BingoTeam winningTeam) {
		UUID settingsId = getOrCreateSettingsIdFromGame(game);

		Map<String, GameRecord.TeamRecord> teams = new HashMap<>();
		for (BingoTeam team : game.getTeamManager().getActiveTeams()) {
			int score = team.getCompleteCount();
			TeamData.TeamTemplate template = new TeamData.TeamTemplate(ComponentUtils.MINI_BUILDER.serialize(team.getName()), team.getColor());

			List<GameRecord.ParticipantRecord> participants = team.getMembers().stream()
					.map(p -> new GameRecord.ParticipantRecord(p.getAmountOfTaskCompleted(), p.getName(), p.getId()))
					.toList();
			teams.put(team.getIdentifier(), new GameRecord.TeamRecord(score, template, participants));
		}

		GameRecord record = new GameRecord(settingsId, teams, winningTeam == null ? "" : winningTeam.getIdentifier(), new Date());
		List<GameRecord> newRecords = new ArrayList<>(getAllRecords());
		newRecords.add(record);

		data.setSerializableList("games", GameRecord.class, newRecords);
		data.saveChanges();
	}

	public UUID getOrCreateSettingsIdFromGame(BingoGame game) {
		Set<String> allIds = data.getStorage("settings").getKeys();
		for (String id : allIds) {
			BingoSettings settings = data.getSerializable("settings." + id, BingoSettings.class);
			if (game.getSettings().equals(settings)) {
				return UUID.fromString(id);
			}
		}
		// Settings are not yet saved in the history file, save it here.
		UUID settingsId = UUID.randomUUID();
		data.setSerializable("settings." + settingsId, BingoSettings.class, game.getSettings());
		data.saveChanges();

		return settingsId;
	}

	public void purgeBottomRecords(int amountOfRecords) {

	}

	public void purgeOldRecords(int amountOfRecords) {

	}

	public void purgeFilteredRecords(Predicate<GameRecord> filter) {

	}
}
