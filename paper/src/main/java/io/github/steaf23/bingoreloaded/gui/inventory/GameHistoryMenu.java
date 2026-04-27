package io.github.steaf23.bingoreloaded.gui.inventory;

import io.github.steaf23.bingoreloaded.data.record.GameRecord;
import io.github.steaf23.bingoreloaded.data.record.GameRecordData;
import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class GameHistoryMenu extends BasicMenu {

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

	private static final ItemTemplate NEXT = new ItemTemplate(0, ItemTypePaper.of(Material.STRUCTURE_VOID),
			PlayerDisplayTranslationKey.MENU_NEXT.translate()
					.color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD));

	private static final ItemTemplate PREVIOUS = new ItemTemplate(8, ItemTypePaper.of(Material.BARRIER),
			PlayerDisplayTranslationKey.MENU_PREVIOUS.translate()
					.color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD));

	private static final ItemTemplate ORDER_BY_TIME = new ItemTemplate(0, 1, ItemType.of("minecraft:clock"), Component.text("Sort by Fastest Time"));
	private static final ItemTemplate ORDER_BY_SCORE = new ItemTemplate(0, 1, ItemType.of("minecraft:compass"), Component.text("Sort by Highest Score"));

	private static final Map<BingoGamemode, Set<ScoreCondition>> SCORE_CONDITIONS_PER_MODE = Map.of(
			BingoGamemodes.BINGO, Set.of(ScoreCondition.LOWEST_TIME),
			BingoGamemodes.LOCKOUT, Set.of(ScoreCondition.LOWEST_TIME, ScoreCondition.BIGGEST_SCORE),
			BingoGamemodes.COMPLETE, Set.of(ScoreCondition.LOWEST_TIME),
			BingoGamemodes.HOTSWAP, Set.of(ScoreCondition.LOWEST_TIME, ScoreCondition.BIGGEST_SCORE),
			BingoGamemodes.BLITZ, Set.of(ScoreCondition.BIGGEST_SCORE)
	);

	private final GameRecordData historyData;
	private final ScrollableItemBar<Category> categories = new ScrollableItemBar<>(this, 0, 0, 9, SelectionModel.SelectMode.SINGLE);
	private final StackedGroup stack;

	public GameHistoryMenu(MenuBoard manager, GameRecordData historyData) {
		super(manager, Component.text("Game History"), 6);
		this.historyData = historyData;
		this.categories.setItemClickedCallback((idx, item, category) -> showCategory(idx, category));

		Map<UUID, BingoSettings> settingIds = historyData.getSettings();

		List<Category> categoryData = new ArrayList<>();
		stack = new StackedGroup(1, 2, 7, 4);
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

			ItemTemplate item = new ItemTemplate(i, ItemType.of("minecraft:leather_horse_armor"),
					Component.empty().append(Component.text("Category: ").color(NamedTextColor.GRAY))
							.append(settings.mode().asComponent())
							.append(Component.text(" "))
							.append(settings.size().asComponent()),
					Component.empty().append(Component.text("Kit: ").color(NamedTextColor.GRAY)).append(settings.kit().getDisplayName()),
					Component.empty().append(Component.text("Card: ").color(NamedTextColor.GRAY)).append(Component.text(settings.card().cardName())),
					Component.empty().append(Component.text("Games played: ").color(NamedTextColor.GRAY)).append(Component.text(records.size())),
					Component.empty(),
					Component.empty().append(BasicMenu.INPUT_LEFT_CLICK).append(Component.text("View Scoreboard").color(TextColor.fromHexString("#ff661c")).decorate(TextDecoration.BOLD)))
					.setLeatherColor(settings.mode().getColor());
			categoryData.add(new Category(item, settings.mode()));
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

		showCategory(0, categoryData.getFirst());
	}

	private List<List<UUID>> groupSettingsByMode(Map<UUID, BingoSettings> allSettings) {
		List<List<UUID>> result = new ArrayList<>();
		outer:
		for (UUID settingsId : allSettings.keySet()) {
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

		stack.setCurrentGroup(GameHistoryMenu.this, pageIndex);
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
}
