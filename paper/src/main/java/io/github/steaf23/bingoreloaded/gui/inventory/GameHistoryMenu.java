package io.github.steaf23.bingoreloaded.gui.inventory;

import io.github.steaf23.bingoreloaded.data.record.GameRecord;
import io.github.steaf23.bingoreloaded.data.record.GameRecordData;
import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.action.MenuAction;
import io.github.steaf23.bingoreloaded.lib.inventory.group.PaginatedGroup;
import io.github.steaf23.bingoreloaded.lib.inventory.group.StackedGroup;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.lib.util.PlayerDisplayTranslationKey;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.settings.gamemode.BingoGamemode;
import io.github.steaf23.bingoreloaded.settings.gamemode.BingoGamemodes;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class GameHistoryMenu extends BasicMenu {

	enum ScoreCondition {
		LOWEST_TIME,
		BIGGEST_SCORE,
		;
	}

	private static final ItemTemplate NEXT = new ItemTemplate(0, ItemTypePaper.of(Material.STRUCTURE_VOID),
			PlayerDisplayTranslationKey.MENU_NEXT.translate()
					.color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD));

	private static final ItemTemplate PREVIOUS = new ItemTemplate(8, ItemTypePaper.of(Material.BARRIER),
			PlayerDisplayTranslationKey.MENU_PREVIOUS.translate()
					.color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD));

	private static final Map<BingoGamemode, Set<ScoreCondition>> SCORE_CONDITIONS_PER_MODE = Map.of(
			BingoGamemodes.BINGO, Set.of(ScoreCondition.LOWEST_TIME),
			BingoGamemodes.LOCKOUT, Set.of(ScoreCondition.LOWEST_TIME, ScoreCondition.BIGGEST_SCORE),
			BingoGamemodes.COMPLETE, Set.of(ScoreCondition.LOWEST_TIME),
			BingoGamemodes.HOTSWAP, Set.of(ScoreCondition.LOWEST_TIME, ScoreCondition.BIGGEST_SCORE),
			BingoGamemodes.BLITZ, Set.of(ScoreCondition.BIGGEST_SCORE)
	);

	private final GameRecordData historyData;
	private final List<MenuAction> categories = new ArrayList<>();
	private int categoryOffset = 0;

	public GameHistoryMenu(MenuBoard manager, GameRecordData historyData) {
		super(manager, Component.text("Game History"), 6);
		this.historyData = historyData;

		Map<UUID, BingoSettings> settingIds = historyData.getSettings();

		ScoreCondition scoring = ScoreCondition.LOWEST_TIME;

		StackedGroup stack = new StackedGroup(1, 2, 7, 4);
		// Sort settings by gamemode
		int i = 0;
		List<List<UUID>> groupedSettings = groupSettingsByMode(settingIds);
		for (List<UUID> group : groupedSettings) {
			List<GameRecord> records = historyData.getGamesFilteredBy(record -> {
				if (record.winningTeam() == null || record.winningTeam().isEmpty()) {
					return false;
				}

				for (UUID id : group) {
					if (record.settingsId().equals(id)) {
						return true;
					}
				}
				return false;
			});
			BingoSettings settings = settingIds.get(group.getFirst());
			int pageIndex = i;
			MenuAction action = new MenuAction() {
				@Override
				public void use(ActionArguments arguments) {
					stack.setCurrentGroup(GameHistoryMenu.this, pageIndex);
				}
			};

			PaginatedGroup<GameRecord> scores = new PaginatedGroup<>(1, 2, 7, 4, this::onScoresClicked);
//			addAction(new ItemTemplate(ItemType.of("minecraft:apple")), args -> {
//				scores.setPage(this, scores.getCurrentPage() + 1);
//			});

			List<GameRecord> orderedByTimeAsc = records.stream()
					.sorted(Comparator.comparing(GameRecord::playTime))
					.toList();

			int place = 1;
			List<ItemTemplate> templates = new ArrayList<>();
			for (GameRecord game : orderedByTimeAsc) {
				GameRecord.TeamRecord team = game.teams().get(game.winningTeam());
				ItemType type = ItemType.of("minecraft:dried_ghast");
				TextColor color = NamedTextColor.GRAY;
				switch (place) {
					case 1 -> {
						type = ItemType.of("minecraft:gold_ingot");
						color = TextColor.fromHexString("#fdd50e");
					}
					case 2 -> {
						type = ItemType.of("minecraft:iron_ingot");
						color = TextColor.fromHexString("#b9e4d8");
					}
					case 3 -> {
						type = ItemType.of("minecraft:copper_ingot");
						color = TextColor.fromHexString("#cc6c54");
					}
				};
				List<Component> description = new ArrayList<>();
				description.add(team.team().nameComponent().color(team.team().color()).decorate(TextDecoration.BOLD));
				for (GameRecord.ParticipantRecord participant : team.participants()) {
					description.add(Component.text(participant.displayName()));
				}

				List<String> scoreString = new ArrayList<>();
				for (ScoreCondition condition : SCORE_CONDITIONS_PER_MODE.get(settings.mode())) {
					switch (condition) {
						case LOWEST_TIME -> scoreString.add(GameTimer.getTimeAsString(game.playTime()));
						case BIGGEST_SCORE -> scoreString.add(String.valueOf(team.score()));
					}
				}
				String score = String.join(" - ", scoreString);

				templates.add(new ItemTemplate(type, Component.empty()
								.append(Component.text("#" + place + " - ").color(color).decorate(TextDecoration.BOLD))
								.append(Component.text(score).decorate(TextDecoration.ITALIC)), description)
						.setGlowing(place <= 3));
				place += 1;
			}

			scores.setItems(templates, orderedByTimeAsc);
			stack.addGroup(scores);

			ItemTemplate item = new ItemTemplate(i, ItemType.of("minecraft:paper"),
					Component.empty().append(Component.text("Category: ").color(NamedTextColor.GRAY))
							.append(settings.mode().asComponent())
							.append(Component.text(" "))
							.append(settings.size().asComponent()),
					Component.empty().append(Component.text("Kit: ").color(NamedTextColor.GRAY)).append(settings.kit().getDisplayName()),
					Component.empty().append(Component.text("Card: ").color(NamedTextColor.GRAY)).append(Component.text(settings.card())),
					Component.empty().append(Component.text("Games played: ").color(NamedTextColor.GRAY)).append(Component.text(records.size())),
					Component.empty(),
					Component.empty().append(BasicMenu.INPUT_LEFT_CLICK).append(Component.text("View Scoreboard").color(TextColor.fromHexString("#ff661c")).decorate(TextDecoration.BOLD)));
			action.setItem(item);
			categories.add(action);
			i++;
		}

		for (int j = 0; j < 9; j++) {
			addItem(BasicMenu.BLANK.copyToSlot(j, 1));
		}

		for (int k = 2; k < 6; k++) {
			addItem(BasicMenu.BLANK.copyToSlot(0, k));
		}

		for (int k = 2; k < 6; k++) {
			addItem(BasicMenu.BLANK.copyToSlot(8, k));
		}

		addScrollingCategories();
		stack.updateVisibleItems(this);
	}

	private void onScoresClicked(GameRecord record) {

	}

	private List<List<UUID>> groupSettingsByMode(Map<UUID, BingoSettings> allSettings) {
		List<List<UUID>> result = new ArrayList<>();
		outer: for (UUID settingsId : allSettings.keySet()) {
			BingoSettings settings = allSettings.get(settingsId);

			// Check if this setting matches any already placed groups
			for (List<UUID> ids : result) {
				UUID id = ids.getFirst();
				if (id.equals(settingsId)) {
					continue;
				}

				BingoSettings other = allSettings.get(id);
				if (other == settings) {
					continue;
				}

				if (inSameBracket(settings, other)) {
					ids.add(id);
					continue outer;
				}
			}

			// if settings is not already matched before, add a new result set (different settings).
			result.add(new ArrayList<>(List.of(settingsId)));
		}
		return result;
	}

	private boolean inSameBracket(BingoSettings settings, BingoSettings other) {
		if (settings.equals(other)) {
			return true;
		}

		if (settings.mode() != other.mode()) {
			return false;
		}

		if (settings.size() != other.size()) {
			return false;
		}

		if (!settings.card().equals(other.card())) {
			return false;
		}

		if (!settings.kit().equals(other.kit())) {
			return false;
		}

		if (settings.mode() == BingoGamemodes.BINGO) {
			return true;
		}
		else if (settings.mode() == BingoGamemodes.LOCKOUT) {
			return true;
		}
		else if (settings.mode() == BingoGamemodes.COMPLETE) {
			return settings.completeGoal() == other.completeGoal();
		}
		else if (settings.mode() == BingoGamemodes.HOTSWAP) {
			return settings.hotswapGoal() == other.hotswapGoal() && settings.expireHotswapTasks() == other.expireHotswapTasks();
		}
		else if (settings.mode() == BingoGamemodes.BLITZ) {
			return true;
		}
		else {
			return true;
		}
	}

	private void addScrollingCategories() {
		if (categories.size() <= 9) {
			for (int idx = 0; idx < categories.size(); idx++) {
				ItemTemplate item = categories.get(idx).item().copyToSlot(idx);
				addItem(item, categories.get(idx));
			}
			return;
		}

		addAction(NEXT, args -> scrollCategories(1));
		addAction(PREVIOUS, args -> scrollCategories(-1));
		for (int i = 0; i < 7; i++) {
			int offsetIndex = (i + categoryOffset) % categories.size();
			ItemTemplate item = categories.get(offsetIndex).item().copyToSlot(i + 1);
			addItem(item, categories.get(offsetIndex));
		}
	}

	void scrollCategories(int by) {
		categoryOffset = Math.clamp(categoryOffset + by, 0, categories.size() - 7);
		addScrollingCategories();
	}

}
