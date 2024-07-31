package io.github.steaf23.bingoreloaded.gui.inventory.creator;

import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.tasks.BingoStatistic;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.bingoreloaded.tasks.StatisticTask;
import io.github.steaf23.playerdisplay.inventory.BasicMenu;
import io.github.steaf23.playerdisplay.inventory.MenuBoard;
import io.github.steaf23.playerdisplay.inventory.item.ItemTemplate;
import io.github.steaf23.playerdisplay.util.BlockColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StatisticSelectionMenu extends BasicMenu
{
    public String listName;
    protected static final ItemTemplate QUIT = new ItemTemplate(49, Material.REDSTONE, BingoMessage.MENU_SAVE_EXIT.asPhrase().color(NamedTextColor.RED).decorate(TextDecoration.BOLD));

    public StatisticSelectionMenu(MenuBoard menuBoard, String listName)
    {
        super(menuBoard, Component.text("Pick Statistics"), 6);
        this.listName = listName;
        addAction(new ItemTemplate(1, 0, Material.FEATHER, BasicMenu.applyTitleFormat("Travel")), args -> createTravelMenu().open(args.player()));
        addAction(new ItemTemplate(3, 0, Material.DIAMOND_SWORD, BasicMenu.applyTitleFormat("Kill")), args -> createEntityMenu(Statistic.KILL_ENTITY).open(args.player()));
        addAction(new ItemTemplate(5, 0, Material.SKELETON_SKULL, BasicMenu.applyTitleFormat("Get Killed")), args -> createEntityMenu(Statistic.ENTITY_KILLED_BY).open(args.player()));
        addAction(new ItemTemplate(7, 0, Material.STONECUTTER, BasicMenu.applyTitleFormat("Block Interactions")), args -> createBlockInteractMenu().open(args.player()));
        addAction(new ItemTemplate(1, 2, Material.CHEST, BasicMenu.applyTitleFormat("Container Interactions")), args -> createContainerMenu().open(args.player()));
        addAction(new ItemTemplate(3, 2, Material.DIAMOND_PICKAXE, BasicMenu.applyTitleFormat("Mine Block")), args -> createBlockMenu(Statistic.MINE_BLOCK).open(args.player()));
        addAction(new ItemTemplate(5, 2, Material.HOPPER, BasicMenu.applyTitleFormat("Drop Item")), args -> createItemMenu(Statistic.DROP).open(args.player()));
        addAction(new ItemTemplate(7, 2, Material.SHEARS, BasicMenu.applyTitleFormat("Use/Place Item")), args -> createItemMenu(Statistic.USE_ITEM).open(args.player()));
        addAction(new ItemTemplate(1, 4, Material.DEAD_BUSH, BasicMenu.applyTitleFormat("Break Item")), args -> createItemMenu(Statistic.BREAK_ITEM).open(args.player()));
        addAction(new ItemTemplate(3, 4, Material.CRAFTING_TABLE, BasicMenu.applyTitleFormat("Craft Item")), args -> createItemMenu(Statistic.CRAFT_ITEM).open(args.player()));
        addAction(new ItemTemplate(5, 4, Material.REDSTONE, BasicMenu.applyTitleFormat("Damage Related")), args -> createDamageMenu().open(args.player()));
        addAction(new ItemTemplate(7, 4, Material.BAKED_POTATO, BasicMenu.applyTitleFormat("Other")), args -> createMiscMenu().open(args.player()));
        addCloseAction(QUIT);
        addItems(BLANK.copyToSlot(45),
                BLANK.copyToSlot(46),
                BLANK.copyToSlot(47),
                BLANK.copyToSlot(48),
                BLANK.copyToSlot(50),
                BLANK.copyToSlot(51),
                BLANK.copyToSlot(52),
                BLANK.copyToSlot(53)
        );
    }

    private TaskPickerMenu createEntityMenu(Statistic stat)
    {
        List<EntityType> entities = Arrays.stream(EntityType.values())
                .filter(BingoStatistic::isEntityValidForStatistic)
                .toList();

        List<GameTask> tasks = new ArrayList<>();
        entities.forEach(e -> tasks.add(new GameTask(new StatisticTask(new BingoStatistic(stat, e)))));

        return new TaskPickerMenu(getMenuBoard(), "Select Entities", tasks, listName);
    }

    private TaskPickerMenu createBlockMenu(Statistic stat)
    {
        Set<Material> glassPanes = new HashSet<>();
        for (BlockColor color : BlockColor.values())
        {
            glassPanes.add(color.glassPane);
        }

        List<GameTask> tasks = new ArrayList<>();

        for (Material m : Material.values())
        {
            if (!m.name().contains("LEGACY_") && !glassPanes.contains(m) && m.isBlock() && m.isItem() && !m.isAir())
            {
                tasks.add(new GameTask(new StatisticTask(new BingoStatistic(stat, m))));
            }
        }
        return new TaskPickerMenu(getMenuBoard(), "Select Blocks", tasks, listName);
    }

    private TaskPickerMenu createItemMenu(Statistic stat)
    {
        Set<Material> glassPanes = new HashSet<>();
        for (BlockColor color : BlockColor.values())
        {
            glassPanes.add(color.glassPane);
        }

        List<GameTask> tasks = new ArrayList<>();
        for (Material m : Material.values())
        {
            if (!m.name().contains("LEGACY_") && !glassPanes.contains(m) && m.isItem() && !m.isAir())
            {
                tasks.add(new GameTask(new StatisticTask(new BingoStatistic(stat, m))));
            }
        }
        return new TaskPickerMenu(getMenuBoard(), "Select Items", tasks, listName);
    }

    public TaskPickerMenu createTravelMenu()
    {
        List<GameTask> tasks = new ArrayList<>();
        for (Statistic stat : BingoStatistic.getStatisticsOfCategory(BingoStatistic.StatisticCategory.TRAVEL))
        {
            tasks.add(new GameTask(new StatisticTask(new BingoStatistic(stat))));
        }
        return new TaskPickerMenu(getMenuBoard(), "Travel Statistics", tasks, listName);
    }

    private TaskPickerMenu createContainerMenu()
    {
        List<GameTask> tasks = new ArrayList<>();
        BingoStatistic.getStatisticsOfCategory(BingoStatistic.StatisticCategory.CONTAINER_INTERACT)
                .forEach(stat -> tasks.add(new GameTask(new StatisticTask(new BingoStatistic(stat))))
                );
        return new TaskPickerMenu(getMenuBoard(), "Container Statistics", tasks, listName);
    }

    private TaskPickerMenu createBlockInteractMenu()
    {
        List<GameTask> tasks = new ArrayList<>();
        BingoStatistic.getStatisticsOfCategory(BingoStatistic.StatisticCategory.BLOCK_INTERACT)
                .forEach(stat -> tasks.add(new GameTask(new StatisticTask(new BingoStatistic(stat))))
                );
        return new TaskPickerMenu(getMenuBoard(),  "Select Blocks", tasks, listName);
    }

    private TaskPickerMenu createDamageMenu()
    {
        List<GameTask> tasks = new ArrayList<>();
        BingoStatistic.getStatisticsOfCategory(BingoStatistic.StatisticCategory.DAMAGE)
                .forEach(stat -> tasks.add(new GameTask(new StatisticTask(new BingoStatistic(stat))))
                );
        return new TaskPickerMenu(getMenuBoard(), "Damage Statistics", tasks, listName);
    }

    private TaskPickerMenu createMiscMenu()
    {
        List<GameTask> tasks = new ArrayList<>();
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
                        default -> tasks.add(new GameTask(new StatisticTask(new BingoStatistic(stat))));
                    }
                });
        return new TaskPickerMenu(getMenuBoard(), "Other Statistics", tasks, listName);
    }

}
