package io.github.steaf23.bingoreloaded.gui.inventory.creator;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.lib.api.EntityType;
import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.VanillaItems;
import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformRegistries;
import io.github.steaf23.bingoreloaded.lib.api.statistics.StatisticHandle;
import io.github.steaf23.bingoreloaded.lib.api.statistics.VanillaStatistic;
import io.github.steaf23.bingoreloaded.lib.api.statistics.VanillaStatistics;
import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.bingoreloaded.tasks.data.StatisticTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StatisticSelectionMenu extends BasicMenu
{
    public String listName;
    protected static final ItemTemplate QUIT = new ItemTemplate(49, VanillaItems.REDSTONE.type(), BingoMessage.MENU_SAVE_EXIT.asPhrase().color(NamedTextColor.RED).decorate(TextDecoration.BOLD));

    PlatformRegistries registries = PlatformResolver.getRegistries();

    public StatisticSelectionMenu(MenuBoard menuBoard, String listName)
    {
        super(menuBoard, Component.text("Pick Statistics"), 6);
        this.listName = listName;
        addAction(new ItemTemplate(1, 0, VanillaItems.LEATHER_BOOTS.type(), BingoReloaded.applyTitleFormat("Travel")), args -> createTravelMenu().open(args.player()));
        addAction(new ItemTemplate(3, 0, VanillaItems.DIAMOND_SWORD.type(), BingoReloaded.applyTitleFormat("Kill")), args -> createEntityMenu(VanillaStatistics.KILL_ENTITY).open(args.player()));
        addAction(new ItemTemplate(5, 0, VanillaItems.SKELETON_SKULL.type(), BingoReloaded.applyTitleFormat("Get Killed")), args -> createEntityMenu(VanillaStatistics.ENTITY_KILLED_BY).open(args.player()));
        addAction(new ItemTemplate(7, 0, VanillaItems.STONECUTTER.type(), BingoReloaded.applyTitleFormat("Block Interactions")), args -> createBlockInteractMenu().open(args.player()));
        addAction(new ItemTemplate(1, 2, VanillaItems.CHEST.type(), BingoReloaded.applyTitleFormat("Container Interactions")), args -> createContainerMenu().open(args.player()));
        addAction(new ItemTemplate(3, 2, VanillaItems.DIAMOND_PICKAXE.type(), BingoReloaded.applyTitleFormat("Mine Block")), args -> createBlockMenu(VanillaStatistics.MINE_BLOCK).open(args.player()));
        addAction(new ItemTemplate(5, 2, VanillaItems.HOPPER.type(), BingoReloaded.applyTitleFormat("Drop Item")), args -> createItemMenu(VanillaStatistics.DROP).open(args.player()));
        addAction(new ItemTemplate(7, 2, VanillaItems.SHEARS.type(), BingoReloaded.applyTitleFormat("Use/Place Item")), args -> createItemMenu(VanillaStatistics.USE_ITEM).open(args.player()));
        addAction(new ItemTemplate(1, 4, VanillaItems.DEAD_BUSH.type(), BingoReloaded.applyTitleFormat("Break Item")), args -> createItemMenu(VanillaStatistics.BREAK_ITEM).open(args.player()));
        addAction(new ItemTemplate(3, 4, VanillaItems.CRAFTING_TABLE.type(), BingoReloaded.applyTitleFormat("Craft Item")), args -> createItemMenu(VanillaStatistics.CRAFT_ITEM).open(args.player()));
        addAction(new ItemTemplate(5, 4, VanillaItems.REDSTONE.type(), BingoReloaded.applyTitleFormat("Damage Related")), args -> createDamageMenu().open(args.player()));
        addAction(new ItemTemplate(7, 4, VanillaItems.BAKED_POTATO.type(), BingoReloaded.applyTitleFormat("Other")), args -> createMiscMenu().open(args.player()));
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

    private TaskPickerMenu createEntityMenu(VanillaStatistic stat)
    {
        Set<EntityType> entities = StatisticHandle.getValidEntityTypes(getMenuBoard().context().runtime());

        List<GameTask> tasks = new ArrayList<>();
        entities.forEach(e -> tasks.add(new GameTask(new StatisticTask(new StatisticHandle(stat, e)))));

        return new TaskPickerMenu(getMenuBoard(), "Select Entities", tasks, listName);
    }

    private TaskPickerMenu createBlockMenu(VanillaStatistic stat)
    {
        List<GameTask> tasks = new ArrayList<>();

        for (ItemType m : registries.allItems())
        {
            if (m.isBlock() && !m.isAir())
            {
                tasks.add(new GameTask(new StatisticTask(new StatisticHandle(stat, m))));
            }
        }
        return new TaskPickerMenu(getMenuBoard(), "Select Blocks", tasks, listName);
    }

    private TaskPickerMenu createItemMenu(VanillaStatistic stat)
    {
        List<GameTask> tasks = new ArrayList<>();
        for (ItemType m : registries.allItems())
        {
            if (!m.isAir())
            {
                tasks.add(new GameTask(new StatisticTask(new StatisticHandle(stat, m))));
            }
        }
        return new TaskPickerMenu(getMenuBoard(), "Select Items", tasks, listName);
    }

    public TaskPickerMenu createTravelMenu()
    {
        List<GameTask> tasks = new ArrayList<>();
        VanillaStatistics.STATISTICS_BY_CATEGORY.getOrDefault(VanillaStatistic.Category.TRAVEL, List.of()).forEach(type -> {
            tasks.add(new GameTask(new StatisticTask(new StatisticHandle(type))));
        });
        return new TaskPickerMenu(getMenuBoard(), "Travel Statistics", tasks, listName);
    }

    private TaskPickerMenu createContainerMenu()
    {
        List<GameTask> tasks = new ArrayList<>();
        VanillaStatistics.STATISTICS_BY_CATEGORY.getOrDefault(VanillaStatistic.Category.CONTAINER_INTERACT, List.of()).forEach(type -> {
            tasks.add(new GameTask(new StatisticTask(new StatisticHandle(type))));
        });
        return new TaskPickerMenu(getMenuBoard(), "Container Statistics", tasks, listName);
    }

    private TaskPickerMenu createBlockInteractMenu()
    {
        List<GameTask> tasks = new ArrayList<>();
        VanillaStatistics.STATISTICS_BY_CATEGORY.getOrDefault(VanillaStatistic.Category.BLOCK_INTERACT, List.of()).forEach(type -> {
            tasks.add(new GameTask(new StatisticTask(new StatisticHandle(type))));
        });
        return new TaskPickerMenu(getMenuBoard(),  "Select Blocks", tasks, listName);
    }

    private TaskPickerMenu createDamageMenu()
    {
        List<GameTask> tasks = new ArrayList<>();
        VanillaStatistics.STATISTICS_BY_CATEGORY.getOrDefault(VanillaStatistic.Category.DAMAGE, List.of()).forEach(type -> {
            tasks.add(new GameTask(new StatisticTask(new StatisticHandle(type))));
        });
        return new TaskPickerMenu(getMenuBoard(), "Damage Statistics", tasks, listName);
    }

    private TaskPickerMenu createMiscMenu() {
        List<GameTask> tasks = new ArrayList<>();
        VanillaStatistics.STATISTICS_BY_CATEGORY.getOrDefault(VanillaStatistic.Category.OTHER, List.of()).stream()
                .filter(t ->
                        t != VanillaStatistics.TIME_SINCE_DEATH &&
                        t != VanillaStatistics.TIME_SINCE_REST &&
                        t != VanillaStatistics.TOTAL_WORLD_TIME &&
                        t != VanillaStatistics.LEAVE_GAME)
                .forEach(type -> tasks.add(new GameTask(new StatisticTask(new StatisticHandle(type)))));

        return new TaskPickerMenu(getMenuBoard(), "Other Statistics", tasks, listName);
    }
}
