package io.github.steaf23.bingoreloaded.lib.inventory;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.lib.api.inventory.InventoryTemplate;
import io.github.steaf23.bingoreloaded.lib.api.item.VanillaItems;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.inventory.action.MenuAction;
import io.github.steaf23.bingoreloaded.lib.inventory.action.MenuItemGroup;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class BasicMenu implements Menu
{
    public static Component pluginTitlePrefix = Component.empty();

    public static final ItemTemplate BLANK = new ItemTemplate(VanillaItems.BLACK_STAINED_GLASS_PANE.type(), null)
            .setNoTooltip(true);

    private final InventoryTemplate inventory;
    private final MenuType type;
    private final MenuBoard manager;
    private final MenuItemGroup itemGroup;

    private Component title;
    private boolean openOnce;

    public BasicMenu(MenuBoard manager, Component initialTitle, int rows) {
        this(manager, new InventoryTemplate(rows * 9), Component.text().append(pluginTitlePrefix).append(initialTitle).build(), MenuType.CHEST);
    }

    /**
     * Useful for textured menus, sets title as component string without prefix, to put custom fonts in the title.
     */
    public BasicMenu(MenuBoard manager, Component initialTitle, boolean usePrefix) {
        this(manager, new InventoryTemplate(6 * 9), usePrefix ? Component.text().append(pluginTitlePrefix).append(initialTitle).build() : initialTitle, MenuType.CHEST);
    }

    public BasicMenu(MenuBoard manager, Component initialTitle, MenuType menuType) {
        this(manager, new InventoryTemplate(slotCountForMenu(menuType)), Component.text().append(pluginTitlePrefix).append(initialTitle).build(), menuType);
    }

    // Used for common setup
    private BasicMenu(MenuBoard manager, InventoryTemplate inventory, Component title, MenuType type) {
        this.inventory = inventory;
        this.manager = manager;
        this.itemGroup = new MenuItemGroup();
        this.title = title;
        this.type = type;
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
        getMenuBoard().context().taskScheduler().runTask(t -> beforeOpening(player));
    }

    public @Nullable ItemTemplate getItemAtSlot(int slot) {
        return itemGroup.getItem(slot);
    }

    public BasicMenu addItem(@NotNull ItemTemplate item, @Nullable MenuAction action, boolean replaceExisting) {
        if (!replaceExisting && this.getBackedInventory().getItem(item.getSlot()).type().isAir()) {
            return this;
        }

        itemGroup.addItem(item, action);
        // Replace/ set new item in its target slot
        this.getBackedInventory().setItem(item.getSlot(), item.buildItem());

        return this;
    }

    public BasicMenu addItem(@NotNull ItemTemplate item) {
        return addItem(item, null, true);
    }

    public BasicMenu addItem(@NotNull ItemTemplate item, @Nullable MenuAction action) {
        return addItem(item, action, true);
    }

    public BasicMenu addAction(MenuAction action) {
        if (action.item() == null) {
            ConsoleMessenger.bug("Cannot add action as it does not contain any item!", this);
            return this;
        }
        addItem(action.item(), action);
        return this;
    }

    public MenuAction addAction(@NotNull ItemTemplate item, Consumer<MenuAction.ActionArguments> action) {
        MenuAction menuAction = new MenuAction()
        {
            @Override
            public void use(ActionArguments arguments) {
                action.accept(arguments);
            }
        };
        addItem(item, menuAction);

        return menuAction;
    }

    public BasicMenu addExitAction(int slot) {
        return addCloseAction(new ItemTemplate(slot, VanillaItems.REDSTONE.type(), BingoReloaded.applyTitleFormat(BingoMessage.MENU_EXIT.asPhrase())));
    }

    public BasicMenu addSaveAction(int slot, Consumer<MenuAction.ActionArguments> action) {
        addAction(new ItemTemplate(slot, VanillaItems.EMERALD.type(), BingoReloaded.applyTitleFormat(BingoMessage.MENU_SAVE.asPhrase())), action);
        return this;
    }

    public BasicMenu addCloseAction(@NotNull ItemTemplate item) {
        MenuAction closeAction = new MenuAction()
        {
            @Override
            public void use(ActionArguments arguments) {
                close(arguments.player());
            }
        };
        addItem(item, closeAction);

        return this;
    }

    public void addItems(@NotNull ItemTemplate... items) {
        for (ItemTemplate item : items) {
            addItem(item, null);
        }
    }

    public void addActions(@NotNull MenuAction... actions) {
        for (MenuAction action : actions) {
            addAction(action);
        }
    }

    public BasicMenu removeItem(int slotIdx) {
        this.getBackedInventory().setItem(slotIdx, null);
        itemGroup.removeItem(slotIdx);
        return this;
    }

    public MenuBoard getMenuBoard() {
        return this.manager;
    }

    public static int slotCountForMenu(MenuType type) {
        return switch (type) {
            case CHEST -> 27;
            case ANVIL -> 3;
        };
    }

    @Override
    public void beforeOpening(PlayerHandle player) {
    }

    @Override
    public boolean onClick(PlayerHandle player, int clickedSlot, ClickType clickType) {
        return itemGroup.handleClick(this, player, clickedSlot, clickType);
    }

    @Override
    public boolean onDrag() {
        return true;
    }


    @Override
    public void beforeClosing(PlayerHandle player) {
    }

    @Override
    public MenuType type() {
        return type;
    }

    @Override
    public Component title() {
        return title;
    }

    @Override
    public @NotNull InventoryTemplate getBackedInventory() {
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
