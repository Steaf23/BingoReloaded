package io.github.steaf23.bingoreloaded.gui.inventory;


import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.FilterType;
import io.github.steaf23.bingoreloaded.lib.inventory.InventoryMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.PaginatedSelectionMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.action.ComboBoxButtonAction;
import io.github.steaf23.bingoreloaded.lib.inventory.action.MenuAction;
import io.github.steaf23.bingoreloaded.lib.inventory.action.SpinBoxButtonAction;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.util.BingoPlayerSender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

public class AdminBingoMenu extends BasicMenu
{
    private final BingoSession session;

    private static final int DURATION_MAX = 60;
    private static final int TEAMSIZE_MAX = 64;

    private static final Component COUNTDOWN_INPUT_LORE = InventoryMenu.inputButtonText(Component.text("Click")).append(Component.text("toggle countdown type"));

    private static final ItemTemplate START = new ItemTemplate(6, 0,
            ItemTypePaper.of(Material.LIME_CONCRETE), BingoReloaded.applyTitleFormat(BingoMessage.OPTIONS_START.asPhrase()));
    private static final ItemTemplate END = new ItemTemplate(6, 0,
            ItemTypePaper.of(Material.RED_CONCRETE), BingoReloaded.applyTitleFormat(BingoMessage.OPTIONS_END.asPhrase()));
    private static final ItemTemplate JOIN = new ItemTemplate(2, 0,
            ItemTypePaper.of(Material.WHITE_GLAZED_TERRACOTTA), BingoReloaded.applyTitleFormat(BingoMessage.OPTIONS_TEAM.asPhrase()));
    private static final ItemTemplate CARD = new ItemTemplate(1, 2,
            ItemTypePaper.of(Material.MAP), BingoReloaded.applyTitleFormat(BingoMessage.OPTIONS_CARD.asPhrase()));
    private static final ItemTemplate KIT = new ItemTemplate(3, 2,
            ItemTypePaper.of(Material.LEATHER_HELMET), BingoReloaded.applyTitleFormat(BingoMessage.OPTIONS_KIT.asPhrase()));
    private static final ItemTemplate MODE = new ItemTemplate(1, 4,
            ItemTypePaper.of(Material.ENCHANTED_BOOK), BingoReloaded.applyTitleFormat(BingoMessage.OPTIONS_GAMEMODE.asPhrase()));
    private static final ItemTemplate EFFECTS = new ItemTemplate(3, 4,
            ItemTypePaper.of(Material.POTION), BingoReloaded.applyTitleFormat(BingoMessage.OPTIONS_EFFECTS.asPhrase()));

    private static final ItemTemplate COUNTDOWN_TYPE_DISABLED = new ItemTemplate(5, 2,
            ItemTypePaper.of(Material.COMPASS), BingoReloaded.applyTitleFormat("Countdown Disabled"),
            Component.text("No timer will be used to limit play time."))
            .addDescription("input", 10, COUNTDOWN_INPUT_LORE);
    private static final ItemTemplate COUNTDOWN_TYPE_DURATION = new ItemTemplate(5, 2,
            ItemTypePaper.of(Material.CLOCK), BingoReloaded.applyTitleFormat("Countdown Duration"),
            Component.text("Countdown timer will be enabled."),
            Component.text("The game will end after the timer runs out,"),
            Component.text("this removes the win goal condition from Hot-Swap and Complete-X."))
            .addDescription("input", 10, COUNTDOWN_INPUT_LORE);
    private static final ItemTemplate COUNTDOWN_TYPE_LIMIT = new ItemTemplate(5, 2,
            ItemTypePaper.of(Material.RECOVERY_COMPASS), BingoReloaded.applyTitleFormat("Countdown Time Limit"),
            Component.text("Countdown timer will be enabled."),
            Component.text("Any goal is still a valid win condition,"),
            Component.text("but the game will end after the timer runs out."))
            .addDescription("input", 10, COUNTDOWN_INPUT_LORE);

    private static final ItemTemplate DURATION = new ItemTemplate(5, 4,
            ItemTypePaper.of(Material.RECOVERY_COMPASS), BingoReloaded.applyTitleFormat("Countdown Duration"));
    private static final ItemTemplate TEAM_SIZE = new ItemTemplate(7, 2,
            ItemTypePaper.of(Material.ENDER_EYE), BingoReloaded.applyTitleFormat("Maximum Team Size"));
    private static final ItemTemplate PRESETS = new ItemTemplate(7, 4,
            ItemTypePaper.of(Material.CHEST_MINECART), BingoReloaded.applyTitleFormat("Setting Presets"));

