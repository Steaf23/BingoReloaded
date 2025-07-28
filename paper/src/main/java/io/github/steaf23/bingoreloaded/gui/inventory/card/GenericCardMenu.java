package io.github.steaf23.bingoreloaded.gui.inventory.card;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.api.CardDisplayInfo;
import io.github.steaf23.bingoreloaded.api.CardMenu;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.gui.inventory.item.OpenCardSelectAction;
import io.github.steaf23.bingoreloaded.gui.inventory.item.TaskItemAction;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.bingoreloaded.lib.util.MultilineComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GenericCardMenu extends BasicMenu implements CardMenu
{
    protected List<GameTask> tasks;
    private final CardDisplayInfo displayInfo;
    private final BingoReloaded bingo;

    public GenericCardMenu(BingoReloaded bingo, MenuBoard menuBoard, CardDisplayInfo displayInfo, @Nullable Component alternateTitle)
    {
        super(menuBoard, alternateTitle == null ? BingoMessage.CARD_TITLE.asPhrase() : alternateTitle, displayInfo.size().size);
		this.bingo = bingo;
		this.tasks = new ArrayList<>();
        setMaxStackSizeOverride(64);
        this.displayInfo = displayInfo;
        if (displayInfo.allowViewingOtherCards()) {
            addAction(OpenCardSelectAction.createItem(bingo, 8));
        }
    }

    public void updateTasks(List<GameTask> tasks) {
        this.tasks = tasks;
        for (int i = 0; i < tasks.size(); i++)
        {
            addItem(getItemFromTask(i).setSlot(displayInfo().size().getCardInventorySlot(i)), new TaskItemAction(tasks.get(i)));
        }
    }

    public CardMenu copy(@Nullable Component newTitle) {
        return new GenericCardMenu(bingo, getMenuBoard(), displayInfo(), newTitle);
    }

    @Override
    public CardDisplayInfo displayInfo() {
        return displayInfo;
    }

    public @NotNull ItemTemplate getItemFromTask(int taskIndex) {
        return tasks.get(taskIndex).toItem(displayInfo);
    }

    public void setInfo(Component name, Component... description)
    {
        ItemTemplate info = new ItemTemplate(0, ItemType.of("minecraft:map"),
                name.decorate(TextDecoration.BOLD).color(displayInfo.mode().getColor()),
                MultilineComponent.from(NamedTextColor.YELLOW, TextDecoration.ITALIC, description));
        addItem(info);
    }

    @Override
    public void beforeOpening(PlayerHandle player) {
        updateTasks(tasks);
    }

    public BingoReloaded bingo() {
        return bingo;
    }
}
