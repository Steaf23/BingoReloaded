package io.github.steaf23.bingoreloaded.gui.creator;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.gui.base.MenuItem;
import io.github.steaf23.bingoreloaded.gui.base.MenuInventory;
import io.github.steaf23.bingoreloaded.gui.base.TreeMenu;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.tasks.StatisticTask;
import io.github.steaf23.bingoreloaded.tasks.statistics.BingoStatistic;
import io.github.steaf23.bingoreloaded.util.FlexColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

//TODO: add description to statistic and when to trigger them
public class StatisticPickerUI extends TreeMenu
{
    public String listName;

    protected static final MenuItem BG_ITEM = new MenuItem(Material.BLACK_STAINED_GLASS_PANE, " ", "");
    protected static final MenuItem QUIT = new MenuItem(49, Material.REDSTONE, "" + ChatColor.RED + ChatColor.BOLD + BingoTranslation.MENU_SAVE_EXIT.translate(), "");

    public StatisticPickerUI(MenuInventory parent, String listName)
    {
        super(54, "Pick Statistics", parent);
        this.listName = listName;
        addMenuAction(new MenuItem(1, 0, Material.FEATHER, TITLE_PREFIX + "Travel"), createTravelMenu());
        addMenuAction(new MenuItem(3, 0, Material.DIAMOND_SWORD, TITLE_PREFIX + "Kill"), createEntityMenu(Statistic.KILL_ENTITY));
        addMenuAction(new MenuItem(5, 0, Material.SKELETON_SKULL, TITLE_PREFIX + "Get Killed"), createEntityMenu(Statistic.ENTITY_KILLED_BY));
        addMenuAction(new MenuItem(7, 0, Material.STONECUTTER, TITLE_PREFIX + "Block Interactions"), createBlockInteractMenu());
        addMenuAction(new MenuItem(1, 2, Material.CHEST, TITLE_PREFIX + "Container Interactions"), createContainerMenu());
        addMenuAction(new MenuItem(3, 2, Material.DIAMOND_PICKAXE, TITLE_PREFIX + "Mine Block"), createBlockMenu(Statistic.MINE_BLOCK));
        addMenuAction(new MenuItem(5, 2, Material.HOPPER, TITLE_PREFIX + "Drop Item"), createItemMenu(Statistic.DROP));
        addMenuAction(new MenuItem(7, 2, Material.SHEARS, TITLE_PREFIX + "Use/Place Item"), createItemMenu(Statistic.USE_ITEM));
        addMenuAction(new MenuItem(1, 4, Material.DEAD_BUSH, TITLE_PREFIX + "Break Item"), createItemMenu(Statistic.BREAK_ITEM));
        addMenuAction(new MenuItem(3, 4, Material.CRAFTING_TABLE, TITLE_PREFIX + "Craft Item"), createItemMenu(Statistic.CRAFT_ITEM));
        addMenuAction(new MenuItem(5, 4, Material.REDSTONE, TITLE_PREFIX + "Damage Related"), createDamageMenu());
        addMenuAction(new MenuItem(7, 4, Material.BAKED_POTATO, TITLE_PREFIX + "Other"), createMiscMenu());
        addItems(BG_ITEM.copyToSlot(45),
                BG_ITEM.copyToSlot(46),
                BG_ITEM.copyToSlot(47),
                BG_ITEM.copyToSlot(48),
                QUIT,
                BG_ITEM.copyToSlot(50),
                BG_ITEM.copyToSlot(51),
                BG_ITEM.copyToSlot(52),
                BG_ITEM.copyToSlot(53)
        );
    }

    @Override
    public void onItemClicked(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        if (slotClicked == QUIT.getSlot())
        {
            close(player);
        }

        super.onItemClicked(event, slotClicked, player, clickType);
    }

    public void addStatisticsToSave(@NotNull List<BingoStatistic> statistics)
    {
        statistics.addAll(statistics);
    }

    private TaskPickerUI createEntityMenu(Statistic stat)
    {
        List<EntityType> entities = Arrays.stream(EntityType.values())
                .filter(type -> BingoStatistic.isEntityValidForStatistic(type))
                .toList();

        List<BingoTask> tasks = new ArrayList<>();
        entities.forEach(e -> tasks.add(new BingoTask(new StatisticTask(new BingoStatistic(stat, e)))));

        TaskPickerUI picker = new TaskPickerUI(tasks, "Select Entities", this, listName);
        return picker;
    }

