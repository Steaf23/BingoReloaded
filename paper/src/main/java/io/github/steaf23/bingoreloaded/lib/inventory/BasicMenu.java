package io.github.steaf23.bingoreloaded.lib.inventory;

import io.github.steaf23.bingoreloaded.lib.api.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.lib.inventory.action.MenuAction;
import io.github.steaf23.bingoreloaded.lib.inventory.action.MenuItemGroup;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class BasicMenu implements InventoryMenu
{
    public static Component pluginTitlePrefix = Component.empty();

    public static ItemTemplate BLANK = new ItemTemplate(ItemType.of("black_stained_glass_pane"), null)
            .addMetaModifier(meta -> {
                meta.setHideTooltip(true);
                return meta;
            });

    private final Inventory inventory;
    private final MenuBoard manager;
    private int maxStackSizeOverride = -1; // -1 means no override (i.e. default stack sizes for all items)
    private final MenuItemGroup itemGroup;

    private Component title;
    private boolean openOnce;

    public BasicMenu(MenuBoard manager, Component initialTitle, int rows) {
        this(manager, Bukkit.createInventory(null, rows * 9, Component.text().append(pluginTitlePrefix).append(initialTitle).build()));
        this.title = initialTitle;
    }

    /**
     * Useful for textured menus, sets title as component string without prefix, to put custom fonts in the title.
     */
    public BasicMenu(MenuBoard manager, Component initialTitle, boolean usePrefix) {
        this(manager, Bukkit.createInventory(null, 6 * 9, usePrefix ? Component.text().append(pluginTitlePrefix).append(initialTitle).build() : initialTitle));
        this.title = Component.empty();
    }

    public BasicMenu(MenuBoard manager, Component initialTitle, InventoryType type) {
        this(manager, Bukkit.createInventory(null, type, Component.text().append(pluginTitlePrefix).append(initialTitle).build()));
        this.title = initialTitle;
    }

    // Used for common setup
    private BasicMenu(MenuBoard manager, Inventory inventory) {
        this.inventory = inventory;
        this.manager = manager;
        this.itemGroup = new MenuItemGroup();
    }

    public void open(PlayerHandle player) {
        manager.open(this, player);
    }

    public void open(PlayerHandle player, boolean openOnce) {
        setOpenOnce(openOnce);
        manager.open(this, player);
    }

    public void close(PlayerHandle player) {
        manager.close(this, player);
    }

    public void reopen(PlayerHandle player) {
        Bukkit.getScheduler().runTask(PlayerDisplay.getPlugin(), t -> beforeOpening(player));
    }

    public @Nullable ItemTemplate getItemAtSlot(int slot) {
        for (ItemTemplate item : itemGroup.items) {
            if (item.getSlot() == slot) {
                return item;
            }
        }
        return null;
    }

    public BasicMenu addItem(@NotNull ItemTemplate item, boolean replaceExisting) {
        if (maxStackSizeOverride != -1)
            getInventory().setMaxStackSize(maxStackSizeOverride);

        if (!replaceExisting && getInventory().getItem(item.getSlot()) != null) {
            return this;
        }

        itemGroup.addItem(item);
        // Replace/ set new item in its target slot
        getInventory().setItem(item.getSlot(), item.buildItem());

        return this;
    }

    public BasicMenu addItem(@NotNull ItemTemplate item) {
        return addItem(item, true);
    }

    public BasicMenu addAction(@NotNull ItemTemplate item, Consumer<MenuAction.ActionArguments> action) {
        MenuAction menuAction = new MenuAction()
        {
            @Override
            public void use(ActionArguments arguments) {
                action.accept(arguments);
            }
        };
        item.setAction(menuAction);
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
                close(arguments.player());
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
        getInventory().setItem(slotIdx, null);
        return this;
    }

    protected void setMaxStackSizeOverride(int maxValue) {
        maxStackSizeOverride = Math.min(64, Math.max(1, maxValue));
    }

    public MenuBoard getMenuBoard() {
        return this.manager;
    }

    @Override
    public void beforeOpening(PlayerHandle player) {
    }

    @Override
    public boolean onClick(final InventoryClickEvent event, PlayerHandle player, int clickedSlot, ClickType clickType) {
        return itemGroup.handleClick(this, event, player, clickedSlot, clickType);
    }

    @Override
    public boolean onDrag(final InventoryDragEvent event) {
        return true;
    }

    @Override
    public void onCustomAction(Key key, DataStorage payload) {
    }

    @Override
    public void beforeClosing(PlayerHandle player) {
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    public String toString() {
        return "BasicMenu{" + this.title + "}";
    }

    @Override
    public void setOpenOnce(boolean value) {
        openOnce = value;
    }

    @Override
    public boolean openOnce() {
        return openOnce;
    }
}
