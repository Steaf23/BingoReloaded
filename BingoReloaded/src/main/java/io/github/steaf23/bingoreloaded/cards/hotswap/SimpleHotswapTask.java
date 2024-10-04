package io.github.steaf23.bingoreloaded.cards.hotswap;

import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;
import io.github.steaf23.playerdisplay.inventory.item.ItemTemplate;
import net.kyori.adventure.text.format.TextColor;

public class SimpleHotswapTask implements HotswapTaskHolder
{
    int currentTime;
    private final GameTask task;
    boolean recovering = false;

    public SimpleHotswapTask(GameTask task, int recoveryTime) {
        this.task = task;
        this.currentTime = recoveryTime;
    }

    @Override
    public GameTask getTask() {
        return task;
    }

    @Override
    public boolean isRecovering() {
        return recovering;
    }

    @Override
    public void startRecovering() {
        recovering = true;
    }

    @Override
    public void updateTaskTime() {
        if (recovering) {
            currentTime -= 1;
        }
    }

    @Override
    public int getCurrentTime() {
        return currentTime;
    }

    @Override
    public ItemTemplate convertToItem() {
        ItemTemplate item = task.toItem();
        if (isRecovering()) {
            item.addDescription("time", 1, BingoMessage.HOTSWAP_RECOVER.asPhrase(GameTimer.getTimeAsComponent(currentTime)).color(TextColor.fromHexString("#5cb1ff")));
        }
        return item;
    }
}
