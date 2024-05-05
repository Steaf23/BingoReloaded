package io.github.steaf23.bingoreloaded.gui.base;

import io.github.steaf23.bingoreloaded.gui.base.item.MenuAction;
import io.github.steaf23.bingoreloaded.gui.base.item.MenuItem;
import io.github.steaf23.bingoreloaded.util.ExtraMath;
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
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BasicMenu implements Menu
{
    public record ActionArguments(HumanEntity player, ClickType clickType) {}

    public static String pluginTitlePrefix = "";

    private static int ID_COUNTER = 0;

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

        if (item.getSlot() == -1) {
            inventory.addItem(item.getStack());
        } else {
            // Replace/ set new item in its target slot
            inventory.setItem(item.getSlot(), item.getStack());
        }

        return this;
    }

    public BasicMenu addItem(@NotNull MenuItem item) {
        return addItem(item, true);
    }

    public BasicMenu addAction(@NotNull MenuItem item, Consumer<ActionArguments> action) {
        String actionId = "" + ID_COUNTER;
        ID_COUNTER++;

        item.setAction(new MenuAction()
        {
            @Override
            public void use(MenuItem item, ActionArguments arguments) {
                action.accept(arguments);
            }
        });
        addItem(item);

        return this;
    }

    public BasicMenu addSpinBoxAction(@NotNull MenuItem item, int minValue, int maxValue, int initialValue, BiConsumer<ActionArguments, Integer> action) {
        //TODO: refactor into MenuControl!

        // Ensure min value is never bigger than max value and the value is between 1 and 64
        final int finalMin = ExtraMath.clamped(minValue, 1, maxValue);
        final int finalMax = ExtraMath.clamped(maxValue, minValue, 64);

        int initialAmount = ExtraMath.clamped(initialValue, finalMin, finalMax);
        MenuItem initializedItem = item.setAmount(initialAmount);
        initializedItem.setDescription(Menu.inputButtonText("Left Click") + "increase",
                Menu.inputButtonText("Right Click") + "decrease",
                Menu.inputButtonText("Hold Shift") + "edit faster");

        return addAction(initializedItem, arguments -> {
            ClickType clickType = arguments.clickType;
            int byAmount = 1;
            if (clickType.isShiftClick())
            {
                byAmount = 10;
            }
            if (clickType.isRightClick())
            {
                byAmount *= -1;
            }

            int newAmount = ExtraMath.clamped(initializedItem.getAmount() + byAmount, finalMin, finalMax);
//            updateActionItem(initializedItem.setAmount(newAmount));
            action.accept(arguments, newAmount);
        });
    }

    public BasicMenu addToggleAction(@NotNull MenuItem item, boolean initialValue, BiConsumer<ActionArguments, Boolean> action) {
        //TODO: refactor into MenuControl!

        MenuItem initialItem = item.setGlowing(initialValue);
        initialItem.setDescription(Menu.inputButtonText("Click") + "toggle value");
        return addAction(initialItem, arguments -> {
            boolean newValue = !initialItem.isGlowing();
//            updateActionItem(initialItem.setGlowing(newValue));
            action.accept(arguments, newValue);
        });
    }

    public BasicMenu addCloseAction(@NotNull MenuItem item) {
        return addAction(item, this::close);
    }

    public void addItems(@NotNull MenuItem... items) {
        for (MenuItem item : items) {
            addItem(item);
        }
    }

    public MenuItem getItemAt(int slotIndex) {
        return new MenuItem(slotIndex, getInventory().getItem(slotIndex));
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

    public MenuBoard getMenuManager() {
        return this.manager;
    }

    @Override
    public void beforeOpening(HumanEntity player) {
    }

    /**
     * @param event
     * @param player
     * @param clickedSlot
     * @param clickType
     * @return true if this event should be cancelled
     */
    @Override
    public boolean onClick(final InventoryClickEvent event, HumanEntity player, int clickedSlot, ClickType clickType) {
        for (MenuItem item : items) {
            if (item.getSlot() == clickedSlot)
            {
                item.useItem(new ActionArguments(player, clickType));
                //TODO: find a way to update itemstack automatically on change, no matter where!
                inventory.setItem(item.getSlot(), item.getStack());
            }
        }
        return true;
    }

    /**
     * @param event
     * @return true if this event should be cancelled
     */
    @Override
    public boolean onDrag(final InventoryDragEvent event) {
        return true;
    }

    @Override
    public void beforeClosing(HumanEntity player) {
    }
}
