package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.BingoGame;
import io.github.steaf23.bingoreloaded.BingoSettings;
import io.github.steaf23.bingoreloaded.GameWorldManager;
import io.github.steaf23.bingoreloaded.Message;
import io.github.steaf23.bingoreloaded.data.BingoCardsData;
import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.util.GUIPreset5x9;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BingoOptionsUI extends AbstractGUIInventory
{
    private final InventoryItem start = new InventoryItem(GUIPreset5x9.SEVEN_CENTER1.positions[3],
            Material.LIME_CONCRETE, TITLE_PREFIX + TranslationData.itemName("menu.options.start"));
    private static final InventoryItem JOIN = new InventoryItem(GUIPreset5x9.SEVEN_CENTER1.positions[0],
            Material.WHITE_GLAZED_TERRACOTTA, TITLE_PREFIX + TranslationData.itemName("menu.options.team"));
    private static final InventoryItem LEAVE = new InventoryItem(GUIPreset5x9.SEVEN_CENTER1.positions[1],
            Material.BARRIER, TITLE_PREFIX + TranslationData.itemName("menu.options.leave"));
    private static final InventoryItem KIT = new InventoryItem(GUIPreset5x9.SEVEN_CENTER1.positions[2],
            Material.IRON_INGOT, TITLE_PREFIX + TranslationData.itemName("menu.options.kit"));
    private static final InventoryItem CARD = new InventoryItem(GUIPreset5x9.SEVEN_CENTER1.positions[4],
            Material.MAP, TITLE_PREFIX + TranslationData.itemName("menu.options.card"));
    private static final InventoryItem MODE = new InventoryItem(GUIPreset5x9.SEVEN_CENTER1.positions[5],
            Material.ENCHANTED_BOOK, TITLE_PREFIX + TranslationData.itemName("menu.options.mode"));
    private static final InventoryItem EFFECTS = new InventoryItem(GUIPreset5x9.SEVEN_CENTER1.positions[6],
            Material.POTION, TITLE_PREFIX + TranslationData.itemName("menu.options.effects"));
    private static final InventoryItem EXTRA = new InventoryItem(44,
            Material.STRUCTURE_VOID, TITLE_PREFIX + TranslationData.translate("menu.next"));

    private static final InventoryItem JOIN_P = JOIN.inSlot(GUIPreset5x9.TWO_HORIZONTAL_WIDE.positions[0]);
    private static final InventoryItem LEAVE_P = LEAVE.inSlot(GUIPreset5x9.TWO_HORIZONTAL_WIDE.positions[1]);

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        String worldName = GameWorldManager.getWorldName(player.getWorld());

        if (!player.hasPermission("bingo.settings"))
        {
            BingoGame game = GameWorldManager.get().getGame(worldName);

            if (slotClicked == JOIN_P.getSlot())
            {
                game.getTeamManager().openTeamSelector(player, this);
            }
            else if (slotClicked == LEAVE_P.getSlot())
            {
                game.playerQuit(player);
            }
            return;
        }

        BingoSettings settings = GameWorldManager.get().getGameSettings(worldName);

        if (slotClicked == JOIN.getSlot())
        {
            BingoGame game = GameWorldManager.get().getGame(worldName);
            game.getTeamManager().openTeamSelector(player, this);
        }
        else if (slotClicked == LEAVE.getSlot())
        {
            BingoGame game = GameWorldManager.get().getGame(worldName);
            game.playerQuit(player);
        }
        else if (slotClicked == KIT.getSlot())
        {
            KitOptionsUI kitSelector = new KitOptionsUI(this, settings);
            kitSelector.open(player);
        }
        else if (slotClicked == MODE.getSlot())
        {
            GamemodeOptionsUI gamemodeSelector = new GamemodeOptionsUI(this, settings);
            gamemodeSelector.open(player);
        }
        else if (slotClicked == CARD.getSlot())
        {
            openCardPicker(player);
        }
        else if (slotClicked == EFFECTS.getSlot())
        {
            EffectOptionsUI effectSelector = new EffectOptionsUI(this, settings);
            effectSelector.open(player);
        }
        else if (slotClicked == EXTRA.getSlot())
        {
             BingoOptionsExtraUI extraOptions = new BingoOptionsExtraUI(this, settings);
             extraOptions.open(player);
        }
        else if (slotClicked == start.getSlot())
        {
            if (GameWorldManager.get().isGameWorldActive(player.getWorld()))
            {
                GameWorldManager.get().endGame(GameWorldManager.getWorldName(player.getWorld()));
                start.setType(Material.LIME_CONCRETE);
                ItemMeta meta = start.getItemMeta();
                if (meta != null)
                {
                    meta.setDisplayName(TITLE_PREFIX + TranslationData.itemName("menu.options.start"));
                    start.setItemMeta(meta);
                }
                addOption(start);
            }
            else
            {
                GameWorldManager.get().startGame(GameWorldManager.getWorldName(player.getWorld()));
            }
        }
    }

    public static void openOptions(Player player)
    {
        BingoOptionsUI options = new BingoOptionsUI();
        if (GameWorldManager.get().isGameWorldActive(player.getWorld()))
        {
            options.start.setType(Material.RED_CONCRETE);
            ItemMeta meta = options.start.getItemMeta();
            if (meta != null)
            {
                meta.setDisplayName(TITLE_PREFIX + TranslationData.itemName("menu.options.end"));
                options.start.setItemMeta(meta);
            }
        }
        else
        {
            options.start.setType(Material.LIME_CONCRETE);
            ItemMeta meta = options.start.getItemMeta();
            if (meta != null)
            {
                meta.setDisplayName(TITLE_PREFIX + TranslationData.itemName("menu.options.start"));
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

    private BingoOptionsUI()
    {
        super(45, TranslationData.translate("menu.options.title"), null);
    }

    private void openCardPicker(Player player)
    {
        List<InventoryItem> cards = new ArrayList<>();

        for (String cardName : BingoCardsData.getCardNames())
        {
            cards.add(new InventoryItem(Material.PAPER, cardName,
                    ChatColor.DARK_PURPLE + TranslationData.translate("creator.card_item.desc",
                            "" + BingoCardsData.getLists(cardName).size())));
        }

        PaginatedPickerUI cardPicker = new PaginatedPickerUI(cards, TranslationData.itemName("menu.options.card"),this, FilterType.DISPLAY_NAME)
        {
            @Override
            public void onOptionClickedDelegate(InventoryClickEvent event, InventoryItem clickedOption, Player player)
            {
                ItemMeta meta = clickedOption.getItemMeta();
                if (meta != null)
                {
                    cardSelected(meta.getDisplayName(), GameWorldManager.getWorldName(player.getWorld()));
                }
                close(player);
            }
        };
        cardPicker.open(player);
    }

    private void cardSelected(String cardName, String worldName)
    {
        if (cardName == null) return;
        new Message("game.settings.card_selected").color(ChatColor.GOLD).arg(cardName).sendAll();
        GameWorldManager.get().getGameSettings(worldName).card = cardName;
    }
}
