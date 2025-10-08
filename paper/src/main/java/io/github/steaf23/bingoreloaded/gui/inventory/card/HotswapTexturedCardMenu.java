package io.github.steaf23.bingoreloaded.gui.inventory.card;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.api.CardDisplayInfo;
import io.github.steaf23.bingoreloaded.api.CardMenu;
import io.github.steaf23.bingoreloaded.api.HotswapCardMenu;
import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.cards.hotswap.HotswapTaskHolder;
import io.github.steaf23.bingoreloaded.data.TexturedMenuData;
import io.github.steaf23.bingoreloaded.gui.inventory.core.TexturedTitleBuilder;
import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HotswapTexturedCardMenu extends TexturedCardMenu implements HotswapCardMenu
{
    private List<HotswapTaskHolder> taskHolders;

    public HotswapTexturedCardMenu(BingoReloaded bingo, MenuBoard board, CardDisplayInfo displayInfo) {
        super(bingo, board, displayInfo);
    }

    // Override to only allow tasks getting updated from the task holders.
    @Override
    public void updateTasks(List<GameTask> tasks) {
        super.updateTasks(taskHolders.stream().map(HotswapTaskHolder::getTask).toList());
    }

    @Override
    protected Component buildTitle(BingoGamemode mode, CardSize size) {
        //FIXME: REFACTOR actually add textures.
//        TexturedMenuData textures = BingoReloaded.getInstance().getTextureData();

        TexturedMenuData.Texture cardTexture = null;
//        if (size == CardSize.X3) {
//            cardTexture = textures.getTexture("hotswap_card_3");
//        } else if (size == CardSize.X5) {
//            cardTexture = textures.getTexture("hotswap_card_5");
//        }

        if (cardTexture == null) {
            return Component.empty();
        }

        TexturedTitleBuilder title = new TexturedTitleBuilder()
                .addSpace(cardTexture.menuOffset())
                .addTexture(cardTexture)
                .resetSpace();

        return title.build();
    }

    @Override
    public CardMenu copy(@Nullable Component alternateTitle) {
        return new HotswapTexturedCardMenu(bingo, getMenuBoard(), displayInfo());
    }

    @Override
    public void updateTaskHolders(List<HotswapTaskHolder> taskHolders) {
        this.taskHolders = taskHolders;
        updateTasks(null);

        //TODO: check if inventory needs to be recreated at all by comparing completed items
        Map<Integer, TextColor> completedSlots = new HashMap<>();
        for (int i = 0; i < tasks.size(); i++) {
            int rawSlot = getSlotForTask(i);
            if (tasks.get(i).isCompleted()) {
                tasks.get(i).getCompletedByTeam().ifPresent(team -> completedSlots.put(rawSlot, team.getColor()));
            }
        }

        Inventory oldInventory = getInventory();
        //trick the menu system by remaking the inventory, which will fail its inventory comparison when reading close event.
        setOpenedInventory(Bukkit.createInventory(null, 6 * 9, getStartingTitle()
                .append(SlotBackgroundRenderer.slotCompletedBackground(completedSlots))));

        setTaskItems();
        addItem(getInfo());
        if (oldInventory == null) {
            return;
        }
        var viewers = new ArrayList<>(oldInventory.getViewers());
        viewers.forEach(p -> {
            p.openInventory(getInventory());
        });
    }

    @Override
    public @NotNull ItemTemplate getItemFromTask(int taskIndex) {
        ItemTemplate item = taskHolders.get(taskIndex).convertToItem(displayInfo());
        if (tasks.get(taskIndex).isCompleted()) {
            item.setItemType(ItemTypePaper.of(Material.POISONOUS_POTATO));
            item.setCustomModelData("1012");
            item.setGlowing(false);
        }
        return item;
    }
}
