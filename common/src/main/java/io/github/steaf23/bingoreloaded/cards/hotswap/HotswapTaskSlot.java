package io.github.steaf23.bingoreloaded.cards.hotswap;

import io.github.steaf23.bingoreloaded.api.CardDisplayInfo;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.tasks.GameTask;

public interface HotswapTaskSlot
{
    boolean isRecovering();
    void startRecovering();

    void updateTaskTime();
	int getFullTime();
    int getCurrentTime();

    ItemTemplate convertToItem(GameTask task, CardDisplayInfo displayInfo);
}
