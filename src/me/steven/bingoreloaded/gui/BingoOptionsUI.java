package me.steven.bingoreloaded.gui;

import me.steven.bingoreloaded.BingoGame;
import me.steven.bingoreloaded.BingoReloaded;
import me.steven.bingoreloaded.item.InventoryItem;
import me.steven.bingoreloaded.data.BingoCardsData;
import me.steven.bingoreloaded.cardcreator.CardEntry;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BingoOptionsUI extends AbstractGUIInventory
{
    private final BingoGame game;

    private final Map<String, InventoryItem> settingsMenuItems = new HashMap<>(){{
        put("join", new InventoryItem(GUIBuilder5x9.OptionPositions.SEVEN_CENTER1.positions[0],
                Material.WHITE_GLAZED_TERRACOTTA, TITLE_PREFIX + "Join A Team"));
        put("leave", new InventoryItem(GUIBuilder5x9.OptionPositions.SEVEN_CENTER1.positions[1],
                Material.BARRIER, TITLE_PREFIX + "Quit Bingo"));
        put("kit", new InventoryItem(GUIBuilder5x9.OptionPositions.SEVEN_CENTER1.positions[2],
                Material.IRON_INGOT, TITLE_PREFIX + "Change Starting Kit"));
        put("start", new InventoryItem(GUIBuilder5x9.OptionPositions.SEVEN_CENTER1.positions[3],
                Material.LIME_CONCRETE, TITLE_PREFIX + "Start The Game"));
        put("card", new InventoryItem(GUIBuilder5x9.OptionPositions.SEVEN_CENTER1.positions[4],
                Material.MAP, TITLE_PREFIX + "Change Bingo Card"));
        put("gamemode", new InventoryItem(GUIBuilder5x9.OptionPositions.SEVEN_CENTER1.positions[5],
                Material.ENCHANTED_BOOK, TITLE_PREFIX + "Change Gamemode"));
        put("effects", new InventoryItem(GUIBuilder5x9.OptionPositions.SEVEN_CENTER1.positions[6],
                Material.POTION, TITLE_PREFIX + "Change Player Effects"));
    }};

    private final Map<String, InventoryItem> playerMenuItems = new HashMap<>(){{
        put("join", new InventoryItem(GUIBuilder5x9.OptionPositions.TWO_HORIZONTAL_WIDE.positions[0],
                Material.WHITE_GLAZED_TERRACOTTA, TITLE_PREFIX + "Join A Team"));
        put("leave", new InventoryItem(GUIBuilder5x9.OptionPositions.TWO_HORIZONTAL_WIDE.positions[1],
                Material.BARRIER, TITLE_PREFIX + "Quit Bingo"));
    }};

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player)
    {
        if (!player.hasPermission("bingo.settings"))
        {
            if (slotClicked == playerMenuItems.get("join").getSlot())
            {
                game.getTeamManager().openTeamSelector(player, this);
            }
            else if (slotClicked == playerMenuItems.get("leave").getSlot())
            {
                game.playerQuit(player);
            }
            return;
        }


        if (slotClicked == settingsMenuItems.get("join").getSlot())
        {
            game.getTeamManager().openTeamSelector(player, this);
        }
        else if (slotClicked == settingsMenuItems.get("leave").getSlot())
        {
            game.playerQuit(player);
        }
        else if (slotClicked == settingsMenuItems.get("kit").getSlot())
        {
            KitOptionsUI kitSelector = new KitOptionsUI(this, game);
            kitSelector.open(player);
        }
        else if (slotClicked == settingsMenuItems.get("gamemode").getSlot())
        {
            GamemodeOptionsUI gamemodeSelector = new GamemodeOptionsUI(this, game);
            gamemodeSelector.open(player);
        }
        else if (slotClicked == settingsMenuItems.get("card").getSlot())
        {
            openCardPicker(player);
        }
        else if (slotClicked == settingsMenuItems.get("effects").getSlot())
        {
            EffectOptionsUI effectSelector = new EffectOptionsUI(this, game);
            effectSelector.open(player);
        }
        else if (slotClicked == settingsMenuItems.get("start").getSlot())
        {
            game.start();
        }
    }

    public static void open(Player player, BingoGame gameInstance)
    {
        BingoOptionsUI options = new BingoOptionsUI(gameInstance);
        if (player.hasPermission("bingo.settings"))
        {
            options.fillOptions(new InventoryItem[]{
                    options.settingsMenuItems.get("join"),
                    options.settingsMenuItems.get("leave"),
                    options.settingsMenuItems.get("kit"),
                    options.settingsMenuItems.get("gamemode"),
                    options.settingsMenuItems.get("card"),
                    options.settingsMenuItems.get("start"),
                    options.settingsMenuItems.get("effects"),
            });
        }
        else if (player.hasPermission("bingo.player"))
        {
            options.fillOptions(new InventoryItem[]{
                    options.playerMenuItems.get("join").inSlot(20),
                    options.playerMenuItems.get("leave").inSlot(24),
            });
        }
        options.open(player);
    }

    private BingoOptionsUI(BingoGame game)
    {
        super(45, "Options Menu", null);
        this.game = game;
    }

    private void openCardPicker(Player player)
    {
        List<InventoryItem> cards = new ArrayList<>();

        for (String cardName : BingoCardsData.getCardNames())
        {
            cards.add(new InventoryItem(Material.PAPER, cardName,
                    ChatColor.DARK_PURPLE + "Contains " +
                            BingoCardsData.getOrCreateCard(cardName).getItemLists().size() + " item List(s)"));
        }

        ItemPickerUI cardPicker = new ItemPickerUI(cards, "Pick a card",this)
        {
            @Override
            public void onOptionClickedDelegate(InventoryClickEvent event, InventoryItem clickedOption, Player player)
            {
                ItemMeta meta = clickedOption.getItemMeta();
                if (meta != null)
                {
                    cardSelected(BingoCardsData.getOrCreateCard(meta.getDisplayName()), player);
                }
                close(player);
            }
        };
        cardPicker.open(player);
    }

    private void cardSelected(CardEntry card, Player player)
    {
        if (card == null) return;

        BingoReloaded.print("'" + card.getName() + "' selected as the playing Bingo card!", player);
        game.card = card;
    }
}
