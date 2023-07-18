package io.github.steaf23.bingoreloaded.gui;


import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gui.base.*;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class AdminBingoMenu extends BasicMenu
{
    private final BingoSession session;

    private final MenuItem start = new MenuItem(4, 2,
            Material.LIME_CONCRETE, TITLE_PREFIX + BingoTranslation.OPTIONS_START.translate());
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
    private static final MenuItem EXTRA = new MenuItem(8, 5,
            Material.STRUCTURE_VOID, TITLE_PREFIX + BingoTranslation.MENU_NEXT.translate());

    public AdminBingoMenu(MenuManager menuManager, BingoSession session, ConfigData config) {
        super(menuManager, BingoTranslation.OPTIONS_TITLE.translate(), 6);
        this.session = session;

        for (int i = 0; i < 8; i++) {
            addItem(BLANK.copyToSlot(i, 5));
        }

        addAction(JOIN, p -> session.teamManager.openTeamSelector(getMenuManager(), (Player) p));
        addAction(LEAVE, p -> {
            BingoParticipant gamePlayer = session.teamManager.getBingoParticipant((Player) p);
            if (gamePlayer != null)
                session.removeParticipant(gamePlayer);
        });
        addAction(KIT, p -> new KitOptionsMenu(getMenuManager(), session).open(p));
        addAction(MODE, p -> new GamemodeOptionsMenu(getMenuManager(), session).open(p));
        addAction(CARD, this::openCardPicker);
        addAction(EFFECTS, p -> new EffectOptionsMenu(getMenuManager(), session.settingsBuilder, session).open(p));
        addAction(EXTRA, p -> new ExtraBingoMenu(getMenuManager(), session.settingsBuilder, config).open(p));

        updateStartButton();
    }

    private void openCardPicker(HumanEntity player) {
        BingoCardData cardsData = new BingoCardData();
        List<MenuItem> cards = new ArrayList<>();

        for (String cardName : cardsData.getCardNames()) {
            cards.add(new MenuItem(Material.PAPER, cardName,
                    ChatColor.DARK_PURPLE + BingoTranslation.LIST_COUNT.translate(
                            "" + cardsData.getListNames(cardName).size())));
        }

        BasicMenu cardPicker = new PaginatedSelectionMenu(getMenuManager(), "Choose A Card", cards, FilterType.DISPLAY_NAME)
        {
            @Override
            public void onOptionClickedDelegate(InventoryClickEvent event, MenuItem clickedOption, HumanEntity player) {
                ItemMeta meta = clickedOption.getItemMeta();
                if (meta != null) {
                    cardSelected(meta.getDisplayName());
                }
                close(player);
            }
        };
        cardPicker.open(player);
    }

    private void updateStartButton() {
        if (session.isRunning()) {
            start.setType(Material.RED_CONCRETE);
            ItemMeta meta = start.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(TITLE_PREFIX + BingoTranslation.OPTIONS_END.translate());
                start.setItemMeta(meta);
            }
        } else {
            start.setType(Material.LIME_CONCRETE);
            ItemMeta meta = start.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(TITLE_PREFIX + BingoTranslation.OPTIONS_START.translate());
                start.setItemMeta(meta);
            }
        }
        addAction(start, p -> {
            if (session.isRunning()) {
                session.endGame();
            } else {
                session.startGame();
            }
            updateStartButton();
        });
    }

    private void cardSelected(String cardName) {
        if (cardName == null) return;
        session.settingsBuilder.card(cardName);
    }
}
