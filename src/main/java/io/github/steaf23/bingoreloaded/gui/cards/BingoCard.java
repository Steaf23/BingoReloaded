package io.github.steaf23.bingoreloaded.gui.cards;

import io.github.steaf23.bingoreloaded.BingoGame;
import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.Message;
import io.github.steaf23.bingoreloaded.data.BingoCardsData;
import io.github.steaf23.bingoreloaded.data.BingoTasksData;
import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.gui.AbstractGUIInventory;
import io.github.steaf23.bingoreloaded.event.BingoCardSlotCompleteEvent;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.item.tasks.AbstractBingoTask;
import io.github.steaf23.bingoreloaded.item.tasks.AdvancementTask;
import io.github.steaf23.bingoreloaded.item.tasks.ItemTask;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
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

import java.util.*;

public class BingoCard extends AbstractGUIInventory implements Listener
{
    public CardSize size;
    public List<AbstractBingoTask> tasks = new ArrayList<>();

    protected BingoGame game;

    private static final ItemTask DEFAULT_TASK = new ItemTask(Material.DIRT);

    public BingoCard(CardSize size, BingoGame game)
    {
        super(9 * size.cardSize, TranslationData.translate("menu.card.title"), null);
        this.size = size;
        this.game = game;
        InventoryItem cardInfoItem = new InventoryItem(0, Material.MAP, TranslationData.itemName("menu.card.info_regular"), TranslationData.itemDescription("menu.card.info_regular"));
        addOption(cardInfoItem);

        BingoReloaded.registerListener(this);
    }

    public void generateCard(String cardName)
    {
        List<AbstractBingoTask> newItems = new ArrayList<>();

        List<String> ticketList = new ArrayList<>();
        for (String listName : BingoCardsData.getListsSortedByMin(cardName))
        {
            if (BingoTasksData.getTaskCount(listName) <= 0) // Skip empty task lists.
            {
                continue;
            }
            for (int i = 0; i < BingoCardsData.getListMin(cardName, listName); i++)
            {
                ticketList.add(listName);
            }
        }

        List<String> overflowList = new ArrayList<>();
        for (String listName : BingoCardsData.getLists(cardName))
        {
            for (int i = 0; i < BingoCardsData.getListMax(cardName, listName) - BingoCardsData.getListMin(cardName, listName); i++)
            {
                overflowList.add(listName);
            }
        }
        Collections.shuffle(overflowList);
        ticketList.addAll(overflowList);
        if (ticketList.size() > size.fullCardSize)
            ticketList = ticketList.subList(0, size.fullCardSize);

        Map<String, List<AbstractBingoTask>> allTasks = new HashMap<>();
        for (String listName : ticketList)
        {
            if (!allTasks.containsKey(listName))
            {
                List<AbstractBingoTask> listTasks = BingoTasksData.getAllTasks(listName);
                if (listTasks.size() <= 0) // Skip empty task lists.
                {
                    continue;
                }
                Collections.shuffle(listTasks);
                allTasks.put(listName, listTasks);
            }
            newItems.add(allTasks.get(listName).remove(allTasks.get(listName).size() - 1));
        }

        while (newItems.size() < size.fullCardSize)
        {
            newItems.add(DEFAULT_TASK.copy());
        }
        newItems = newItems.subList(0, size.fullCardSize);

        Collections.shuffle(newItems);
        tasks = newItems;
    }

    public void showInventory(HumanEntity player)
    {
        for (int i = 0; i < tasks.size(); i++)
        {
            InventoryItem item = tasks.get(i).item.inSlot(size.getCardInventorySlot(i));
            addOption(item);
        }

        open(player);
    }

