package io.github.steaf23.bingoreloaded.gui.inventory;


import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.TaskTagData;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.FilterType;
import io.github.steaf23.bingoreloaded.lib.inventory.InventoryMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.MenuFilterSettings;
import io.github.steaf23.bingoreloaded.lib.inventory.PaginatedDataMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.action.ComboBoxButtonAction;
import io.github.steaf23.bingoreloaded.lib.inventory.action.MenuAction;
import io.github.steaf23.bingoreloaded.lib.inventory.action.SpinBoxButtonAction;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.player.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.settings.gamemode.BingoGamemodes;
import io.github.steaf23.bingoreloaded.settings.gamemode.GamemodeFeature;
import io.github.steaf23.bingoreloaded.util.BingoPlayerSender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdminBingoMenu extends BasicMenu {

	private final BingoSession session;
	private final BingoCardData cardData;

	private static final int DURATION_MAX = 60;
	private static final int TEAMSIZE_MAX = 64;
	private static final int TEAMCOUNT_MAX = 64;

	private static final Component COUNTDOWN_INPUT_LORE = InventoryMenu.inputButtonText(Component.text("Click")).append(Component.text("toggle countdown type"));

	private static final ItemTemplate START = new ItemTemplate(6, 0,
			ItemTypePaper.of(Material.LIME_CONCRETE), BingoReloaded.applyTitleFormat(BingoMessage.OPTIONS_START.asPhrase()));
	private static final ItemTemplate END = new ItemTemplate(6, 0,
			ItemTypePaper.of(Material.RED_CONCRETE), BingoReloaded.applyTitleFormat(BingoMessage.OPTIONS_END.asPhrase()));
	private static final ItemTemplate JOIN = new ItemTemplate(2, 0,
			ItemTypePaper.of(Material.WHITE_GLAZED_TERRACOTTA), BingoReloaded.applyTitleFormat(BingoMessage.OPTIONS_TEAM.asPhrase()));
	private static final ItemTemplate CARD = new ItemTemplate(0, 2,
			ItemTypePaper.of(Material.MAP), BingoReloaded.applyTitleFormat(BingoMessage.OPTIONS_CARD.asPhrase()));
	private static final ItemTemplate KIT = new ItemTemplate(2, 2,
			ItemTypePaper.of(Material.LEATHER_HELMET), BingoReloaded.applyTitleFormat(BingoMessage.OPTIONS_KIT.asPhrase()));
	private static final ItemTemplate MODE = new ItemTemplate(1, 4,
			ItemTypePaper.of(Material.ENCHANTED_BOOK), BingoReloaded.applyTitleFormat(BingoMessage.OPTIONS_GAMEMODE.asPhrase()));
	private static final ItemTemplate EFFECTS = new ItemTemplate(3, 4,
			ItemTypePaper.of(Material.POTION), BingoReloaded.applyTitleFormat(BingoMessage.OPTIONS_EFFECTS.asPhrase()));

	private static final ItemTemplate COUNTDOWN_TYPE_DISABLED = new ItemTemplate(4, 2,
			ItemTypePaper.of(Material.COMPASS), BingoReloaded.applyTitleFormat("Countdown Disabled"),
			Component.text("No timer will be used to limit play time."))
			.addDescription("input", 10, COUNTDOWN_INPUT_LORE);
	private static final ItemTemplate COUNTDOWN_TYPE_DURATION = new ItemTemplate(4, 2,
			ItemTypePaper.of(Material.CLOCK), BingoReloaded.applyTitleFormat("Countdown Duration"),
			Component.text("Countdown timer will be enabled."),
			Component.text("The game will end after the timer runs out,"),
			Component.text("this removes the win goal condition from Hot-Swap and Complete-X."))
			.addDescription("input", 10, COUNTDOWN_INPUT_LORE);
	private static final ItemTemplate COUNTDOWN_TYPE_LIMIT = new ItemTemplate(4, 2,
			ItemTypePaper.of(Material.RECOVERY_COMPASS), BingoReloaded.applyTitleFormat("Countdown Time Limit"),
			Component.text("Countdown timer will be enabled."),
			Component.text("Any goal is still a valid win condition,"),
			Component.text("but the game will end after the timer runs out."))
			.addDescription("input", 10, COUNTDOWN_INPUT_LORE);

	private static final ItemTemplate DURATION = new ItemTemplate(5, 4,
			ItemTypePaper.of(Material.RECOVERY_COMPASS), BingoReloaded.applyTitleFormat("Countdown Duration"));
	private static final ItemTemplate TEAM_SIZE = new ItemTemplate(6, 2,
			ItemTypePaper.of(Material.ENDER_EYE), BingoReloaded.applyTitleFormat("Maximum Team Size"));
	private static final ItemTemplate TEAM_COUNT = new ItemTemplate(8, 2,
			ItemTypePaper.of(Material.ENDER_PEARL), BingoReloaded.applyTitleFormat("Maximum Team Count")).setMaxStackSize(64);
	private static final ItemTemplate PRESETS = new ItemTemplate(7, 4,
			ItemTypePaper.of(Material.CHEST_MINECART), BingoReloaded.applyTitleFormat("Setting Presets"));

	public AdminBingoMenu(MenuBoard menuBoard, BingoSession session) {
		super(menuBoard, BingoMessage.OPTIONS_TITLE.asPhrase(), 6);
		this.session = session;
		this.cardData = new BingoCardData();
	}

	@Override
	public void beforeOpening(PlayerHandle player) {
		super.beforeOpening(player);

		BingoSettings view = session.settingsBuilder.view();

		Component selected = Component.text("Selected: ").color(NamedTextColor.YELLOW).decorate(TextDecoration.ITALIC);

		BingoSettings settings = session.settingsBuilder.view();

		List<Component> cardLore = new ArrayList<>();
		cardLore.add(selected);
		cardLore.add(Component.text(" - ").append(Component.text(settings.card().cardName())));
		if (!settings.card().excludedTags().isEmpty()) {
			cardLore.add(tagDescription(settings.card().excludedTags()));
		}
		ItemTemplate cardItem = CARD.copy().setLore(cardLore.toArray(Component[]::new));

		ItemTemplate kitItem = KIT.copy().setLore(selected,
				Component.text(" - ").append(settings.kit().getDisplayName()));

		List<Component> modeLore = new ArrayList<>();
		modeLore.add(selected);
		modeLore.add(Component.text(" - ").append(settings.mode().asComponent()));
		modeLore.add(Component.text("   Size: ").append(settings.size().asComponent()));
		for (GamemodeFeature feature : settings.mode().featureSet()) {
			switch (feature) {
				case UNIQUE_CARD -> {
					modeLore.add(settings.differentCardPerTeam() ?
							Component.text("   Different cards generated").color(NamedTextColor.RED) :
							Component.text("   Same cards generated").color(NamedTextColor.GRAY));
				}
				case HOTSWAP_WIN_GOAL -> {
					modeLore.add(Component.text("   Win goal: ").append(Component.text(settings.hotswapGoal())));
				}
				case COMPLETE_WIN_GOAL -> {
					modeLore.add(Component.text("   Win goal: ").append(Component.text(settings.completeGoal())));
				}
				case TASK_EXPIRATION -> {
					modeLore.add(settings.expireHotswapTasks() ?
							Component.text("   Tasks expire").color(NamedTextColor.RED) :
							Component.text("   Tasks do not expire").color(NamedTextColor.GRAY));
				}
			}
		}

		ItemTemplate modeItem = MODE.copy().setLore(modeLore.toArray(Component[]::new));

		List<Component> effects = new ArrayList<>(List.of(EffectOptionFlags.effectsToText(settings.effects())));
		effects.addFirst(selected);
		ItemTemplate effectsItem = EFFECTS.copy().setLore(effects.toArray(Component[]::new));

		addAction(JOIN, arguments -> {
			TeamSelectionMenu selectionMenu = new TeamSelectionMenu(getMenuBoard(), session);
			selectionMenu.open(arguments.player());
		});
		addAction(kitItem, arguments -> new KitOptionsMenu(getMenuBoard(), session).open(arguments.player()));
		addAction(modeItem, arguments -> new GamemodeOptionsMenu(getMenuBoard(), session).open(arguments.player()));
		addAction(cardItem, this::openCardPicker);
		addAction(effectsItem, arguments -> new EffectOptionsMenu(getMenuBoard(), session.settingsBuilder).open(arguments.player()));
		addAction(PRESETS, arguments -> new SettingsPresetMenu(getMenuBoard(), session.settingsBuilder).open(arguments.player()));

		ItemTemplate teamSizeItem = TEAM_SIZE.copy();
		int maxTeamSize = view.maxTeamSize();
		updateTeamSizeLore(teamSizeItem, maxTeamSize);
		MenuAction teamSizeAction = new SpinBoxButtonAction(1, TEAMSIZE_MAX, maxTeamSize, value -> {
			session.settingsBuilder.maxTeamSize(value);
			updateTeamSizeLore(teamSizeItem, value);
		});
		teamSizeAction.setItem(teamSizeItem);

		ItemTemplate teamCountItem = TEAM_COUNT.copy();
		int maxTeamCount = view.maxTeamCount();
		MenuAction teamCountAction;
		if (view.mode() == BingoGamemodes.BLITZ) {
			updateTeamCountLore(teamCountItem, view.maxTeamCount());
			teamCountItem.addDescription("warning", 1, Component.text("Cannot change team count when playing Blitz!").color(NamedTextColor.RED));
			teamCountAction = new SpinBoxButtonAction(1, maxTeamCount, maxTeamCount, value -> {
				BingoPlayerSender.sendMessage(Component.text("Cannot change team count when playing Blitz!").color(NamedTextColor.RED), player);
			});
		} else {
			updateTeamCountLore(teamCountItem, maxTeamCount);
			teamCountAction = new SpinBoxButtonAction(1, TEAMCOUNT_MAX, maxTeamCount, value -> {
				session.settingsBuilder.maxTeamCount(value);
				updateTeamCountLore(teamCountItem, value);
			});
		}
		teamCountAction.setItem(teamCountItem);

		ItemTemplate durationItem = DURATION.copy();
		int duration = view.countdownDuration();
		updateDurationLore(durationItem, duration);
		MenuAction durationAction = new SpinBoxButtonAction(1, DURATION_MAX, duration, value -> {
			session.settingsBuilder.countdownGameDuration(value);
			updateDurationLore(durationItem, value);
		});
		durationAction.setItem(durationItem);

		MenuAction countdownAction = new ComboBoxButtonAction.Builder("DISABLED", COUNTDOWN_TYPE_DISABLED.copy())
				.addOption("DURATION", COUNTDOWN_TYPE_DURATION.copy())
				.addOption("TIME_LIMIT", COUNTDOWN_TYPE_LIMIT.copy())
				.setCallback((oldValue, newValue, arguments) -> {
					session.settingsBuilder.countdownType(BingoSettings.CountdownType.valueOf(newValue));
					return true;
				})
				.buildAction(COUNTDOWN_TYPE_DISABLED.getSlot(), view.countdownType().name());
		addActions(teamSizeAction, teamCountAction, durationAction, countdownAction);

		MenuAction startAction = new ComboBoxButtonAction.Builder("start", START.copy())
				.addOption("end", END.copy())
				.setCallback((clickedValue, newValue, args) -> {
					if (clickedValue.equals("start")) {
						if (!session.startGame()) {
							BingoPlayerSender.sendMessage(Component.text("Could not start game, see console for details.").color(NamedTextColor.RED), args.player());
							return false;
						}
						return true;
					} else if (clickedValue.equals("end")) {
						session.endGame();
						return true;
					} else {
						return false;
					}
				})
				.buildAction(ItemTemplate.slotFromXY(6, 0), session.isRunning() ? "end" : "start");
		addAction(startAction);
	}

	private void openCardPicker(MenuAction.ActionArguments arguments) {
		PlayerHandle player = arguments.player();
		BingoCardData cardsData = new BingoCardData();

		PaginatedDataMenu<String> cardPicker = new PaginatedDataMenu<>(
				getMenuBoard(),
				Component.text("Choose A Card"),
				new ArrayList<>(cardsData.getCardNames()),
				FilterType.NONE) {
			@Override
			public void onOptionClickedDelegate(InventoryClickEvent event, String clickedOption, PlayerHandle player) {
				if (!clickedOption.isEmpty()) {
					if (event.isLeftClick()) {
						cardSelected(clickedOption);
					} else if (event.isRightClick()) {
						new TagExclusionMenu(AdminBingoMenu.this, cardData, clickedOption).open(player);
					}
				}
				close(player);
			}

			@Override
			public ItemTemplate toItem(String s, boolean isSelected) {
				return new ItemTemplate(ItemTypePaper.of(Material.PAPER), Component.text(s),
						BingoMessage.LIST_COUNT.asPhrase(Component.text(cardsData.getListNames(s).size())
								.color(NamedTextColor.DARK_PURPLE)))
						.addDescription("input", 10,
								InventoryMenu.INPUT_LEFT_CLICK.append(Component.text("select this card.")),
								InventoryMenu.INPUT_RIGHT_CLICK.append(Component.text("also edit allowed tasks using tags.")));
			}

			@Override
			public boolean filterData(String s, MenuFilterSettings filter) {
				return false;
			}

			@Override
			public boolean openOnce() {
				return true;
			}
		};
		cardPicker.open(player);
	}

	private void cardSelected(String cardName) {
		if (cardName == null) return;

		session.settingsBuilder.cardName(cardName);
	}

	public void cardAndTagSelected(String cardName, List<String> tags) {
		cardSelected(cardName);
		session.settingsBuilder.excludedTags(new HashSet<>(tags));
	}

	private void updateDurationLore(ItemTemplate item, int duration) {
		item.setLore(
				Component.text("Timer set to " + duration + " minutes(s)"),
				Component.text("for bingo games on countdown mode"));
	}

	private void updateTeamSizeLore(ItemTemplate item, int value) {
		item.setLore(
				Component.text("Selected:").color(NamedTextColor.YELLOW).decorate(TextDecoration.ITALIC),
				Component.text(" - ").append(Component.text(value)));

		item.addDescription("warning", 1,
				Component.text("(When changing this setting all currently").color(NamedTextColor.GRAY),
				Component.text("joined players will be kicked from their teams!)").color(NamedTextColor.GRAY));
	}

	private void updateTeamCountLore(ItemTemplate item, int value) {
		item.setLore(
				Component.text("Selected:").color(NamedTextColor.YELLOW).decorate(TextDecoration.ITALIC),
				Component.text(" - ").append(Component.text(value)));

		item.addDescription("warning", 1,
				Component.text("(When changing this setting all currently").color(NamedTextColor.GRAY),
				Component.text("joined players will be kicked from their teams!)").color(NamedTextColor.GRAY));
	}

	public Component tagDescription(Set<String> tags) {
		TaskTagData tagData = cardData.tags();
		Component result = Component.text("   Excluding tasks tagged with ").color(NamedTextColor.GRAY);
		int i = 0;
		for (String tag : tags) {
			result = result.append(Component.text("<" + tag + ">").color(tagData.getAllTags().getOrDefault(tag, new TaskTagData.TaskTag(NamedTextColor.WHITE)).color()));
			if (i < tags.size() - 1) {
				result = result.append(Component.text(", "));
			}
			i++;
		}
		return result;
	}
}
