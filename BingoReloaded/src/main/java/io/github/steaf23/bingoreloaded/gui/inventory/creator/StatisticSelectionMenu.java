package io.github.steaf23.bingoreloaded.gui.inventory.creator;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.tasks.StatisticTask;
import io.github.steaf23.bingoreloaded.tasks.statistics.BingoStatistic;
import io.github.steaf23.easymenulib.inventory.BasicMenu;
import io.github.steaf23.easymenulib.inventory.MenuBoard;
import io.github.steaf23.easymenulib.inventory.item.ItemTemplate;
import io.github.steaf23.easymenulib.util.FlexColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

// TODO: add description to statistic and when to trigger them
public class StatisticSelectionMenu extends BasicMenu
{
    public String listName;

    protected static final ItemTemplate BG_ITEM = new ItemTemplate(Material.BLACK_STAINED_GLASS_PANE, " ", "");
    protected static final ItemTemplate QUIT = new ItemTemplate(49, Material.REDSTONE, "" + ChatColor.RED + ChatColor.BOLD + BingoTranslation.MENU_SAVE_EXIT.translate(), "");

    public StatisticSelectionMenu(MenuBoard menuBoard, String listName)
    {
        super(menuBoard, "Pick Statistics", 6);
        this.listName = listName;
        addAction(new ItemTemplate(1, 0, Material.FEATHER, TITLE_PREFIX + "Travel"), p -> createTravelMenu().open(p));
        addAction(new ItemTemplate(3, 0, Material.DIAMOND_SWORD, TITLE_PREFIX + "Kill"), p -> createEntityMenu(Statistic.KILL_ENTITY).open(p));
        addAction(new ItemTemplate(5, 0, Material.SKELETON_SKULL, TITLE_PREFIX + "Get Killed"), p -> createEntityMenu(Statistic.ENTITY_KILLED_BY).open(p));
        addAction(new ItemTemplate(7, 0, Material.STONECUTTER, TITLE_PREFIX + "Block Interactions"), p -> createBlockInteractMenu().open(p));
        addAction(new ItemTemplate(1, 2, Material.CHEST, TITLE_PREFIX + "Container Interactions"), p -> createContainerMenu().open(p));
        addAction(new ItemTemplate(3, 2, Material.DIAMOND_PICKAXE, TITLE_PREFIX + "Mine Block"), p -> createBlockMenu(Statistic.MINE_BLOCK).open(p));
        addAction(new ItemTemplate(5, 2, Material.HOPPER, TITLE_PREFIX + "Drop Item"), p -> createItemMenu(Statistic.DROP).open(p));
        addAction(new ItemTemplate(7, 2, Material.SHEARS, TITLE_PREFIX + "Use/Place Item"), p -> createItemMenu(Statistic.USE_ITEM).open(p));
        addAction(new ItemTemplate(1, 4, Material.DEAD_BUSH, TITLE_PREFIX + "Break Item"), p -> createItemMenu(Statistic.BREAK_ITEM).open(p));
        addAction(new ItemTemplate(3, 4, Material.CRAFTING_TABLE, TITLE_PREFIX + "Craft Item"), p -> createItemMenu(Statistic.CRAFT_ITEM).open(p));
        addAction(new ItemTemplate(5, 4, Material.REDSTONE, TITLE_PREFIX + "Damage Related"), p -> createDamageMenu().open(p));
        addAction(new ItemTemplate(7, 4, Material.BAKED_POTATO, TITLE_PREFIX + "Other"), p -> createMiscMenu().open(p));
        addCloseAction(QUIT);
        addItems(BG_ITEM.copyToSlot(45),
                BG_ITEM.copyToSlot(46),
                BG_ITEM.copyToSlot(47),
                BG_ITEM.copyToSlot(48),
                BG_ITEM.copyToSlot(50),
                BG_ITEM.copyToSlot(51),
                BG_ITEM.copyToSlot(52),
                BG_ITEM.copyToSlot(53)
        );
    }

    public void addStatisticsToSave(@NotNull List<BingoStatistic> statistics)
    {
        statistics.addAll(statistics);
    }

    private TaskPickerMenu createEntityMenu(Statistic stat)
    {
        List<EntityType> entities = Arrays.stream(EntityType.values())
                .filter(type -> BingoStatistic.isEntityValidForStatistic(type))
                .collect(Collectors.toList());

        List<BingoTask> tasks = new ArrayList<>();
        entities.forEach(e -> tasks.add(new BingoTask(new StatisticTask(new BingoStatistic(stat, e)))));

        TaskPickerMenu picker = new TaskPickerMenu(getMenuBoard(), "Select Entities", tasks, listName);
        return picker;
    }

    private TaskPickerMenu createBlockMenu(Statistic stat)
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
        TaskPickerMenu picker = new TaskPickerMenu(getMenuBoard(), "Select Blocks", tasks, listName);
        return picker;
    }

    private TaskPickerMenu createItemMenu(Statistic stat)
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
        TaskPickerMenu picker = new TaskPickerMenu(getMenuBoard(), "Select Items", tasks, listName);
        return picker;
    }

    public TaskPickerMenu createTravelMenu()
    {
        List<BingoTask> tasks = new ArrayList<>();
        for (Statistic stat : BingoStatistic.getStatisticsOfCategory(BingoStatistic.StatisticCategory.TRAVEL))
        {
            tasks.add(new BingoTask(new StatisticTask(new BingoStatistic(stat))));
        }
        TaskPickerMenu picker = new TaskPickerMenu(getMenuBoard(), "Travel Statistics", tasks, listName);
        return picker;
    }

    private TaskPickerMenu createContainerMenu()
    {
        List<BingoTask> tasks = new ArrayList<>();
        BingoStatistic.getStatisticsOfCategory(BingoStatistic.StatisticCategory.CONTAINER_INTERACT)
                .forEach(stat -> tasks.add(new BingoTask(new StatisticTask(new BingoStatistic(stat))))
                );
        TaskPickerMenu picker = new TaskPickerMenu(getMenuBoard(), "Container Statistics", tasks, listName);
        return picker;
    }

    private TaskPickerMenu createBlockInteractMenu()
    {
        List<BingoTask> tasks = new ArrayList<>();
        BingoStatistic.getStatisticsOfCategory(BingoStatistic.StatisticCategory.BLOCK_INTERACT)
                .forEach(stat -> tasks.add(new BingoTask(new StatisticTask(new BingoStatistic(stat))))
                );
        TaskPickerMenu picker = new TaskPickerMenu(getMenuBoard(),  "Select Blocks", tasks, listName);
        return picker;
    }

    private TaskPickerMenu createDamageMenu()
    {
        List<BingoTask> tasks = new ArrayList<>();
        BingoStatistic.getStatisticsOfCategory(BingoStatistic.StatisticCategory.DAMAGE)
                .forEach(stat -> tasks.add(new BingoTask(new StatisticTask(new BingoStatistic(stat))))
                );
        TaskPickerMenu picker = new TaskPickerMenu(getMenuBoard(), "Damage Statistics", tasks, listName);
        return picker;
    }

    private TaskPickerMenu createMiscMenu()
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
        TaskPickerMenu picker = new TaskPickerMenu(getMenuBoard(), "Other Statistics", tasks, listName);
        return picker;
    }

}
