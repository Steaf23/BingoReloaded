package io.github.steaf23.easymenulib.inventory;

import io.github.steaf23.easymenulib.EasyMenuLibrary;
import io.github.steaf23.easymenulib.inventory.item.ItemTemplate;
import io.github.steaf23.easymenulib.inventory.item.action.MenuAction;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
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
import java.util.function.Consumer;

public class BasicMenu implements Menu
{
    public record ActionArguments(HumanEntity player, ClickType clickType) {}

    public static String pluginTitlePrefix = "";

    protected static final String TITLE_PREFIX = "" + ChatColor.GOLD + ChatColor.BOLD;
    protected static final ChatColor[] TITLE_PREFIX_ARRAY = new ChatColor[] {ChatColor.GOLD, ChatColor.BOLD};
    protected static ItemTemplate BLANK = new ItemTemplate(Material.BLACK_STAINED_GLASS_PANE, "")
            .addMetaModifier(meta -> {
                meta.setHideTooltip(true);
                return meta;
            });

    private final Inventory inventory;
    private final MenuBoard manager;
    private int maxStackSizeOverride = -1; // -1 means no override (i.e. default stack sizes for all items)
    private final List<ItemTemplate> items;

    private String title;

    public BasicMenu(MenuBoard manager, String initialTitle, int rows) {
        this(manager, Bukkit.createInventory(null, rows * 9, pluginTitlePrefix + initialTitle));
        this.title = initialTitle;
    }

    public BasicMenu(MenuBoard manager, String initialTitle, InventoryType type) {
        this(manager, Bukkit.createInventory(null, type, pluginTitlePrefix + initialTitle));
        this.title = initialTitle;
    }

    // Used for common setup
    private BasicMenu(MenuBoard manager, Inventory inventory) {
        this.inventory = inventory;
        this.manager = manager;
        this.items = new ArrayList<>();
        this.title = "";
    }

    public void open(HumanEntity player) {
        manager.open(this, player);
    }

    public void open(ActionArguments arguments) {
        manager.open(this, arguments.player);
    }

    public void close(HumanEntity player) {
        manager.close(this, player);
    }

    public void close(ActionArguments arguments) {
        manager.close(this, arguments.player);
    }

    public void reopen(HumanEntity player) {
        Bukkit.getScheduler().runTask(EasyMenuLibrary.getPlugin(), t -> {
            beforeOpening(player);
        });
    }

    public @Nullable ItemTemplate getItemAtSlot(int slot) {
        for (ItemTemplate item : items) {
            if (item.getSlot() == slot)
            {
                return item;
            }
        }
        return null;
    }

    public BasicMenu addItem(@NotNull ItemTemplate item, boolean replaceExisting) {
        if (maxStackSizeOverride != -1)
            inventory.setMaxStackSize(maxStackSizeOverride);

        if (!replaceExisting && inventory.getItem(item.getSlot()) != null) {
            return this;
        }

        items.removeIf(i -> i.getSlot() == item.getSlot());
        items.add(item);

        // Replace/ set new item in its target slot
        inventory.setItem(item.getSlot(), item.buildItem());

        return this;
    }

    public BasicMenu addItem(@NotNull ItemTemplate item) {
        return addItem(item, true);
    }

    public BasicMenu addAction(@NotNull ItemTemplate item, Consumer<ActionArguments> action) {
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

    public BasicMenu addAction(@NotNull ItemTemplate item, MenuAction action) {
        item.setAction(action);
        addItem(item);
        return this;
    }

    public BasicMenu addCloseAction(@NotNull ItemTemplate item) {
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

    public void addItems(@NotNull ItemTemplate... items) {
        for (ItemTemplate item : items) {
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
        for (ItemTemplate item : new ArrayList<ItemTemplate>(items)) {
            if (item.getSlot() == clickedSlot)
            {
                item.useItem(new ActionArguments(player, clickType));
                //TODO: find a way to update itemstack automatically on change, no matter where!
                inventory.setItem(item.getSlot(), item.buildItem());
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

    @Override
    public String toString() {
        return "BasicMenu{" + this.title + "}";
    }
}