    public boolean hasBingo(BingoTeam team)
    {
        //check for rows and columns
        for (int y = 0; y < size.cardSize; y++)
        {
            boolean completedRow = true;
            boolean completedCol = true;
            for (int x = 0; x < size.cardSize; x++)
            {
                int indexRow = size.cardSize * y + x;
                if (!tasks.get(indexRow).isCompletedByTeam(team))
                {
                    completedRow = false;
                }

                int indexCol = size.cardSize * x + y;
                if (!tasks.get(indexCol).isCompletedByTeam(team))
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
            if (!tasks.get(idx).isCompletedByTeam(team))
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
                if (!tasks.get(idx).isCompletedByTeam(team))
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
        for (AbstractBingoTask item : tasks)
        {
            if (item.isCompletedByTeam(team))
                count++;
        }

        return count;
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        for (int i = 0; i < tasks.size(); i++)
        {
            if (size.getCardInventorySlot(i) == slotClicked)
            {
                AbstractBingoTask task = tasks.get(i);
                BaseComponent base = new TextComponent("\n");
                BaseComponent name = task.getDisplayName();
                name.setBold(true);
                BaseComponent desc = task.getDescription();
                desc.setColor(ChatColor.GRAY);
                base.addExtra(name);
                base.addExtra("\n - ");
                base.addExtra(desc);
                Message.sendDebug(base, player);
            }
        }
    }

    public BingoCard copy()
    {
        BingoCard card = new BingoCard(this.size, game);
        List<AbstractBingoTask> newItems = new ArrayList<>();
        for (AbstractBingoTask slot : tasks)
        {
            newItems.add(slot.copy());
        }
        card.tasks = newItems;
        return card;
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event)
    {
        if (!(event.getWhoClicked() instanceof Player player) || !game.inProgress)
            return;

        BingoTeam team = game.getTeamManager().getTeamOfPlayer(player);
        if (team == null || team.card != this)
            return;

        if (event.getSlotType() == InventoryType.SlotType.RESULT && event.getClick() != ClickType.SHIFT_LEFT)
        {
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    ItemStack resultStack = player.getItemOnCursor();
                    if (resultStack != null)
                    {
                        completeItemSlot(resultStack, team, player);
                    }
                }
            }.runTask(Bukkit.getPluginManager().getPlugin(BingoReloaded.NAME));
            return;
        }

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for (ItemStack stack : player.getInventory().getContents())
                {
                    if (stack != null)
                    {
                        stack = completeItemSlot(stack, team, player);
                    }
                }

                ItemStack stack = player.getItemOnCursor();
                stack = completeItemSlot(stack, team, player);
            }
        }.runTask(Bukkit.getPluginManager().getPlugin(BingoReloaded.NAME));
    }

    @EventHandler
    public void onPlayerCollectItem(final EntityPickupItemEvent event)
    {
        if (!(event.getEntity() instanceof Player p) || !game.inProgress)
            return;

        BingoTeam team = game.getTeamManager().getTeamOfPlayer(p);
        if (team == null || team.card != this)
            return;

        ItemStack stack = event.getItem().getItemStack();
        int amount = stack.getAmount();
        stack = completeItemSlot(stack, team, p);
        if (amount != stack.getAmount())
        {
            event.setCancelled(true);
            ItemStack resultStack = stack.clone();
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    p.getWorld().dropItem(event.getItem().getLocation(), resultStack);
                    event.getItem().remove();
                }
            }.runTask(Bukkit.getPluginManager().getPlugin(BingoReloaded.NAME));
        }
    }

    @EventHandler
    public void onPlayerDroppedItem(final PlayerDropItemEvent event)
    {
        if (!game.inProgress)
            return;

        BingoTeam team = game.getTeamManager().getTeamOfPlayer(event.getPlayer());
        if (team == null || team.card != this)
            return;

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                ItemStack stack = event.getItemDrop().getItemStack();
                stack = completeItemSlot(stack, team, event.getPlayer());
            }
        }.runTask(Bukkit.getPluginManager().getPlugin(BingoReloaded.NAME));
    }

    @EventHandler
    public void onPlayerAdvancementDone(final PlayerAdvancementDoneEvent event)
    {
        if (!game.inProgress)
            return;

        // Make sure the card of the player is this one!
        BingoTeam team = game.getTeamManager().getTeamOfPlayer(event.getPlayer());
        if (team == null || team.card != this)
            return;

        if (game.getSettings().deathMatchItem != null)
            return;
        for (var slot : tasks)
        {
            if (slot instanceof AdvancementTask advSlot && !advSlot.isComplete())
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

        for (AbstractBingoTask task : tasks)
        {
            if (task instanceof ItemTask itemTask)
            {
                if (item.getType().equals(itemTask.item.getType()) && item.getAmount() >= itemTask.getCount())
                {
                    if (!itemTask.complete(team, game.getGameTime()))
                    {
                        continue;
                    }
                    item.setAmount(item.getAmount() - itemTask.getCount());
//                    player.updateInventory();
                    var slotEvent = new BingoCardSlotCompleteEvent(itemTask, team, player, hasBingo(team));
                    Bukkit.getPluginManager().callEvent(slotEvent);
                    break;
                }
            }
        }
        return item;
    }
}
