package io.github.steaf23.bingoreloaded.gui.inventory;

import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.BingoSettingsData;
import io.github.steaf23.bingoreloaded.data.record.GameRecord;
import io.github.steaf23.bingoreloaded.data.record.LeaderboardData;
import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.group.PaginatedGroup;
import io.github.steaf23.bingoreloaded.lib.inventory.group.ScrollableItemBar;
import io.github.steaf23.bingoreloaded.lib.inventory.group.SelectionModel;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LeaderboardMenu extends BasicMenu {

	private static BingoSettingsData settingsData = null;

	private final LeaderboardData leaderboard;

	enum ScoreCondition {
		LOWEST_TIME(Comparator.comparing(GameRecord::playTime)),
		BIGGEST_SCORE(Comparator.<GameRecord>comparingInt((record) -> record.teams().get(record.winningTeam()).score()).reversed()),
		;

		private final Comparator<GameRecord> comparator;

		ScoreCondition(Comparator<GameRecord> comparator) {
			this.comparator = comparator;
		}
	}

	private record Category(ItemTemplate item, BingoGamemode mode) {

	}

	private record SettingsGroup(String presetName, BingoSettings settings, Set<String> aliases) {}

	private static final ItemTemplate NEXT = new ItemTemplate(0, ItemTypePaper.of(Material.STRUCTURE_VOID),
			PlayerDisplayTranslationKey.MENU_NEXT.translate()
					.color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD));

	private static final ItemTemplate PREVIOUS = new ItemTemplate(8, ItemTypePaper.of(Material.BARRIER),
			PlayerDisplayTranslationKey.MENU_PREVIOUS.translate()
					.color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD));

	private static final ItemTemplate ORDER_BY_TIME = new ItemTemplate(0, 1, ItemType.of("minecraft:clock"), BingoMessage.LEADERBOARD_SORT_TIME.asPhrase());
	private static final ItemTemplate ORDER_BY_SCORE = new ItemTemplate(0, 1, ItemType.of("minecraft:compass"), BingoMessage.LEADERBOARD_SORT_SCORE.asPhrase());

	private static final Map<BingoGamemode, Set<ScoreCondition>> SCORE_CONDITIONS_PER_MODE = Map.of(
			BingoGamemodes.BINGO, Set.of(ScoreCondition.LOWEST_TIME),
			BingoGamemodes.LOCKOUT, Set.of(ScoreCondition.LOWEST_TIME, ScoreCondition.BIGGEST_SCORE),
			BingoGamemodes.COMPLETE, Set.of(ScoreCondition.LOWEST_TIME),
			BingoGamemodes.HOTSWAP, Set.of(ScoreCondition.LOWEST_TIME, ScoreCondition.BIGGEST_SCORE),
			BingoGamemodes.BLITZ, Set.of(ScoreCondition.BIGGEST_SCORE)
	);

	private final ScrollableItemBar<Category> categories = new ScrollableItemBar<>(this, 0, 0, 8, SelectionModel.SelectMode.SINGLE);
	private final StackedGroup stack;

	public LeaderboardMenu(MenuBoard manager, LeaderboardData leaderboard, PlayerHandle player, boolean categorizeByPresets) {
		super(manager, BingoMessage.LEADERBOARD_TITLE.asPhrase(), 6);
		this.leaderboard = leaderboard;

		if (settingsData == null) {
			settingsData = new BingoSettingsData();
		}

		this.categories.setItemClickedCallback((idx, item, category) -> {
			showCategory(idx, category);
			return null;
		});

		Map<String, BingoSettings> allSettings = this.leaderboard.getSettings(settingsData);
		stack = new StackedGroup(1, 2, 7, 4);

		List<Category> categoryData = new ArrayList<>();
		int i = 0;
		List<SettingsGroup> groupedSettings = categorizeByPresets ? groupSettingsByPreset(allSettings) : groupSettingsInSameBracket(allSettings);
		for (SettingsGroup group : groupedSettings) {
			List<GameRecord> records = leaderboard.getGamesFilteredBy(record -> {
				if(record.winningTeam() == null || record.winningTeam().isEmpty()) {
					return false;
				}

				return group.aliases().contains(record.settingsId());
			});
			BingoSettings settings = group.settings;

			StackedGroup categoriesStack = new StackedGroup(1, 2, 7, 4);
			for (ScoreCondition condition : SCORE_CONDITIONS_PER_MODE.get(settings.mode())) {
				PaginatedGroup<GameRecord> scores = new PaginatedGroup<>(1, 2, 7, 4, null, SelectionModel.SelectMode.MULTIPLE_OR_NONE, false);

				List<GameRecord> orderedByCondition = records.stream()
						.sorted(condition.comparator)
						.toList();

				int place = 1;
				List<ItemTemplate> templates = new ArrayList<>();
				for (GameRecord game : orderedByCondition) {
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
					}
					;
					List<Component> description = new ArrayList<>();
					description.add(team.team().nameComponent().color(team.team().color()).decorate(TextDecoration.BOLD));
					for (GameRecord.ParticipantRecord participant : team.participants()) {
						description.add(Component.text(participant.displayName()));
					}

					List<String> scoreString = new ArrayList<>();
					switch (condition) {
						case LOWEST_TIME -> scoreString.add(GameTimer.getTimeAsString(game.playTime()));
						case BIGGEST_SCORE -> scoreString.add(String.valueOf(team.score()));
					}

					String score = String.join(" - ", scoreString);

					templates.add(new ItemTemplate(type, Component.empty()
							.append(Component.text("#" + place + " - ").color(color).decorate(TextDecoration.BOLD))
							.append(Component.text(score).decorate(TextDecoration.ITALIC)), description));
					place += 1;
				}

				scores.setItems(templates, orderedByCondition);
				// Select the first 3 places to make them shiny.
				for (int j = 0; j < Math.min(templates.size(), 3); j++) {
					scores.selection().toggleSlot(j);
				}
				categoriesStack.addGroup(scores);
			}

			stack.addGroup(categoriesStack);

			ItemTemplate item = categoryItem(group, records.size());
			categoryData.add(new Category(item.copyToSlot(i), settings.mode()));
			i++;
		}
		categories.setItems(categoryData.stream()
				.map(Category::item)
				.toList(), categoryData);

		for (int j = 0; j < 9; j++) {
			addItem(BasicMenu.BLANK.copyToSlot(j, 1));
		}

		for (int k = 2; k < 6; k++) {
			addItem(BasicMenu.BLANK.copyToSlot(0, k));
		}

		for (int k = 2; k < 6; k++) {
			addItem(BasicMenu.BLANK.copyToSlot(8, k));
		}

		categories.updateVisibleItems(this);

		if (categoryData.isEmpty()) {
			return;
		}
		showCategory(0, categoryData.getFirst());
	}

	private List<SettingsGroup> groupSettingsInSameBracket(Map<String, BingoSettings> allSettings) {
		List<SettingsGroup> result = new ArrayList<>();
		outer:
		for (String settingsId : allSettings.keySet()) {
			BingoSettings settings = allSettings.get(settingsId);

			// Check if this setting matches any already placed groups
			for (SettingsGroup ids : result) {
				String id = ids.aliases.stream().findFirst().orElse("");
				if (id.equals(settingsId)) {
					continue;
				}

				BingoSettings other = allSettings.get(id);
				if (inSameBracket(settings, other)) {
					ids.aliases.add(settingsId);
					continue outer;
				}
			}

			// if settings is not already matched before, add a new result set (different settings).
			result.add(new SettingsGroup("", settings, new HashSet<>(Set.of(settingsId))));
		}
		return result;
	}

	private List<SettingsGroup> groupSettingsByPreset(Map<String, BingoSettings> allSettings) {
		List<SettingsGroup> result = new ArrayList<>();

		Map<String, Set<String>> presetsAndSimilarSettings = new HashMap<>();
		for (String name : allSettings.keySet()) {
			if (settingsData.containsSettings(name)) {
				presetsAndSimilarSettings.put(name, new HashSet<>());
			}
		}

		// Check which settings are secretly presets by comparing their contents.
		for (String preset : presetsAndSimilarSettings.keySet()) {
			BingoSettings presetSettings = allSettings.get(preset);
			Set<String> settingsLikePreset = new HashSet<>();
			for (String current : allSettings.keySet()) {
				BingoSettings currentSettings = allSettings.get(current);
				if (current.equals(preset) || presetSettings.equals(currentSettings)) {
					settingsLikePreset.add(current);
				}
			}
			result.add(new SettingsGroup(preset, presetSettings, settingsLikePreset));
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

		if (!settings.card().cardName().equals(other.card().cardName())) {
			return false;
		}

		if (!settings.kit().equals(other.kit())) {
			return false;
		}

		if (settings.mode() == BingoGamemodes.BINGO) {
			return true;
		} else if (settings.mode() == BingoGamemodes.LOCKOUT) {
			return true;
		} else if (settings.mode() == BingoGamemodes.COMPLETE) {
			return settings.completeGoal() == other.completeGoal();
		} else if (settings.mode() == BingoGamemodes.HOTSWAP) {
			return settings.hotswapGoal() == other.hotswapGoal() && settings.expireHotswapTasks() == other.expireHotswapTasks();
		} else if (settings.mode() == BingoGamemodes.BLITZ) {
			return true;
		} else {
			return true;
		}
	}

	private void showCategory(int pageIndex, Category current) {

		stack.setCurrentGroup(LeaderboardMenu.this, pageIndex);
		if (!(stack.getCurrentGroup() instanceof StackedGroup categoryGroup)) {
			return;
		}

		if (!(categoryGroup.getCurrentGroup() instanceof PaginatedGroup<?> paginatedGroup)) {
			return;
		}

		paginatedGroup.setPage(this, 0);

		updatePageNavigation(categoryGroup, paginatedGroup, current.mode());
	}

	private void updatePageNavigation(StackedGroup categoryPage, PaginatedGroup<?> page, BingoGamemode mode) {
		int currentPage = page.getCurrentPage();
		int pageCount = page.getPageCount();
		Component pageCountDesc = Component.text(String.format("%02d", currentPage + 1) + "/" + String.format("%02d", pageCount));

		ItemTemplate prevPage = PREVIOUS.copyToSlot(0, 5).setLore(pageCountDesc);
		ItemTemplate nextPage = NEXT.copyToSlot(8, 5).setLore(pageCountDesc);

		int orderIndex = 0;
		int orderSlot = ItemTemplate.slotFromXY(0, 1);
		if (SCORE_CONDITIONS_PER_MODE.get(mode).contains(ScoreCondition.LOWEST_TIME)) {
			ItemTemplate timeOrder = ORDER_BY_TIME.copyToSlot(orderSlot + orderIndex);
			int finalOrderIndex = orderIndex;
			if (categoryPage.currentGroupIndex() == orderIndex) {
				timeOrder.setGlowing(true);
			}
			addAction(timeOrder, (args) -> {
				categoryPage.setCurrentGroup(this, finalOrderIndex);
				PlatformResolver.get().runTask((t) -> {
					updatePageNavigation(categoryPage, page, mode);
				});
			});
			orderIndex++;
		}

		if (SCORE_CONDITIONS_PER_MODE.get(mode).contains(ScoreCondition.BIGGEST_SCORE)) {
			ItemTemplate scoreOrder = ORDER_BY_SCORE.copyToSlot(orderSlot + orderIndex);
			int finalOrderIndex = orderIndex;
			if (categoryPage.currentGroupIndex() == orderIndex) {
				scoreOrder.setGlowing(true);
			}
			addAction(scoreOrder, (args) -> {
				categoryPage.setCurrentGroup(this, finalOrderIndex);
				PlatformResolver.get().runTask((t) -> {
					updatePageNavigation(categoryPage, page, mode);
				});
			});
			orderIndex++;
		}

		for (int unusedOrderIndex = orderIndex; unusedOrderIndex < 9; unusedOrderIndex++) {
			addItem(BLANK.copyToSlot(orderSlot + orderIndex));
		}

		if (currentPage > 0) {
			addAction(prevPage, (args) -> {
				page.previousPage(this);
				PlatformResolver.get().runTask((t) -> {
					updatePageNavigation(categoryPage, page, mode);
				});
			});
		} else {
			addItem(BLANK.copyToSlot(0, 5));
		}

		if (currentPage < pageCount - 1) {
			addAction(nextPage, (args) -> {
				page.nextPage(this);
				PlatformResolver.get().runTask((t) -> {
					updatePageNavigation(categoryPage, page, mode);
				});
			});
		} else {
			addItem(BLANK.copyToSlot(8, 5));
		}
	}

	private ItemTemplate categoryItem(SettingsGroup group, int numGamesPlayed) {
		BingoSettings settings = group.settings();
		List<Component> lore = new ArrayList<>();

		if (!group.presetName().isEmpty()) {
			lore.add(Component.empty().append(BingoMessage.LEADERBOARD_SETTINGS.asPhrase().append(Component.text(": ")).color(NamedTextColor.GRAY)).append(Component.text(group.presetName())));
		}

		lore.addAll(List.of(
				Component.empty().append(BingoMessage.LEADERBOARD_KIT.asPhrase().append(Component.text(": ")).color(NamedTextColor.GRAY)).append(settings.kit().getDisplayName()),
				Component.empty().append(BingoMessage.LEADERBOARD_CARD.asPhrase().append(Component.text(": ")).color(NamedTextColor.GRAY)).append(Component.text(settings.card().cardName())),
				Component.empty().append(BingoMessage.LEADERBOARD_GAMES_PLAYED.asPhrase().append(Component.text(": ")).color(NamedTextColor.GRAY)).append(Component.text(numGamesPlayed)),
				Component.empty(),
				Component.empty().append(BasicMenu.INPUT_LEFT_CLICK).append(BingoMessage.LEADERBOARD_PROMPT.asPhrase().color(TextColor.fromHexString("#ff661c")).decorate(TextDecoration.BOLD))
			));

		ItemTemplate item = new ItemTemplate(ItemType.of("minecraft:leather_horse_armor"),
				Component.empty().append(BingoMessage.LEADERBOARD_CATEGORY.asPhrase().append(Component.text(": ")).color(NamedTextColor.GRAY))
						.append(settings.mode().asComponent())
						.append(Component.text(" "))
						.append(settings.size().asComponent()),
				lore)
				.setLeatherColor(settings.mode().getColor());

		return item;
	}

}
