package io.github.steaf23.bingoreloaded.gui.inventory.card;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.api.CardDisplayInfo;
import io.github.steaf23.bingoreloaded.api.CardMenu;
import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.data.TexturedMenuData;
import io.github.steaf23.bingoreloaded.gui.inventory.core.InventoryMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.core.TexturedTitleBuilder;
import io.github.steaf23.bingoreloaded.gui.inventory.core.action.MenuAction;
import io.github.steaf23.bingoreloaded.gui.inventory.core.action.MenuItemGroup;
import io.github.steaf23.bingoreloaded.gui.inventory.item.OpenCardSelectAction;
import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.lib.item.TaskItemConverter;
import io.github.steaf23.bingoreloaded.lib.util.MultilineComponent;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TexturedCardMenu implements InventoryMenu, CardMenu
{
    private final MenuBoard board;
    protected List<GameTask> tasks;
	protected BingoReloaded bingo;

    private final MenuItemGroup itemGroup;
    private final Component startingTitle;
    private Inventory openedInventory;
    private final CardDisplayInfo displayInfo;

    private ItemTemplate info;

    boolean openOnce = false;

    public static final ItemTemplate DUMMY_ITEM = new ItemTemplate(ItemTypePaper.of(Material.POISONOUS_POTATO));

    public TexturedCardMenu(BingoReloaded bingo, MenuBoard board, CardDisplayInfo displayInfo) {
        ItemTemplate info = DUMMY_ITEM.copyToSlot(0);

		this.bingo = bingo;
        this.board = board;
        this.tasks = new ArrayList<>();
        this.itemGroup = new MenuItemGroup();
        this.startingTitle = buildTitle(displayInfo.mode(), displayInfo.size());
        this.info = info;
        this.displayInfo = displayInfo;
		if (displayInfo.allowViewingOtherCards()) {
			addAction(OpenCardSelectAction.createItem(bingo, 8));
		}
    }

    protected Component buildTitle(BingoGamemode mode, CardSize size) {
        TexturedMenuData textures = BingoReloaded.getTextureData();

        TexturedMenuData.Texture cardTexture = null;
        if (size == CardSize.X3) {
            cardTexture = textures.getTexture("card_3");
        } else if (size == CardSize.X5) {
            cardTexture = textures.getTexture("card_5");
        }

        TexturedMenuData.Texture bannerTexture = switch (mode) {
            case REGULAR -> textures.getTexture("banner_regular");
            case LOCKOUT -> textures.getTexture("banner_lockout");
            case COMPLETE -> textures.getTexture("banner_complete");
            case HOTSWAP -> textures.getTexture("banner_hotswap");
        };

        if (bannerTexture == null || cardTexture == null) {
            return Component.empty();
        }

        TexturedTitleBuilder title = new TexturedTitleBuilder()
                .addSpace(cardTexture.menuOffset())
                .addTexture(cardTexture)
                .resetSpace()
                .addSpace(cardTexture.menuOffset())
                .addTexture(bannerTexture)
                .resetSpace();

        return title.build();
    }

    public void updateTasks(List<GameTask> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void open(PlayerHandle entity) {
        board.open(this, entity);
    }

    @Override
    public MenuBoard getMenuBoard() {
        return board;
    }

    @Override
    public void beforeOpening(PlayerHandle player) {
        Map<Integer, TextColor> completedSlots = new HashMap<>();

        for (int i = 0; i < tasks.size(); i++) {
            int rawSlot = getSlotForTask(i);
            if (tasks.get(i).isCompleted()) {
                tasks.get(i).getCompletedByTeam().ifPresent(team -> completedSlots.put(rawSlot, team.getColor()));
            }
        }

        Inventory oldInventory = openedInventory;
        //trick the menu system by remaking the inventory, which will fail its inventory comparison when reading close event.
        openedInventory = Bukkit.createInventory(null, 6 * 9, startingTitle
                .append(SlotBackgroundRenderer.slotCompletedBackground(completedSlots)));

        setTaskItems();
        addItem(info);
        if (oldInventory == null) {
            return;
        }
        var viewers = new ArrayList<>(oldInventory.getViewers());
        viewers.forEach(p -> {
            p.openInventory(openedInventory);
        });
    }

    @Override
    public boolean onClick(InventoryClickEvent event, PlayerHandle player, int clickedSlot, ClickType clickType) {
        return itemGroup.handleClick(this, event, player, clickedSlot, clickType);
    }

    @Override
    public boolean onDrag(InventoryDragEvent event) {
        return true;
    }

    @Override
    public void onCustomAction(Key key, DataStorage payload) {

    }

    @Override
    public void beforeClosing(PlayerHandle player) {

    }

    @Override
    public boolean openOnce() {
        return openOnce;
    }

    @Override
    public void setOpenOnce(boolean value) {
        this.openOnce = value;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return openedInventory;
    }

    protected void setOpenedInventory(@NotNull Inventory inventory) {
        this.openedInventory = inventory;
    }

    protected Component getStartingTitle() {
        return startingTitle;
    }

    /**
     * @param alternateTitle not used by textured menus
     */
    @Override
    public CardMenu copy(@Nullable Component alternateTitle) {
        return new TexturedCardMenu(bingo, getMenuBoard(), displayInfo);
    }

    @Override
    public CardDisplayInfo displayInfo() {
        return displayInfo;
    }

    protected @NotNull ItemTemplate getItemFromTask(int taskIndex) {
        GameTask task = tasks.get(taskIndex);
        ItemTemplate item = TaskItemConverter.taskToItem(task, displayInfo);
        if (tasks.get(taskIndex).isCompleted()) {
            item.setItemType(ItemTypePaper.of(Material.POISONOUS_POTATO));
            item.setCustomModelData("1012");
            item.setGlowing(false);
        }
        return item;
    }

    @Override
    public void setInfo(Component name, Component... description) {
        this.info = DUMMY_ITEM.copyToSlot(0)
                .setName(name.decorate(TextDecoration.BOLD).color(displayInfo.mode().getColor()))
                .setLore(MultilineComponent.from(NamedTextColor.YELLOW, TextDecoration.ITALIC, description))
                .setCustomModelData("1011");
    }

	public void addAction(@NotNull MenuAction action) {
		ItemTemplate item = action.item();
		itemGroup.addItem(item, action);
		getInventory().setItem(item.getSlot(), ((StackHandlePaper)item.buildItem()).handle());
	}

    public void addItem(@NotNull ItemTemplate item) {
        itemGroup.addItem(item, null);
        getInventory().setItem(item.getSlot(), ((StackHandlePaper)item.buildItem()).handle());
    }

    protected void setTaskItems() {
        for (int i = 0; i < tasks.size(); i++) {
            ItemTemplate item = getItemFromTask(i).setSlot(getSlotForTask(i));
            addItem(item);
        }
    }

    protected ItemTemplate getInfo() {
        return info;
    }

    public int getSlotForTask(int index) {
        int slot = displayInfo.size().getCardInventorySlot(index);
        if (displayInfo.size() == CardSize.X3) {
            slot += 9;
        }
        return slot;
    }
}
