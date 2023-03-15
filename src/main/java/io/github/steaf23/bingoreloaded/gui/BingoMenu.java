package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.core.BingoGame;
import io.github.steaf23.bingoreloaded.core.BingoSettings;
import io.github.steaf23.bingoreloaded.core.BingoGameManager;
import io.github.steaf23.bingoreloaded.gui.base.FilterType;
import io.github.steaf23.bingoreloaded.gui.base.InventoryItem;
import io.github.steaf23.bingoreloaded.gui.base.MenuInventory;
import io.github.steaf23.bingoreloaded.gui.base.PaginatedPickerMenu;
import io.github.steaf23.bingoreloaded.core.data.BingoCardsData;
import io.github.steaf23.bingoreloaded.util.GUIPreset5x9;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BingoMenu extends MenuInventory
{
    private final BingoGameManager gameManager;

    private final InventoryItem start = new InventoryItem(GUIPreset5x9.SEVEN_CENTER1.positions[3],
            Material.LIME_CONCRETE, TITLE_PREFIX + BingoReloaded.data().translationData.itemName("menu.options.start"));
    private static final InventoryItem JOIN = new InventoryItem(GUIPreset5x9.SEVEN_CENTER1.positions[0],
            Material.WHITE_GLAZED_TERRACOTTA, TITLE_PREFIX + BingoReloaded.data().translationData.itemName("menu.options.team"));
    private static final InventoryItem LEAVE = new InventoryItem(GUIPreset5x9.SEVEN_CENTER1.positions[1],
            Material.BARRIER, TITLE_PREFIX + BingoReloaded.data().translationData.itemName("menu.options.leave"));
    private static final InventoryItem KIT = new InventoryItem(GUIPreset5x9.SEVEN_CENTER1.positions[2],
            Material.IRON_INGOT, TITLE_PREFIX + BingoReloaded.data().translationData.itemName("menu.options.kit"));
    private static final InventoryItem CARD = new InventoryItem(GUIPreset5x9.SEVEN_CENTER1.positions[4],
            Material.MAP, TITLE_PREFIX + BingoReloaded.data().translationData.itemName("menu.options.card"));
    private static final InventoryItem MODE = new InventoryItem(GUIPreset5x9.SEVEN_CENTER1.positions[5],
            Material.ENCHANTED_BOOK, TITLE_PREFIX + BingoReloaded.data().translationData.itemName("menu.options.mode"));
    private static final InventoryItem EFFECTS = new InventoryItem(GUIPreset5x9.SEVEN_CENTER1.positions[6],
            Material.POTION, TITLE_PREFIX + BingoReloaded.data().translationData.itemName("menu.options.effects"));
    private static final InventoryItem EXTRA = new InventoryItem(44,
            Material.STRUCTURE_VOID, TITLE_PREFIX + BingoReloaded.data().translationData.translate("menu.next"));

    private static final InventoryItem JOIN_P = JOIN.inSlot(GUIPreset5x9.TWO_HORIZONTAL_WIDE.positions[0]);
    private static final InventoryItem LEAVE_P = LEAVE.inSlot(GUIPreset5x9.TWO_HORIZONTAL_WIDE.positions[1]);

    private BingoMenu(BingoGameManager gameManager)
    {
        super(45, BingoReloaded.data().translationData.translate("menu.options.title"), null);
        this.gameManager = gameManager;
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        BingoGame game = gameManager.getGame(BingoGameManager.getWorldName(player.getWorld()));
        if (game == null)
            return;

        if (!player.hasPermission("bingo.settings"))
        {
            if (slotClicked == JOIN_P.getSlot())
            {
                game.getTeamManager().openTeamSelector(player, this);
            }
            else if (slotClicked == LEAVE_P.getSlot())
            {
                game.playerQuit(game.getTeamManager().getBingoPlayer(player));
            }
            return;
        }

        BingoSettings settings = gameManager.getGameSettings(game.getWorldName());

        if (slotClicked == JOIN.getSlot())
        {
            game.getTeamManager().openTeamSelector(player, this);
        }
        else if (slotClicked == LEAVE.getSlot())
        {
            game.playerQuit(game.getTeamManager().getBingoPlayer(player));
        }
        else if (slotClicked == KIT.getSlot())
        {
            KitOptionsMenu kitSelector = new KitOptionsMenu(this, game, settings);
            kitSelector.open(player);
        }
        else if (slotClicked == MODE.getSlot())
        {
            GamemodeOptionsMenu gamemodeSelector = new GamemodeOptionsMenu(this, game, settings);
            gamemodeSelector.open(player);
        }
        else if (slotClicked == CARD.getSlot())
        {
            openCardPicker(player, BingoReloaded.data().cardsData);
        }
        else if (slotClicked == EFFECTS.getSlot())
        {
            EffectOptionsMenu effectSelector = new EffectOptionsMenu(this, settings);
            effectSelector.open(player);
        }
        else if (slotClicked == EXTRA.getSlot())
        {
            ExtraBingoMenu extraOptions = new ExtraBingoMenu(this, settings);
            extraOptions.open(player);
        }
        else if (slotClicked == start.getSlot())
        {
            if (game.isInProgress())
            {
                gameManager.endGame(game.getWorldName());
                start.setType(Material.LIME_CONCRETE);
                ItemMeta meta = start.getItemMeta();
                if (meta != null)
                {
                    meta.setDisplayName(TITLE_PREFIX + BingoReloaded.data().translationData.itemName("menu.options.start"));
                    start.setItemMeta(meta);
                }
                addOption(start);
            }
            else
            {
                gameManager.startGame(BingoGameManager.getWorldName(player.getWorld()));
            }
        }
    }

    public static void openOptions(Player player, BingoGameManager gameManager)
    {
        BingoGame game = gameManager.getActiveGame(BingoGameManager.getWorldName(player.getWorld()));
        if (game == null)
            return;

        BingoMenu options = new BingoMenu(gameManager);
        if (game.isInProgress())
        {
            options.start.setType(Material.RED_CONCRETE);
            ItemMeta meta = options.start.getItemMeta();
            if (meta != null)
            {
                meta.setDisplayName(TITLE_PREFIX + BingoReloaded.data().translationData.itemName("menu.options.end"));
                options.start.setItemMeta(meta);
            }
        }
        else
        {
            options.start.setType(Material.LIME_CONCRETE);
            ItemMeta meta = options.start.getItemMeta();
            if (meta != null)
            {
                meta.setDisplayName(TITLE_PREFIX + BingoReloaded.data().translationData.itemName("menu.options.start"));
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
        }
        else if (player.hasPermission("bingo.player"))
        {
            options.fillOptions(
                    JOIN_P.inSlot(20),
                    LEAVE_P.inSlot(24)
            );
        }
        options.open(player);
    }

    private void openCardPicker(Player player, BingoCardsData cardsData)
    {
        List<InventoryItem> cards = new ArrayList<>();

        for (String cardName : cardsData.getCardNames())
        {
            cards.add(new InventoryItem(Material.PAPER, cardName,
                    ChatColor.DARK_PURPLE + BingoReloaded.data().translationData.translate("creator.card_item.desc",
                            "" + cardsData.getListNames(cardName).size())));
        }

        PaginatedPickerMenu cardPicker = new PaginatedPickerMenu(cards, BingoReloaded.data().translationData.itemName("menu.options.card"),this, FilterType.DISPLAY_NAME)
        {
            @Override
            public void onOptionClickedDelegate(InventoryClickEvent event, InventoryItem clickedOption, Player player)
            {
                ItemMeta meta = clickedOption.getItemMeta();
                if (meta != null)
                {
                    cardSelected(meta.getDisplayName(), BingoGameManager.getWorldName(player.getWorld()));
                }
                close(player);
            }
        };
        cardPicker.open(player);
    }

    private void cardSelected(String cardName, String worldName)
    {
        if (cardName == null) return;
        new TranslatedMessage("game.settings.card_selected").color(ChatColor.GOLD).arg(cardName).sendAll(gameManager.getGame(worldName));
        gameManager.getGameSettings(worldName).card = cardName;
    }
}
