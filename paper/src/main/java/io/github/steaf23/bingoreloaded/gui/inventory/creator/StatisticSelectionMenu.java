package io.github.steaf23.bingoreloaded.gui.inventory.creator;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.lib.api.EntityType;
import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.StatisticHandle;
import io.github.steaf23.bingoreloaded.lib.api.StatisticHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.StatisticType;
import io.github.steaf23.bingoreloaded.lib.api.StatisticTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.bingoreloaded.tasks.data.StatisticTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Statistic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StatisticSelectionMenu extends BasicMenu
{
    public String listName;
    protected static final ItemTemplate QUIT = new ItemTemplate(49, ItemTypePaper.of(Material.REDSTONE), BingoMessage.MENU_SAVE_EXIT.asPhrase().color(NamedTextColor.RED).decorate(TextDecoration.BOLD));

    public StatisticSelectionMenu(MenuBoard menuBoard, String listName)
    {
        super(menuBoard, Component.text("Pick Statistics"), 6);
        this.listName = listName;
        addAction(new ItemTemplate(1, 0, ItemTypePaper.of(Material.LEATHER_BOOTS), BingoReloaded.applyTitleFormat("Travel")), args -> createTravelMenu().open(args.player()));
        addAction(new ItemTemplate(3, 0, ItemTypePaper.of(Material.DIAMOND_SWORD), BingoReloaded.applyTitleFormat("Kill")), args -> createEntityMenu(Statistic.KILL_ENTITY).open(args.player()));
        addAction(new ItemTemplate(5, 0, ItemTypePaper.of(Material.SKELETON_SKULL), BingoReloaded.applyTitleFormat("Get Killed")), args -> createEntityMenu(Statistic.ENTITY_KILLED_BY).open(args.player()));
        addAction(new ItemTemplate(7, 0, ItemTypePaper.of(Material.STONECUTTER), BingoReloaded.applyTitleFormat("Block Interactions")), args -> createBlockInteractMenu().open(args.player()));
        addAction(new ItemTemplate(1, 2, ItemTypePaper.of(Material.CHEST), BingoReloaded.applyTitleFormat("Container Interactions")), args -> createContainerMenu().open(args.player()));
        addAction(new ItemTemplate(3, 2, ItemTypePaper.of(Material.DIAMOND_PICKAXE), BingoReloaded.applyTitleFormat("Mine Block")), args -> createBlockMenu(Statistic.MINE_BLOCK).open(args.player()));
        addAction(new ItemTemplate(5, 2, ItemTypePaper.of(Material.HOPPER), BingoReloaded.applyTitleFormat("Drop Item")), args -> createItemMenu(Statistic.DROP).open(args.player()));
        addAction(new ItemTemplate(7, 2, ItemTypePaper.of(Material.SHEARS), BingoReloaded.applyTitleFormat("Use/Place Item")), args -> createItemMenu(Statistic.USE_ITEM).open(args.player()));
        addAction(new ItemTemplate(1, 4, ItemTypePaper.of(Material.DEAD_BUSH), BingoReloaded.applyTitleFormat("Break Item")), args -> createItemMenu(Statistic.BREAK_ITEM).open(args.player()));
        addAction(new ItemTemplate(3, 4, ItemTypePaper.of(Material.CRAFTING_TABLE), BingoReloaded.applyTitleFormat("Craft Item")), args -> createItemMenu(Statistic.CRAFT_ITEM).open(args.player()));
        addAction(new ItemTemplate(5, 4, ItemTypePaper.of(Material.REDSTONE), BingoReloaded.applyTitleFormat("Damage Related")), args -> createDamageMenu().open(args.player()));
        addAction(new ItemTemplate(7, 4, ItemTypePaper.of(Material.BAKED_POTATO), BingoReloaded.applyTitleFormat("Other")), args -> createMiscMenu().open(args.player()));
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
        Set<EntityType> entities = StatisticHandle.getValidEntityTypes();

        List<GameTask> tasks = new ArrayList<>();
        entities.forEach(e -> tasks.add(new GameTask(new StatisticTask(new StatisticHandlePaper(new StatisticTypePaper(stat), e)))));

        return new TaskPickerMenu(getMenuBoard(), "Select Entities", tasks, listName);
    }

    private TaskPickerMenu createBlockMenu(Statistic stat)
    {
        List<GameTask> tasks = new ArrayList<>();

        for (Material m : Material.values())
        {
            if (!m.name().contains("LEGACY_") && m.isBlock() && m.isItem() && !m.isAir())
            {
                tasks.add(new GameTask(new StatisticTask(new StatisticHandlePaper(stat, m))));
            }
        }
        return new TaskPickerMenu(getMenuBoard(), "Select Blocks", tasks, listName);
    }

    private TaskPickerMenu createItemMenu(Statistic stat)
    {
        List<GameTask> tasks = new ArrayList<>();
        for (Material m : Material.values())
        {
            if (!m.name().contains("LEGACY_") && m.isItem() && !m.isAir())
            {
                tasks.add(new GameTask(new StatisticTask(new StatisticHandlePaper(stat, m))));
            }
        }
        return new TaskPickerMenu(getMenuBoard(), "Select Items", tasks, listName);
    }

    public TaskPickerMenu createTravelMenu()
    {
        List<GameTask> tasks = new ArrayList<>();
        TRAVEL_STATS.forEach(type -> {
            tasks.add(new GameTask(new StatisticTask(new StatisticHandlePaper(type))));
        });
        return new TaskPickerMenu(getMenuBoard(), "Travel Statistics", tasks, listName);
    }

    private TaskPickerMenu createContainerMenu()
    {
        List<GameTask> tasks = new ArrayList<>();
        CONTAINER_INTERACT_STATS.forEach(type -> {
            tasks.add(new GameTask(new StatisticTask(new StatisticHandlePaper(type))));
        });
        return new TaskPickerMenu(getMenuBoard(), "Container Statistics", tasks, listName);
    }

    private TaskPickerMenu createBlockInteractMenu()
    {
        List<GameTask> tasks = new ArrayList<>();
        BLOCK_INTERACT_STATS.forEach(type -> {
            tasks.add(new GameTask(new StatisticTask(new StatisticHandlePaper(type))));
        });
        return new TaskPickerMenu(getMenuBoard(),  "Select Blocks", tasks, listName);
    }

    private TaskPickerMenu createDamageMenu()
    {
        List<GameTask> tasks = new ArrayList<>();
        DAMAGE_STATS.forEach(type -> {
            tasks.add(new GameTask(new StatisticTask(new StatisticHandlePaper(type))));
        });
        return new TaskPickerMenu(getMenuBoard(), "Damage Statistics", tasks, listName);
    }

    private TaskPickerMenu createMiscMenu()
    {
        List<GameTask> tasks = new ArrayList<>();
        OTHER_STATS.forEach(type -> {
                    // Disable certain statistics that wouldn't make sense have in a bingo minigame
                    switch (type.handle())
                    {
                        case TIME_SINCE_DEATH,
                                TIME_SINCE_REST,
                                TOTAL_WORLD_TIME,
                                LEAVE_GAME -> {}
                        default -> tasks.add(new GameTask(new StatisticTask(new StatisticHandlePaper(type))));
                    }
                });
        return new TaskPickerMenu(getMenuBoard(), "Other Statistics", tasks, listName);
    }

    private static final Set<StatisticTypePaper> TRAVEL_STATS = getStatisticsInCategory(StatisticType.StatisticCategory.TRAVEL);
    private static final Set<StatisticTypePaper> DAMAGE_STATS = getStatisticsInCategory(StatisticType.StatisticCategory.DAMAGE);
    private static final Set<StatisticTypePaper> BLOCK_INTERACT_STATS = getStatisticsInCategory(StatisticType.StatisticCategory.BLOCK_INTERACT);
    private static final Set<StatisticTypePaper> CONTAINER_INTERACT_STATS = getStatisticsInCategory(StatisticType.StatisticCategory.CONTAINER_INTERACT);
    private static final Set<StatisticTypePaper> OTHER_STATS = getStatisticsInCategory(StatisticType.StatisticCategory.OTHER);

    private static Set<StatisticTypePaper> getStatisticsInCategory(StatisticType.StatisticCategory category) {
        return StatisticTypePaper.STATISTIC_CATEGORY_MAP.keySet().stream()
                .filter(s -> StatisticTypePaper.STATISTIC_CATEGORY_MAP.get(s).equals(category))
                .map(StatisticTypePaper::new)
                .collect(Collectors.toSet());
    }

}
