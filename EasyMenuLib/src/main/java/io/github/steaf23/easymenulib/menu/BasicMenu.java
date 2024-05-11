package io.github.steaf23.easymenulib.menu;

import io.github.steaf23.easymenulib.EasyMenuLibrary;
import io.github.steaf23.easymenulib.menu.item.MenuItem;
import io.github.steaf23.easymenulib.menu.item.action.MenuAction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class BasicMenu implements Menu
{
    public record ActionArguments(HumanEntity player, ClickType clickType) {}

    public static String pluginTitlePrefix = "";

    protected static final String TITLE_PREFIX = "" + ChatColor.GOLD + ChatColor.BOLD;
    protected static MenuItem BLANK = new MenuItem(Material.BLACK_STAINED_GLASS_PANE, " ");

    private final Inventory inventory;
    private final MenuBoard manager;
    private int maxStackSizeOverride = -1; // -1 means no override (i.e. default stack sizes for all items)
    private final List<MenuItem> items;


    public BasicMenu(MenuBoard manager, String initialTitle, int rows) {
        this(manager, Bukkit.createInventory(null, rows * 9, pluginTitlePrefix + initialTitle));
    }

    public BasicMenu(MenuBoard manager, String initialTitle, InventoryType type) {
        this(manager, Bukkit.createInventory(null, type, pluginTitlePrefix + initialTitle));
    }

    // Used for common setup
    private BasicMenu(MenuBoard manager, Inventory inventory) {
        this.inventory = inventory;
        this.manager = manager;
        this.items = new ArrayList<>();
    }

    public final void open(HumanEntity player) {
        manager.open(this, player);
    }

    public final void open(ActionArguments arguments) {
        manager.open(this, arguments.player);
    }

    public final void close(HumanEntity player) {
        manager.close(this, player);
    }

    public final void close(ActionArguments arguments) {
        manager.close(this, arguments.player);
    }

    public final void reopen(HumanEntity player) {
        Bukkit.getScheduler().runTask(EasyMenuLibrary.getPlugin(), t -> {
            beforeOpening(player);
        });
    }

    public @Nullable MenuItem getItemAtSlot(int slot) {
        for (MenuItem item : items) {
            if (item.getSlot() == slot)
            {
                return item;
            }
        }
        return null;
    }

    public BasicMenu addItem(@NotNull MenuItem item, boolean replaceExisting) {
        if (maxStackSizeOverride != -1)
            inventory.setMaxStackSize(maxStackSizeOverride);

        if (!replaceExisting && inventory.getItem(item.getSlot()) != null) {
            return this;
        }

        items.removeIf(i -> i.getSlot() == item.getSlot());
        items.add(item);

        // Replace/ set new item in its target slot
        inventory.setItem(item.getSlot(), item.buildStack());

        return this;
    }

    public BasicMenu addItem(@NotNull MenuItem item) {
        return addItem(item, true);
    }

    public BasicMenu addAction(@NotNull MenuItem item, Consumer<ActionArguments> action) {
        item.setAction(new MenuAction()
        {
            @Override
            public void use(ActionArguments arguments) {
                action.accept(arguments);
            }
        });
        addItem(item);

        return this;
    }

    public BasicMenu addAction(@NotNull MenuItem item, MenuAction action) {
        item.setAction(action);
        addItem(item);
        return this;
    }

    public BasicMenu addCloseAction(@NotNull MenuItem item) {
        item.setAction(new MenuAction()
        {
            @Override
            public void use(ActionArguments arguments) {
                close(arguments);
            }
        });
        addItem(item);

        return this;
    }

    public void addItems(@NotNull MenuItem... items) {
        for (MenuItem item : items) {
            addItem(item);
        }
    }

    public BasicMenu removeItem(int slotIdx) {
        inventory.setItem(slotIdx, null);
        return this;
    }

    protected void setMaxStackSizeOverride(int maxValue) {
        maxStackSizeOverride = Math.min(64, Math.max(1, maxValue));
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    public MenuBoard getMenuBoard() {
        return this.manager;
    }

    @Override
    public void beforeOpening(HumanEntity player) {
    }

    @Override
    public boolean onClick(final InventoryClickEvent event, HumanEntity player, int clickedSlot, ClickType clickType) {
        for (MenuItem item : new ArrayList<MenuItem>(items)) {
            if (item.getSlot() == clickedSlot)
            {
                item.useItem(new ActionArguments(player, clickType));
                //TODO: find a way to update itemstack automatically on change, no matter where!
                inventory.setItem(item.getSlot(), item.buildStack());
            }
        }
        return true;
    }

    @Override
    public boolean onDrag(final InventoryDragEvent event) {
        return true;
    }

    @Override
    public void beforeClosing(HumanEntity player) {
    }
}
