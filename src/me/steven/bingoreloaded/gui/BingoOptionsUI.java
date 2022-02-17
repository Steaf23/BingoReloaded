package me.steven.bingoreloaded.gui;

import me.steven.bingoreloaded.BingoGame;
import me.steven.bingoreloaded.BingoReloaded;
import me.steven.bingoreloaded.item.InventoryItem;
import me.steven.bingoreloaded.data.BingoCardData;
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
        put("join", new InventoryItem(12, Material.WHITE_GLAZED_TERRACOTTA, "" + ChatColor.GOLD + ChatColor.BOLD + "Join A Team"));
        put("leave", new InventoryItem(14, Material.BARRIER, "" + ChatColor.GOLD + ChatColor.BOLD + "Quit Bingo"));
        put("kit", new InventoryItem(29, Material.IRON_INGOT, "" + ChatColor.GOLD + ChatColor.BOLD + "Change Starting Kit"));
        put("gamemode", new InventoryItem(31, Material.ENCHANTED_BOOK, "" + ChatColor.GOLD + ChatColor.BOLD + "Change Gamemode"));
        put("card", new InventoryItem(33, Material.MAP, "" + ChatColor.GOLD + ChatColor.BOLD + "Change Bingo Card"));
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
    }

    public static void open(Player player, BingoGame gameInstance)
    {
        BingoOptionsUI options = new BingoOptionsUI(gameInstance);

        if (player.hasPermission("bingo.admin"))
        {
            options.fillOptions(new InventoryItem[]{
                    options.menuItems.get("join"),
                    options.menuItems.get("leave"),
                    options.menuItems.get("kit"),
                    options.menuItems.get("gamemode"),
                    options.menuItems.get("card"),
            });
        }
        else
        {
            //TODO: fix dynamic slot locations
            options.fillOptions(new InventoryItem[]{
                    options.menuItems.get("join"),
                    options.menuItems.get("leave"),
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

        for (String cardName : BingoCardData.getCardNames())
        {
            cards.add(new InventoryItem(Material.PAPER, cardName,
                    ChatColor.DARK_PURPLE + "Contains " +
                            BingoCardData.getOrCreateCard(cardName).getItemLists().size() + " item List(s)"));
        }

        ItemPickerUI cardPicker = new ItemPickerUI(cards, "Pick a card",this)
        {
            @Override
            public void onOptionClickedDelegate(InventoryClickEvent event, InventoryItem clickedOption, Player player)
            {
                ItemMeta meta = clickedOption.getItemMeta();
                if (meta != null)
                {
                    cardSelected(BingoCardData.getOrCreateCard(meta.getDisplayName()), player);
                }
                close(player);
            }
        };
        cardPicker.open(player);
    }

    private void cardSelected(CardEntry card, Player player)
    {
        if (card == null) return;

        BingoReloaded.print("'" + card.name + "' selected as the playing Bingo card!", player);
        game.card = card;
    }
}
