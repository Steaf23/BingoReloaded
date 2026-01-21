package io.github.steaf23.bingoreloaded.cards.hotswap;

import io.github.steaf23.bingoreloaded.tasks.GameTask;

public interface HotswapTaskHolder
{
    GameTask getTask();

    boolean isRecovering();
    void startRecovering();

    void updateTaskTime();
	int getFullTime();
    int getCurrentTime();
}
