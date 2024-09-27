package io.github.steaf23.bingoreloaded.gui.inventory.card;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.data.TexturedMenuData;
import io.github.steaf23.bingoreloaded.gui.inventory.core.TexturedTitleBuilder;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.playerdisplay.inventory.Menu;
import io.github.steaf23.playerdisplay.inventory.MenuBoard;
import io.github.steaf23.playerdisplay.inventory.item.ItemTemplate;
import io.github.steaf23.playerdisplay.inventory.item.action.MenuItemGroup;
import io.github.steaf23.playerdisplay.util.MultilineComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TexturedCardMenu implements Menu, CardMenu
{
    private final MenuBoard board;
    protected final BingoGamemode mode;
    protected final CardSize size;
    protected List<GameTask> tasks;

    private final MenuItemGroup itemGroup;
    private final Component startingTitle;
    private Inventory openedInventory;

    private ItemTemplate info;

    public static final ItemTemplate DUMMY_ITEM = new ItemTemplate(Material.POISONOUS_POTATO);

    public TexturedCardMenu(MenuBoard board, BingoGamemode mode, CardSize size) {
        ItemTemplate info = DUMMY_ITEM.copyToSlot(0);

        this.board = board;
        this.tasks = new ArrayList<>();
        this.mode = mode;
        this.size = size;
        this.itemGroup = new MenuItemGroup();
        this.startingTitle = buildTitle(mode, size);
        this.info = info;
    }

    protected Component buildTitle(BingoGamemode mode, CardSize size) {
        TexturedMenuData textures = BingoReloaded.getInstance().getTextureData();

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
    public void open(HumanEntity entity) {
        board.open(this, entity);
    }

    @Override
    public MenuBoard getMenuBoard() {
        return board;
    }

    @Override
    public void beforeOpening(HumanEntity player) {
        Map<Integer, TextColor> completedSlots = new HashMap<>();

        for (int i = 0; i < tasks.size(); i++) {
            int rawSlot = size.getCardInventorySlot(i);
            if (tasks.get(i).isCompleted()) {
                tasks.get(i).getCompletedBy().ifPresent(p -> completedSlots.put(rawSlot, p.getTeam().getColor()));
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
    public boolean onClick(InventoryClickEvent event, HumanEntity player, int clickedSlot, ClickType clickType) {
        return itemGroup.handleClick(this, event, player, clickedSlot, clickType);
    }

    @Override
    public boolean onDrag(InventoryDragEvent event) {
        return true;
    }

    @Override
    public void beforeClosing(HumanEntity player) {

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

    @Override
    public CardMenu copy() {
        return new TexturedCardMenu(getMenuBoard(), mode, size);
    }

    protected @NotNull ItemTemplate getItemFromTask(int taskIndex) {
        ItemTemplate item = tasks.get(taskIndex).toItem();
        if (tasks.get(taskIndex).isCompleted()) {
            item.setMaterial(Material.POISONOUS_POTATO);
            item.setCustomModelData(1012);
            item.setGlowing(false);
        }
        return item;
    }

    @Override
    public void setInfo(Component name, Component... description) {
        this.info = DUMMY_ITEM.copyToSlot(0)
                .setName(name.decorate(TextDecoration.BOLD).color(mode.getColor()))
                .setLore(MultilineComponent.from(NamedTextColor.YELLOW, TextDecoration.ITALIC, description))
                .setCustomModelData(1011);
    }

    public void addItem(@NotNull ItemTemplate item) {
        itemGroup.addItem(item);
        getInventory().setItem(item.getSlot(), item.buildItem());
    }

    protected void setTaskItems() {
        for (int i = 0; i < tasks.size(); i++) {
            ItemTemplate item = getItemFromTask(i).setSlot(size.getCardInventorySlot(i));
            addItem(item);
        }
    }

    protected ItemTemplate getInfo() {
        return info;
    }
}