    public AdminBingoMenu(MenuBoard menuBoard, BingoSession session) {
        super(menuBoard, BingoMessage.OPTIONS_TITLE.asPhrase(), 6);
        this.session = session;
    }

    @Override
    public void beforeOpening(PlayerHandle player) {
        super.beforeOpening(player);

        BingoSettings view = session.settingsBuilder.view();

        addAction(JOIN, arguments -> {
            TeamSelectionMenu selectionMenu = new TeamSelectionMenu(getMenuBoard(), session);
            selectionMenu.open(arguments.player());
        });
        addAction(KIT, arguments -> new KitOptionsMenu(getMenuBoard(), session).open(arguments.player()));
        addAction(MODE, arguments -> new GamemodeOptionsMenu(getMenuBoard(), session).open(arguments.player()));
        addAction(CARD, this::openCardPicker);
        addAction(EFFECTS, arguments -> new EffectOptionsMenu(getMenuBoard(), session.settingsBuilder).open(arguments.player()));
        addAction(PRESETS, arguments -> new SettingsPresetMenu(getMenuBoard(), session.settingsBuilder).open(arguments.player()));

        ItemTemplate teamSizeItem = TEAM_SIZE.copy();
        int maxTeamSize = view.maxTeamSize();
        updateTeamSizeLore(teamSizeItem, maxTeamSize);
        MenuAction teamSizeAction = new SpinBoxButtonAction(1, TEAMSIZE_MAX, maxTeamSize, value -> {
            session.settingsBuilder.maxTeamSize(value);
            updateTeamSizeLore(teamSizeItem, value);
        });
        teamSizeAction.setItem(teamSizeItem);

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
        addActions(teamSizeAction, durationAction, countdownAction);

        MenuAction startAction = new ComboBoxButtonAction.Builder("start", START.copy())
                .addOption("end", END.copy())
                .setCallback((clickedValue, newValue, args) -> {
					if (clickedValue.equals("start")) {
						if (!session.startGame()) {
							BingoPlayerSender.sendMessage(Component.text("Could not start game, see console for details.").color(NamedTextColor.RED), args.player());
							return false;
						}
						return true;
					}
					else if (clickedValue.equals("end")) {
                        session.endGame();
						return true;
                    }
					else {
						return false;
					}
                })
                .buildAction(ItemTemplate.slotFromXY(6, 0), session.isRunning() ? "end" : "start");
        addAction(startAction);
    }

    private void openCardPicker(MenuAction.ActionArguments arguments) {
        PlayerHandle player = arguments.player();
        BingoCardData cardsData = new BingoCardData();
        List<ItemTemplate> cards = new ArrayList<>();

        for (String cardName : cardsData.getCardNames()) {
            cards.add(new ItemTemplate(ItemTypePaper.of(Material.PAPER), Component.text(cardName),
                    BingoMessage.LIST_COUNT.asPhrase(Component.text(cardsData.getListNames(cardName).size())
                            .color(NamedTextColor.DARK_PURPLE))));
        }

        BasicMenu cardPicker = new PaginatedSelectionMenu(getMenuBoard(), Component.text("Choose A Card"), cards, FilterType.DISPLAY_NAME)
        {
            @Override
            public void onOptionClickedDelegate(InventoryClickEvent event, ItemTemplate clickedOption, PlayerHandle player) {
                String name = clickedOption.getPlainTextName();
                if (!name.isEmpty()) {
                    cardSelected(name);
                }
                close(player);
            }
        };
        cardPicker.open(player);
    }

    private void cardSelected(String cardName) {
        if (cardName == null) return;
        session.settingsBuilder.card(cardName);
    }

    private void updateDurationLore(ItemTemplate item, int duration) {
        item.setLore(Component.text("Timer set to " + duration + " minutes(s)").color(NamedTextColor.DARK_PURPLE),
                Component.text("for bingo games on countdown mode").color(NamedTextColor.DARK_PURPLE));
    }

    private void updateTeamSizeLore(ItemTemplate item, int value) {
        item.setLore(Component.text("(When changing this setting all currently").color(NamedTextColor.GRAY),
                Component.text("joined players will be kicked from their teams!)").color(NamedTextColor.GRAY),
                Component.text("Maximum team size set to " + value + " players.").color(NamedTextColor.DARK_PURPLE));
    }
}
