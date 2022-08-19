package io.github.steaf23.bingoreloaded.gui.cards;

import io.github.steaf23.bingoreloaded.BingoGame;
import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoCardsData;
import io.github.steaf23.bingoreloaded.data.BingoSlotsData;
import io.github.steaf23.bingoreloaded.gui.AbstractGUIInventory;
import io.github.steaf23.bingoreloaded.item.*;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BingoCard extends AbstractGUIInventory implements Listener
{
    public CardSize size;
    public List<AbstractCardSlot> cardSlots = new ArrayList<>();

    protected BingoGame game;

    private static final ItemCardSlot DEFAULT_SLOT = new ItemCardSlot(Material.DIRT);

    public BingoCard(CardSize size, BingoGame game)
    {
        super(9 * size.cardSize, "Card Viewer", null);
        this.size = size;
        this.game = game;
        InventoryItem cardInfoItem = new InventoryItem(0, Material.PAPER, "Regular Bingo Card", "First team to complete 1 line wins.", "Lines can span vertically, horizontally", "or vertically.");
        addOption(cardInfoItem);

        BingoReloaded.registerListener(this);
    }

    public void generateCard(String cardName)
    {
        List<AbstractCardSlot> newItems = new ArrayList<>();

        for (String listName : BingoCardsData.getListsOnCard(cardName))
        {
            List<AbstractCardSlot> slots = BingoSlotsData.getAllSlots(listName);
            Collections.shuffle(slots);

            int count = BingoCardsData.getListMax(cardName, listName);
            if (slots.size() <= 0)
            {
                for (int i = 0; i < count; i++)
                {
                    newItems.add(DEFAULT_SLOT.copy());
                }
            }
            else
            {
                for (int i = 0; i < count; i++)
                {
                    slots.get(Math.floorMod(i, slots.size())).item.getAmount();
                    newItems.add(slots.get(Math.floorMod(i, slots.size())));
                }
            }
        }

        while (newItems.size() < size.fullCardSize)
        {
            newItems.add(new ItemCardSlot(Material.DIRT));
        }
        newItems = newItems.subList(0, size.fullCardSize);

        //Lastly, shuffle and cut the list so that it contains exactly enough items
        Collections.shuffle(newItems);
        cardSlots = newItems;
    }

    public void showInventory(HumanEntity player)
    {
        for (int i = 0; i < cardSlots.size(); i++)
        {
            addOption(cardSlots.get(i).item.inSlot(size.getCardInventorySlot(i)));
        }

        open(player);
    }

    public boolean hasBingo(BingoTeam team)
    {
        BingoReloaded.print("Your team (" + team.getColor() + team.getName() + ChatColor.RESET + ") has collected " + getCompleteCount(team) + " items!", team);
        //check for rows and columns
        for (int y = 0; y < size.cardSize; y++)
        {
            boolean completedRow = true;
            boolean completedCol = true;
            for (int x = 0; x < size.cardSize; x++)
            {
                int indexRow = size.cardSize * y + x;
                if (!cardSlots.get(indexRow).isCompletedByTeam(team))
                {
                    completedRow = false;
                }

                int indexCol = size.cardSize * x + y;
                if (!cardSlots.get(indexCol).isCompletedByTeam(team))
                {
                    completedCol = false;
                }
            }

            if (completedRow || completedCol)
            {
                return true;
            }
        }

        // check for diagonals
        boolean completedDiagonal1 = true;
        for (int idx = 0; idx < size.fullCardSize; idx += size.cardSize + 1)
        {
            if (!cardSlots.get(idx).isCompletedByTeam(team))
            {
                completedDiagonal1 = false;
                break;
            }
        }

        boolean completedDiagonal2 = true;
        for (int idx = 0; idx < size.fullCardSize; idx += size.cardSize - 1)
        {
            if (idx != 0 && idx != size.fullCardSize - 1)
            {
                if (!cardSlots.get(idx).isCompletedByTeam(team))
                {
                    completedDiagonal2 = false;
                    break;
                }
            }
        }
        return completedDiagonal1 || completedDiagonal2;
    }

    /**
     * @param team The team.
     * @return The amount of completed items for the given team.
     */
    public int getCompleteCount(BingoTeam team)
    {
        int count = 0;
        for (AbstractCardSlot item : cardSlots)
        {
            if (item.isCompletedByTeam(team)) count++;
        }

        return count;
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {

    }

    public BingoCard copy()
    {
        BingoCard card = new BingoCard(this.size, game);
        List<AbstractCardSlot> newItems = new ArrayList<>();
        for (AbstractCardSlot slot : cardSlots)
        {
            newItems.add(slot.copy());
        }
        card.cardSlots = newItems;
        return card;
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event)
    {
        if (!(event.getWhoClicked() instanceof Player player))
        {
            return;
        }

        BingoTeam team = game.getTeamManager().getTeamOfPlayer(player);
        if (team == null || team.card != this)
            return;

        if (event.getSlotType() == InventoryType.SlotType.RESULT)
        {
            ItemStack item = event.getInventory().getItem(event.getRawSlot());
            if (item == null)
            {
                return;
            }
            item = completeItemSlot(item, team, player);
            event.setCancelled(false);
            return;
        }

        for (ItemStack stack : player.getInventory().getContents())
        {
            if (stack != null)
            {
                stack = completeItemSlot(stack, team, player);
            }
        }
    }

    @EventHandler
    public void onPlayerCollectItem(final EntityPickupItemEvent event)
    {
        if (!(event.getEntity() instanceof Player p))
            return;

        BingoTeam team = game.getTeamManager().getTeamOfPlayer(p);
        if (team == null || team.card != this)
            return;

        ItemStack stack = event.getItem().getItemStack();
        stack = completeItemSlot(stack, team, p);
    }

    @EventHandler
    public void onPlayerDroppedItem(final PlayerDropItemEvent event)
    {
        BingoTeam team = game.getTeamManager().getTeamOfPlayer(event.getPlayer());
        if (team == null || team.card != this)
            return;

        ItemStack stack = event.getItemDrop().getItemStack();
        stack = completeItemSlot(stack, team, event.getPlayer());
    }

    @EventHandler
    public void onPlayerAdvancementDone(final PlayerAdvancementDoneEvent event)
    {
        // Make sure the card of the player is this one!
        BingoTeam team = game.getTeamManager().getTeamOfPlayer(event.getPlayer());
        if (team == null || team.card != this)
            return;

        if (game.getSettings().deathMatchItem != null)
            return;
        for (var slot : cardSlots)
        {
            if (slot instanceof AdvancementCardSlot advSlot && !advSlot.isComplete())
            {
                if (advSlot.advancement.equals(event.getAdvancement()))
                {
                    advSlot.complete(team, game.getGameTime());
                    var slotEvent = new BingoCardSlotCompleteEvent(advSlot, team, event.getPlayer(), hasBingo(team));
                    Bukkit.getPluginManager().callEvent(slotEvent);
                    break;
                }
            }
        }
    }

    ItemStack completeItemSlot(ItemStack item, BingoTeam team, Player player)
    {
        if (game.getSettings().deathMatchItem != null)
        {
            if (item.getType() == game.getSettings().deathMatchItem)
            {
                var slotEvent = new BingoCardSlotCompleteEvent(null, team, player, true);
                Bukkit.getPluginManager().callEvent(slotEvent);
            }
            return item;
        }

        for (AbstractCardSlot slot : cardSlots)
        {
            if (slot instanceof ItemCardSlot itemSlot)
            {
                if (item.getType().equals(itemSlot.item.getType()) && item.getAmount() >= itemSlot.getCount())
                {
                    itemSlot.complete(team, game.getGameTime());
                    item.setAmount(item.getAmount() - itemSlot.getCount());
                    new BukkitRunnable()
                    {
                        @Override
                        public void run()
                        {
                            player.updateInventory();
                        }
                    }.runTask(Bukkit.getPluginManager().getPlugin(BingoReloaded.NAME));
                    var slotEvent = new BingoCardSlotCompleteEvent(itemSlot, team, player, hasBingo(team));
                    Bukkit.getPluginManager().callEvent(slotEvent);
                    break;
                }
            }
        }
        return item;
    }
}
