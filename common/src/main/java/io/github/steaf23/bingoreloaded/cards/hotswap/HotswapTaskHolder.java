package io.github.steaf23.bingoreloaded.cards.hotswap;

import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.tasks.GameTask;

public interface HotswapTaskHolder
{
    GameTask getTask();

    boolean isRecovering();
    void startRecovering();

    void updateTaskTime();
    int getCurrentTime();

    ItemTemplate convertToItem();
}
