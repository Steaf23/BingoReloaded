package io.github.steaf23.bingoreloaded.gui;


import io.github.steaf23.bingoreloaded.BingoReloadedCore;
import io.github.steaf23.bingoreloaded.BingoSession;
import io.github.steaf23.bingoreloaded.BingoSettingsBuilder;
import io.github.steaf23.bingoreloaded.data.BingoCardsData;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.gui.base.FilterType;
import io.github.steaf23.bingoreloaded.gui.base.InventoryItem;
import io.github.steaf23.bingoreloaded.gui.base.MenuInventory;
import io.github.steaf23.bingoreloaded.gui.base.PaginatedPickerMenu;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class BingoMenu extends MenuInventory
{
    private final BingoSession session;
    private final ConfigData config;

    private final InventoryItem start = new InventoryItem(4, 2,
            Material.LIME_CONCRETE, TITLE_PREFIX + BingoTranslation.OPTIONS_START.translate());
    private static final InventoryItem JOIN = new InventoryItem(4, 0,
            Material.WHITE_GLAZED_TERRACOTTA, TITLE_PREFIX + BingoTranslation.OPTIONS_TEAM.translate());
    private static final InventoryItem LEAVE = new InventoryItem(2, 1,
            Material.BARRIER, TITLE_PREFIX + BingoTranslation.OPTIONS_LEAVE.translate());
    private static final InventoryItem KIT = new InventoryItem(6, 1,
            Material.IRON_INGOT, TITLE_PREFIX + BingoTranslation.OPTIONS_KIT.translate());
    private static final InventoryItem CARD = new InventoryItem(2, 3,
            Material.MAP, TITLE_PREFIX + BingoTranslation.OPTIONS_CARD.translate());
    private static final InventoryItem MODE = new InventoryItem(6, 3,
            Material.ENCHANTED_BOOK, TITLE_PREFIX + BingoTranslation.OPTIONS_GAMEMODE.translate());
    private static final InventoryItem EFFECTS = new InventoryItem(4, 4,
            Material.POTION, TITLE_PREFIX + BingoTranslation.OPTIONS_EFFECTS.translate());
    private static final InventoryItem EXTRA = new InventoryItem(44,
            Material.STRUCTURE_VOID, TITLE_PREFIX + BingoTranslation.MENU_NEXT.translate());
    private static final InventoryItem VOTE = new InventoryItem(
            Material.EMERALD, "" + ChatColor.GREEN + ChatColor.BOLD + BingoTranslation.OPTIONS_VOTE.translate())
            .setGlowing(true).setKey("vote");

    private static final InventoryItem JOIN_P = JOIN.copyToSlot(2, 2);
    private static final InventoryItem LEAVE_P = LEAVE.copyToSlot(6, 2);

    private BingoMenu(BingoSession session, ConfigData config)
    {
        super(45, BingoTranslation.OPTIONS_TITLE.translate(), null);
        this.session = session;
        this.config = config;
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        BingoPlayer gamePlayer = session.teamManager.getBingoPlayer(player);
        if (!player.hasPermission("bingo.settings"))
        {
            if (slotClicked == JOIN_P.getSlot())
            {
                session.teamManager.openTeamSelector(player, this);
            }
            else if (slotClicked == LEAVE_P.getSlot())
            {
                if (gamePlayer != null)
                    session.removePlayer(gamePlayer);
            }
            else if (new InventoryItem(event.getCurrentItem()).isKeyEqual(VOTE))
            {
                VoteMenu menu = new VoteMenu(this);
                menu.open(player);
            }
            return;
        }

        BingoSettingsBuilder settings = session.settingsBuilder;

        if (slotClicked == JOIN.getSlot())
        {
            session.teamManager.openTeamSelector(player, this);
        }
        else if (slotClicked == LEAVE.getSlot())
        {
            if (gamePlayer != null)
                session.removePlayer(gamePlayer);
        }
        else if (slotClicked == KIT.getSlot())
        {
            KitOptionsMenu kitSelector = new KitOptionsMenu(this, session);
            kitSelector.open(player);
        }
        else if (slotClicked == MODE.getSlot())
        {
            GamemodeOptionsMenu gamemodeSelector = new GamemodeOptionsMenu(this, session);
            gamemodeSelector.open(player);
        }
        else if (slotClicked == CARD.getSlot())
        {
            openCardPicker(player);
        }
        else if (slotClicked == EFFECTS.getSlot())
        {
            EffectOptionsMenu effectSelector = new EffectOptionsMenu(this, settings, session);
            effectSelector.open(player);
        }
        else if (slotClicked == EXTRA.getSlot())
        {
            ExtraBingoMenu extraOptions = new ExtraBingoMenu(this, settings, config);
            extraOptions.open(player);
        }
        else if (slotClicked == start.getSlot())
        {
            if (session.isRunning())
            {
                session.endGame();
                start.setType(Material.LIME_CONCRETE);
                ItemMeta meta = start.getItemMeta();
                if (meta != null)
                {
                    meta.setDisplayName(TITLE_PREFIX + BingoTranslation.OPTIONS_START.translate());
                    start.setItemMeta(meta);
                }
                addOption(start);
            }
            else
            {
                session.startGame();
            }
        }
        else if (new InventoryItem(event.getCurrentItem()).isKeyEqual(VOTE))
        {
            VoteMenu menu = new VoteMenu(this);
            menu.open(player);
        }
    }

    private void openCardPicker(Player player)
    {
        BingoCardsData cardsData = new BingoCardsData();
        List<InventoryItem> cards = new ArrayList<>();

        for (String cardName : cardsData.getCardNames())
        {
            cards.add(new InventoryItem(Material.PAPER, cardName,
                    ChatColor.DARK_PURPLE + BingoTranslation.LIST_COUNT.translate(
                            "" + cardsData.getListNames(cardName).size())));
        }

        PaginatedPickerMenu cardPicker = new PaginatedPickerMenu(cards, BingoTranslation.OPTIONS_CARD.translate(),this, FilterType.DISPLAY_NAME)
        {
            @Override
            public void onOptionClickedDelegate(InventoryClickEvent event, InventoryItem clickedOption, Player player)
            {
                ItemMeta meta = clickedOption.getItemMeta();
                if (meta != null)
                {
                    cardSelected(meta.getDisplayName(), BingoReloadedCore.getWorldNameOfDimension(player.getWorld()));
                }
                close(player);
            }
        };
        cardPicker.open(player);
    }

    private void cardSelected(String cardName, String worldName)
    {
        if (cardName == null) return;
        new TranslatedMessage(BingoTranslation.CARD_SELECTED).color(ChatColor.GOLD).arg(cardName).sendAll(session);
        session.settingsBuilder.card(cardName);
    }

    /**
     * Opens a BingoMenu containing all options similar to the autobingo commands
     * @param player
     * @param gameSession
     */
    public static void openOptions(@NonNull Player player, @NonNull BingoSession gameSession, ConfigData config)
    {
        BingoMenu options = new BingoMenu(gameSession, config);
        if (gameSession.isRunning())
        {
            options.start.setType(Material.RED_CONCRETE);
            ItemMeta meta = options.start.getItemMeta();
            if (meta != null)
            {
                meta.setDisplayName(TITLE_PREFIX + BingoTranslation.OPTIONS_END.translate());
                options.start.setItemMeta(meta);
            }
        }
        else
        {
            options.start.setType(Material.LIME_CONCRETE);
            ItemMeta meta = options.start.getItemMeta();
            if (meta != null)
            {
                meta.setDisplayName(TITLE_PREFIX + BingoTranslation.OPTIONS_START.translate());
                options.start.setItemMeta(meta);
            }
        }
        if (player.hasPermission("bingo.settings"))
        {
            options.fillOptions(
                    JOIN,
                    LEAVE,
                    KIT,
                    MODE,
                    CARD,
                    options.start,
                    EFFECTS,
                    EXTRA
            );

            if (!gameSession.isRunning())
            {
                options.addOption(VOTE.copyToSlot(8, 0));
            }
        }
        else if (player.hasPermission("bingo.player"))
        {
            options.fillOptions(
                    JOIN_P,
                    LEAVE_P
            );

            if (!gameSession.isRunning())
            {
                options.addOption(VOTE.copyToSlot(4, 2));
            }
        }
        options.open(player);
    }
}
