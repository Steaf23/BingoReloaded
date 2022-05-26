package me.steven.bingoreloaded.gui;

import me.steven.bingoreloaded.BingoGame;
import me.steven.bingoreloaded.BingoReloaded;
import me.steven.bingoreloaded.data.MessageSender;
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

    private final Map<String, InventoryItem> menuItems = new HashMap<>(){{
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

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player)
    {
        if (slotClicked == menuItems.get("join").getSlot())
        {
            game.getTeamManager().openTeamSelector(player, this);
        }
        else if (slotClicked == menuItems.get("leave").getSlot())
        {
            game.playerQuit(player);
        }
        else if (slotClicked == menuItems.get("kit").getSlot())
        {
            KitOptionsUI kitSelector = new KitOptionsUI(this, game);
            kitSelector.open(player);
        }
        else if (slotClicked == menuItems.get("gamemode").getSlot())
        {
            GamemodeOptionsUI gamemodeSelector = new GamemodeOptionsUI(this, game);
            gamemodeSelector.open(player);
        }
        else if (slotClicked == menuItems.get("card").getSlot())
        {
            openCardPicker(player);
        }
        else if (slotClicked == menuItems.get("effects").getSlot())
        {
            EffectOptionsUI effectSelector = new EffectOptionsUI(this, game);
            effectSelector.open(player);
        }
        else if (slotClicked == menuItems.get("start").getSlot())
        {
            game.start();
        }
    }

    public static void open(Player player, BingoGame gameInstance)
    {
        BingoOptionsUI options = new BingoOptionsUI(gameInstance);
        options.fillOptions(new InventoryItem[]{
                options.menuItems.get("join"),
                options.menuItems.get("leave"),
                options.menuItems.get("kit"),
                options.menuItems.get("gamemode"),
                options.menuItems.get("card"),
                options.menuItems.get("start"),
                options.menuItems.get("effects"),
                });
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

        MessageSender.send("game.start.card", List.of(card.getName()));
        game.card = card;
    }
}
