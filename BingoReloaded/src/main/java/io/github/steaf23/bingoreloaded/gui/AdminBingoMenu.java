package io.github.steaf23.bingoreloaded.gui;


import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gui.base.BasicMenu;
import io.github.steaf23.bingoreloaded.gui.base.FilterType;
import io.github.steaf23.bingoreloaded.gui.base.MenuBoard;
import io.github.steaf23.bingoreloaded.gui.base.PaginatedSelectionMenu;
import io.github.steaf23.bingoreloaded.gui.base.item.MenuItem;
import io.github.steaf23.bingoreloaded.gui.item.ComboBoxButtonAction;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

public class AdminBingoMenu extends BasicMenu
{
    private final BingoSession session;

    private static final MenuItem START = new MenuItem(4, 2,
            Material.LIME_CONCRETE, TITLE_PREFIX + BingoTranslation.OPTIONS_START.translate());
    private static final MenuItem END = new MenuItem(4, 2,
            Material.RED_CONCRETE, TITLE_PREFIX + BingoTranslation.OPTIONS_END.translate());
    private static final MenuItem JOIN = new MenuItem(4, 0,
            Material.WHITE_GLAZED_TERRACOTTA, TITLE_PREFIX + BingoTranslation.OPTIONS_TEAM.translate());
    private static final MenuItem LEAVE = new MenuItem(2, 1,
            Material.BARRIER, TITLE_PREFIX + BingoTranslation.OPTIONS_LEAVE.translate());
    private static final MenuItem KIT = new MenuItem(6, 1,
            Material.IRON_INGOT, TITLE_PREFIX + BingoTranslation.OPTIONS_KIT.translate());
    private static final MenuItem CARD = new MenuItem(2, 3,
            Material.MAP, TITLE_PREFIX + BingoTranslation.OPTIONS_CARD.translate());
    private static final MenuItem MODE = new MenuItem(6, 3,
            Material.ENCHANTED_BOOK, TITLE_PREFIX + BingoTranslation.OPTIONS_GAMEMODE.translate());
    private static final MenuItem EFFECTS = new MenuItem(4, 4,
            Material.POTION, TITLE_PREFIX + BingoTranslation.OPTIONS_EFFECTS.translate());
    private static final MenuItem PRESETS = new MenuItem(0, 5,
            Material.CHEST_MINECART, TITLE_PREFIX + "Setting Presets",
            org.bukkit.ChatColor.GRAY + "Click to apply settings from saved presets");
    private static final MenuItem EXTRA = new MenuItem(8, 5,
            Material.STRUCTURE_VOID, TITLE_PREFIX + BingoTranslation.MENU_NEXT.translate());

    public AdminBingoMenu(MenuBoard menuBoard, BingoSession session, ConfigData config) {
        super(menuBoard, BingoTranslation.OPTIONS_TITLE.translate(), 6);
        this.session = session;

        for (int i = 1; i < 8; i++) {
            addItem(BLANK.copyToSlot(i, 5));
        }

        addAction(JOIN, p -> {
            TeamSelectionMenu selectionMenu = new TeamSelectionMenu(menuBoard, session.teamManager);
            selectionMenu.open(p);
        });
        addAction(LEAVE, arguments -> {
            BingoParticipant gamePlayer = session.teamManager.getPlayerAsParticipant((Player) arguments.player());
            if (gamePlayer != null)
                session.removeParticipant(gamePlayer);
        });
        addAction(KIT, p -> new KitOptionsMenu(getMenuBoard(), session).open(p));
        addAction(MODE, p -> new GamemodeOptionsMenu(getMenuBoard(), session).open(p));
        addAction(CARD, this::openCardPicker);
        addAction(EFFECTS, p -> new EffectOptionsMenu(getMenuBoard(), session.settingsBuilder, session).open(p));
        addAction(PRESETS, p -> new SettingsPresetMenu(getMenuBoard(), session.settingsBuilder).open(p));
        addAction(EXTRA, p -> new ExtraBingoMenu(getMenuBoard(), session.settingsBuilder).open(p));

        MenuItem centerButton = START.copy();
        centerButton.setAction(new ComboBoxButtonAction(List.of("start", "end"), value -> {
            if (value.equals("start")) {
                centerButton.replaceStack(START);
                session.startGame();
            }
            else if (value.equals("end")) {
                centerButton.replaceStack(END);
                session.endGame();
            }
        }));
    }

    private void openCardPicker(ActionArguments arguments) {
        HumanEntity player = arguments.player();
        BingoCardData cardsData = new BingoCardData();
        List<MenuItem> cards = new ArrayList<>();

        for (String cardName : cardsData.getCardNames()) {
            cards.add(new MenuItem(Material.PAPER, cardName,
                    ChatColor.DARK_PURPLE + BingoTranslation.LIST_COUNT.translate(
                            "" + cardsData.getListNames(cardName).size())));
        }

        BasicMenu cardPicker = new PaginatedSelectionMenu(getMenuBoard(), "Choose A Card", cards, FilterType.DISPLAY_NAME)
        {
            @Override
            public void onOptionClickedDelegate(InventoryClickEvent event, MenuItem clickedOption, HumanEntity player) {
                if (!clickedOption.getName().isEmpty()) {
                    cardSelected(clickedOption.getName());
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
}