    private TaskPickerUI createBlockMenu(Statistic stat)
    {
        Set<Material> glassPanes = new HashSet<>();
        for (FlexColor flexColor : FlexColor.values())
        {
            glassPanes.add(flexColor.glassPane);
        }

        List<BingoTask> tasks = new ArrayList<>();

        for (Material m : Material.values())
        {
            if (!m.name().contains("LEGACY_") && !glassPanes.contains(m) && m.isBlock() && m.isItem() && !m.isAir())
            {
                tasks.add(new BingoTask(new StatisticTask(new BingoStatistic(stat, m))));
            }
        }
        TaskPickerUI picker = new TaskPickerUI(tasks, "Select Blocks", this, listName);
        return picker;
    }

    private TaskPickerUI createItemMenu(Statistic stat)
    {
        Set<Material> glassPanes = new HashSet<>();
        for (FlexColor flexColor : FlexColor.values())
        {
            glassPanes.add(flexColor.glassPane);
        }

        List<BingoTask> tasks = new ArrayList<>();
        for (Material m : Material.values())
        {
            if (!m.name().contains("LEGACY_") && !glassPanes.contains(m) && m.isItem() && !m.isAir())
            {
                tasks.add(new BingoTask(new StatisticTask(new BingoStatistic(stat, m))));
            }
        }
        TaskPickerUI picker = new TaskPickerUI(tasks, "Select Items", this, listName);
        return picker;
    }

    public TaskPickerUI createTravelMenu()
    {
        List<BingoTask> tasks = new ArrayList<>();
        for (Statistic stat : BingoStatistic.getStatisticsOfCategory(BingoStatistic.StatisticCategory.TRAVEL))
        {
            tasks.add(new BingoTask(new StatisticTask(new BingoStatistic(stat))));
        }
        TaskPickerUI picker = new TaskPickerUI(tasks, "Travel Statistics", this, listName);
        return picker;
    }

    private TaskPickerUI createContainerMenu()
    {
        List<BingoTask> tasks = new ArrayList<>();
        BingoStatistic.getStatisticsOfCategory(BingoStatistic.StatisticCategory.CONTAINER_INTERACT)
                .forEach(stat -> tasks.add(new BingoTask(new StatisticTask(new BingoStatistic(stat))))
                );
        TaskPickerUI picker = new TaskPickerUI(tasks, "Container Statistics", this, listName);
        return picker;
    }

    private TaskPickerUI createBlockInteractMenu()
    {
        List<BingoTask> tasks = new ArrayList<>();
        BingoStatistic.getStatisticsOfCategory(BingoStatistic.StatisticCategory.BLOCK_INTERACT)
                .forEach(stat -> tasks.add(new BingoTask(new StatisticTask(new BingoStatistic(stat))))
                );
        TaskPickerUI picker = new TaskPickerUI(tasks, "Select Blocks", this, listName);
        return picker;
    }

    private TaskPickerUI createDamageMenu()
    {
        List<BingoTask> tasks = new ArrayList<>();
        BingoStatistic.getStatisticsOfCategory(BingoStatistic.StatisticCategory.DAMAGE)
                .forEach(stat -> tasks.add(new BingoTask(new StatisticTask(new BingoStatistic(stat))))
                );
        TaskPickerUI picker = new TaskPickerUI(tasks, "Damage Statistics", this, listName);
        return picker;
    }

    private TaskPickerUI createMiscMenu()
    {
        List<BingoTask> tasks = new ArrayList<>();
        BingoStatistic.getStatisticsOfCategory(BingoStatistic.StatisticCategory.OTHER)
                .forEach(stat ->
                {
                    // Disable certain statistics that wouldn't make sense have in a bingo minigame
                    switch (stat)
                    {
                        case TIME_SINCE_DEATH,
                                TIME_SINCE_REST,
                                TOTAL_WORLD_TIME,
                                LEAVE_GAME -> {}
                        default -> tasks.add(new BingoTask(new StatisticTask(new BingoStatistic(stat))));
                    };
                }
                );
        TaskPickerUI picker = new TaskPickerUI(tasks, "Other Statistics", this, listName);
        return picker;
    }

}
