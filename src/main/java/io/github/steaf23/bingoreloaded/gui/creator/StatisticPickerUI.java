package io.github.steaf23.bingoreloaded.gui.creator;

import io.github.steaf23.bingoreloaded.core.data.TranslationData;
import io.github.steaf23.bingoreloaded.gui.base.MenuInventory;
import io.github.steaf23.bingoreloaded.gui.base.TreeMenu;
import io.github.steaf23.bingoreloaded.gui.base.InventoryItem;
import io.github.steaf23.bingoreloaded.core.tasks.statistics.BingoStatistic;
import io.github.steaf23.bingoreloaded.core.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.core.tasks.StatisticTask;
import io.github.steaf23.bingoreloaded.util.FlexColor;
import io.github.steaf23.bingoreloaded.util.GUIPreset6x9;
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

    protected static final InventoryItem BG_ITEM = new InventoryItem(Material.BLACK_STAINED_GLASS_PANE, " ", "");
    protected static final InventoryItem QUIT = new InventoryItem(49, Material.REDSTONE, "" + ChatColor.RED + ChatColor.BOLD + TranslationData.translate("menu.save_exit"), "");

    public StatisticPickerUI(MenuInventory parent, String listName)
    {
        super("Pick Statistics", parent);
        this.listName = listName;
        addMenuOption(new InventoryItem(GUIPreset6x9.TWELVE.positions[0], Material.FEATHER, TITLE_PREFIX + "Travel"), createTravelMenu());
        addMenuOption(new InventoryItem(GUIPreset6x9.TWELVE.positions[1], Material.DIAMOND_SWORD, TITLE_PREFIX + "Kill"), createEntityMenu(Statistic.KILL_ENTITY));
        addMenuOption(new InventoryItem(GUIPreset6x9.TWELVE.positions[2], Material.SKELETON_SKULL, TITLE_PREFIX + "Get Killed"), createEntityMenu(Statistic.ENTITY_KILLED_BY));
        addMenuOption(new InventoryItem(GUIPreset6x9.TWELVE.positions[3], Material.STONECUTTER, TITLE_PREFIX + "Block Interactions"), createBlockInteractMenu());
        addMenuOption(new InventoryItem(GUIPreset6x9.TWELVE.positions[4], Material.CHEST, TITLE_PREFIX + "Container Interactions"), createContainerMenu());
        addMenuOption(new InventoryItem(GUIPreset6x9.TWELVE.positions[5], Material.DIAMOND_PICKAXE, TITLE_PREFIX + "Mine Block"), createBlockMenu(Statistic.MINE_BLOCK));
        addMenuOption(new InventoryItem(GUIPreset6x9.TWELVE.positions[6], Material.HOPPER, TITLE_PREFIX + "Drop Item"), createItemMenu(Statistic.DROP));
        addMenuOption(new InventoryItem(GUIPreset6x9.TWELVE.positions[7], Material.SHEARS, TITLE_PREFIX + "Use/Place Item"), createItemMenu(Statistic.USE_ITEM));
        addMenuOption(new InventoryItem(GUIPreset6x9.TWELVE.positions[8], Material.DEAD_BUSH, TITLE_PREFIX + "Break Item"), createItemMenu(Statistic.BREAK_ITEM));
        addMenuOption(new InventoryItem(GUIPreset6x9.TWELVE.positions[9], Material.CRAFTING_TABLE, TITLE_PREFIX + "Craft Item"), createItemMenu(Statistic.CRAFT_ITEM));
        addMenuOption(new InventoryItem(GUIPreset6x9.TWELVE.positions[10], Material.REDSTONE, TITLE_PREFIX + "Damage Related"), createDamageMenu());
        addMenuOption(new InventoryItem(GUIPreset6x9.TWELVE.positions[11], Material.BAKED_POTATO, TITLE_PREFIX + "Other"), createMiscMenu());
        fillOptions(BG_ITEM.inSlot(45),
                BG_ITEM.inSlot(46),
                BG_ITEM.inSlot(47),
                BG_ITEM.inSlot(48),
                QUIT,
                BG_ITEM.inSlot(50),
                BG_ITEM.inSlot(51),
                BG_ITEM.inSlot(52),
                BG_ITEM.inSlot(53)
        );
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        if (slotClicked == QUIT.getSlot())
        {
            close(player);
        }

        super.delegateClick(event, slotClicked, player, clickType);
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
        BingoStatistic.getStatisticsOfCategory(BingoStatistic.StatisticCategory.TRAVEL)
                .forEach(stat -> tasks.add(new BingoTask(new StatisticTask(new BingoStatistic(stat))))
                );
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
