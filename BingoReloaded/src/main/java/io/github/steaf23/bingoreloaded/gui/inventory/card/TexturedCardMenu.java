package io.github.steaf23.bingoreloaded.gui.inventory.card;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.data.FontMappingData;
import io.github.steaf23.bingoreloaded.gui.inventory.CardMenu;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.playerdisplay.inventory.BasicMenu;
import io.github.steaf23.playerdisplay.inventory.MenuBoard;
import io.github.steaf23.playerdisplay.inventory.item.ItemTemplate;
import io.github.steaf23.playerdisplay.util.MultilineComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TexturedCardMenu extends BasicMenu implements CardMenu
{
    protected final BingoGamemode mode;
    protected final CardSize size;
    protected List<GameTask> tasks;

    public TexturedCardMenu(MenuBoard board, BingoGamemode mode, CardSize size) {
        super(board, buildTitle(mode, size), false);
        this.tasks = new ArrayList<>();
        this.mode = mode;
        this.size = size;
    }

    private static Component buildTitle(BingoGamemode mode, CardSize size) {
        Component result = Component.text().color(NamedTextColor.WHITE)
                .append(Component.translatable("space.-8").append(Component.text(getTextureFromMode(mode, size))))
                .build();
        return result;
    }

    public void updateTasks(List<GameTask> tasks) {
        this.tasks = tasks;
        for (int i = 0; i < tasks.size(); i++) {
            addItem(getItemFromTask(i).setSlot(size.getCardInventorySlot(i)));
        }
    }

    @Override
    public void beforeOpening(HumanEntity player) {
        for (int i = 0; i < tasks.size(); i++)
        {
            addItem(getItemFromTask(i).setSlot(size.getCardInventorySlot(i)));
        }
    }

    @Override
    public CardMenu copy() {
        return new TexturedCardMenu(getMenuBoard(), mode, size);
    }

    protected @NotNull ItemTemplate getItemFromTask(int taskIndex) {
        ItemTemplate item = tasks.get(taskIndex).toItem();
        if (tasks.get(taskIndex).isCompleted()) {
            item.setMaterial(Material.LEATHER_CHESTPLATE);
            item.setCustomModelData(1010);
            tasks.get(taskIndex).getCompletedBy().ifPresent(p -> item.setLeatherColor(p.getTeam().getColor()));
            item.setGlowing(false);
        }
        return item;
    }

    public void setInfo(Component name, Component... description) {
        ItemTemplate info = new ItemTemplate(0, Material.MAP,
                name.decorate(TextDecoration.BOLD).color(mode.getColor()),
                MultilineComponent.from(NamedTextColor.YELLOW, TextDecoration.ITALIC, description))
                .setCustomModelData(1011);
        addItem(info);
    }

    /**
     * @param mode
     * @param size
     * @return
     */
    public static String getTextureFromMode(BingoGamemode mode, CardSize size) {
        FontMappingData mappings = BingoReloaded.getInstance().getCharacterMappings();
        if (size == CardSize.X3) {
            return mappings.mapCharacter(mode.getDataName() + "_3");

        } else if (size == CardSize.X5) {
            return mappings.mapCharacter(mode.getDataName() + "_5");
        }

        return "regular_5";
    }
}
