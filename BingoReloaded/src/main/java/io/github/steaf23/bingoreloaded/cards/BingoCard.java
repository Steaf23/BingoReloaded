package io.github.steaf23.bingoreloaded.cards;


import io.github.steaf23.bingoreloaded.gameloop.BingoGame;
import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.data.TaskListData;
import io.github.steaf23.bingoreloaded.event.BingoCardTaskCompleteEvent;
import io.github.steaf23.bingoreloaded.event.BingoStatisticCompletedEvent;
import io.github.steaf23.bingoreloaded.gui.CardMenu;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.tasks.*;
import io.github.steaf23.bingoreloaded.tasks.statistics.BingoStatistic;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class BingoCard
{
    public final CardSize size;
    public List<BingoTask> tasks;

    protected final CardMenu menu;

    private static final TaskData DEFAULT_TASK = new ItemTask(Material.DIRT, 1);

    public BingoCard(CardSize size)
    {
        this.size = size;
        this.tasks = new ArrayList<>();
        this.menu = new CardMenu(size, BingoTranslation.CARD_TITLE.translate());
        menu.setInfo(BingoTranslation.INFO_REGULAR_NAME.translate(),
                BingoTranslation.INFO_REGULAR_DESC.translate().split("\\n"));
    }

    public BingoCard(CardSize size, List<BingoTask> tasks) {
        this.size = size;
        this.tasks = tasks;
        this.menu = new CardMenu(size, BingoTranslation.CARD_TITLE.translate());
        menu.setInfo(BingoTranslation.INFO_REGULAR_NAME.translate(),
                BingoTranslation.INFO_REGULAR_DESC.translate().split("\\n"));
    }

    /**
     * Generating a bingo card has a few steps:
     *  - Create task shuffler
     *  - Create a ticketlist. This list contains a list name for each task on the card,
     *      based on how often an item from that list should appear on the card.
     *  - Using the ticketlist, pick a random task from each ticketlist entry to put on the card.
     *  - Finally shuffle the tasks and add them to the card.
     *      If the final task count is lower than the amount of spaces available on the card, it will be filled up using default tasks.
     * @param cardName
     * @param seed
     */
    public void generateCard(String cardName, int seed, boolean withAdvancements, boolean withStatistics)
    {
        BingoCardData cardsData = new BingoCardData();
        TaskListData listsData = cardsData.lists();
        // Create shuffler
        Random shuffler;
        if (seed == 0)
        {
            shuffler = new Random();
        }
        else
        {
            shuffler = new Random(seed);
        }

        // Create ticketList
        List<String> ticketList = new ArrayList<>();
        for (String listName : cardsData.getListsSortedByMin(cardName))
        {
            if (listsData.getTasks(listName, withStatistics, withAdvancements).size() == 0) // Skip empty task lists.
            {
                continue;
            }
            for (int i = 0; i < cardsData.getListMin(cardName, listName); i++)
            {
                ticketList.add(listName);
            }
        }
        List<String> overflowList = new ArrayList<>();
        for (String listName : cardsData.getListNames(cardName))
        {
            for (int i = 0; i < cardsData.getListMax(cardName, listName) - cardsData.getListMin(cardName, listName); i++)
            {
                overflowList.add(listName);
            }
        }
        Collections.shuffle(overflowList, shuffler);
        ticketList.addAll(overflowList);
        if (ticketList.size() > size.fullCardSize)
            ticketList = ticketList.subList(0, size.fullCardSize);

        // Pick random tasks
        List<TaskData> newTasks = new ArrayList<>();
        Map<String, List<TaskData>> allTasks = new HashMap<>();
        for (String listName : ticketList)
        {
            if (!allTasks.containsKey(listName))
            {
                List<TaskData> listTasks = new ArrayList<>(listsData.getTasks(listName, withStatistics, withAdvancements));
                if (listTasks.size() == 0) // Skip empty task lists.
                {
                    continue;
                }
                Collections.shuffle(listTasks, shuffler);
                allTasks.put(listName, listTasks);
            }
            newTasks.add(allTasks.get(listName).remove(allTasks.get(listName).size() - 1));
        }
        while (newTasks.size() < size.fullCardSize)
        {
            newTasks.add(DEFAULT_TASK);
        }
        newTasks = newTasks.subList(0, size.fullCardSize);

        // Shuffle and add tasks to the card.
        Collections.shuffle(newTasks, shuffler);
        newTasks.forEach(item ->
                tasks.add(new BingoTask(item))
        );
    }

    public void showInventory(Player player)
    {
        menu.show(player, tasks);
    }

    public boolean hasBingo(BingoTeam team)
    {
        //check for rows and columns
        for (int y = 0; y < size.size; y++)
        {
            boolean completedRow = true;
            boolean completedCol = true;
            for (int x = 0; x < size.size; x++)
            {
                int indexRow = size.size * y + x;
                Optional<BingoParticipant> completedBy = tasks.get(indexRow).completedBy;
                if (completedBy.isEmpty() || !team.getMembers().contains(completedBy.get()))
                {
                    completedRow = false;
                }

                int indexCol = size.size * x + y;
                completedBy = tasks.get(indexCol).completedBy;
                if (completedBy.isEmpty() || !team.getMembers().contains(completedBy.get()))
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
        for (int idx = 0; idx < size.fullCardSize; idx += size.size + 1)
        {
            Optional<BingoParticipant> completedBy = tasks.get(idx).completedBy;
            if (completedBy.isEmpty() || !team.getMembers().contains(completedBy.get()))
            {
                completedDiagonal1 = false;
                break;
            }
        }

        boolean completedDiagonal2 = true;
        for (int idx = 0; idx < size.fullCardSize; idx += size.size - 1)
        {
            if (idx != 0 && idx != size.fullCardSize - 1)
            {
                Optional<BingoParticipant> completedBy = tasks.get(idx).completedBy;
                if (completedBy.isEmpty() || !team.getMembers().contains(completedBy.get()))
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
        for (var task : tasks)
        {
            if (task.completedBy.isPresent() && team.getMembers().contains(task.completedBy.get()))
                count++;
        }

        return count;
    }

    public BingoCard copy()
    {
        BingoCard card = new BingoCard(this.size);
        List<BingoTask> newTasks = new ArrayList<>();
        for (BingoTask slot : tasks)
        {
            newTasks.add(slot.copy());
        }
        card.tasks = newTasks;
        return card;
    }

    public void onInventoryClick(final InventoryClickEvent event, final BingoPlayer player, final BingoGame game)
    {
        Player p = player.sessionPlayer().get();

        if (event.getSlotType() == InventoryType.SlotType.RESULT && event.getClick() != ClickType.SHIFT_LEFT)
        {
            BingoReloaded.scheduleTask(task -> {
                ItemStack resultStack = p.getItemOnCursor();
                completeItemSlot(resultStack, player, game);
            });
            return;
        }

        BingoReloaded.scheduleTask(task -> {
            for (ItemStack stack : p.getInventory().getContents())
            {
                if (stack != null)
                {
                    stack = completeItemSlot(stack, player, game);
                }
            }

            ItemStack stack = p.getItemOnCursor();
            stack = completeItemSlot(stack, player, game);
        });
    }

    public void onPlayerCollectItem(final EntityPickupItemEvent event, final BingoPlayer player, final BingoGame game)
    {
        if (player.getTeam().outOfTheGame)
            return;

        ItemStack stack = event.getItem().getItemStack();
        int amount = stack.getAmount();
        stack = completeItemSlot(stack, player, game);
        if (amount != stack.getAmount())
        {
            event.setCancelled(true);
            ItemStack resultStack = stack.clone();

            BingoReloaded.scheduleTask(task -> {
                player.sessionPlayer().get().getWorld().dropItem(event.getItem().getLocation(), resultStack);
                event.getItem().remove();
            });
        }
    }

    public void onPlayerDroppedItem(final PlayerDropItemEvent event, final BingoPlayer player, final BingoGame game)
    {
        if (player.getTeam().outOfTheGame)
            return;

        BingoReloaded.scheduleTask(task -> {
            ItemStack stack = event.getItemDrop().getItemStack();
            stack = completeItemSlot(stack, player, game);
        });
    }

    ItemStack completeItemSlot(ItemStack item, BingoPlayer player, BingoGame game)
    {
        if (player.sessionPlayer().isEmpty())
            return item;

        BingoTask deathMatchTask = game.getDeathMatchTask();
        if (deathMatchTask != null)
        {
            if (item.getType().equals(deathMatchTask.material))
            {
                var slotEvent = new BingoCardTaskCompleteEvent(deathMatchTask, player, true);
                Bukkit.getPluginManager().callEvent(slotEvent);
            }
            return item;
        }

        for (BingoTask task : tasks)
        {
            if (task.type != BingoTask.TaskType.ITEM)
                continue;

            ItemTask data = (ItemTask)task.data;
            if (data.material().equals(item.getType()) && data.count() <= item.getAmount())
            {
                if (!task.complete(player, game.getGameTime()))
                {
                    continue;
                }
                item.setAmount(item.getAmount() - data.getCount());
                player.sessionPlayer().get().updateInventory();
                var slotEvent = new BingoCardTaskCompleteEvent(task, player, hasBingo(player.getTeam()));
                Bukkit.getPluginManager().callEvent(slotEvent);
                break;
            }
        }
        return item;
    }

    public void onPlayerAdvancementDone(final PlayerAdvancementDoneEvent event, final BingoPlayer player, final BingoGame game)
    {
        if (player.getTeam().outOfTheGame)
            return;

        if (game.getDeathMatchTask() != null)
            return;

        for (BingoTask task : tasks)
        {
            if (task.type != BingoTask.TaskType.ADVANCEMENT)
                continue;

            AdvancementTask data = (AdvancementTask) task.data;

            if (data.advancement().equals(event.getAdvancement()))
            {
                if (!task.complete(player, game.getGameTime()))
                    continue;

                var slotEvent = new BingoCardTaskCompleteEvent(task, player, hasBingo(player.getTeam()));
                Bukkit.getPluginManager().callEvent(slotEvent);
                break;
            }
        }
    }

    public void onPlayerStatIncrement(final PlayerStatisticIncrementEvent event, final BingoPlayer player, final BingoGame game)
    {

        if (player.getTeam().outOfTheGame)
            return;

        if (game.getDeathMatchTask() != null)
            return;

        for (BingoTask task : tasks)
        {
            if (task.type != BingoTask.TaskType.STATISTIC)
                continue;

            StatisticTask data = (StatisticTask)task.data;
            if (data.statistic().equals(new BingoStatistic(event.getStatistic(), event.getEntityType(), event.getMaterial())) &&
                data.getCount() == event.getNewValue())
            {
                if (!task.complete(player, game.getGameTime()))
                    continue;

                var slotEvent = new BingoCardTaskCompleteEvent(task, player, hasBingo(player.getTeam()));
                Bukkit.getPluginManager().callEvent(slotEvent);
                break;
            }
        }
    }

    public void onPlayerStatisticCompleted(final BingoStatisticCompletedEvent event, final BingoPlayer player, final BingoGame game)
    {
        if (player.getTeam().outOfTheGame)
            return;

        if (game.getDeathMatchTask() != null)
            return;

        for (BingoTask task : tasks)
        {
            if (task.type != BingoTask.TaskType.STATISTIC)
                continue;

            StatisticTask data = (StatisticTask)task.data;
            if (data.statistic().equals(event.stat))
            {
                if (!task.complete(player, game.getGameTime()))
                    continue;

                var slotEvent = new BingoCardTaskCompleteEvent(task, player, hasBingo(player.getTeam()));
                Bukkit.getPluginManager().callEvent(slotEvent);
                break;
            }
        }
    }
}
