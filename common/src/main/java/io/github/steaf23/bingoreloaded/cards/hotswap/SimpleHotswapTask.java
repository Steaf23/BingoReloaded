package io.github.steaf23.bingoreloaded.cards.hotswap;

import io.github.steaf23.bingoreloaded.tasks.GameTask;

public class SimpleHotswapTask implements HotswapTaskHolder
{
	int fullTime;
    int currentTime;
    private final GameTask task;
    boolean recovering = false;

    public SimpleHotswapTask(GameTask task, int recoveryTime) {
        this.task = task;
        this.currentTime = recoveryTime;
		this.fullTime = recoveryTime;
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
	public int getFullTime() {
		return recovering ? fullTime : -1;
	}

	@Override
    public int getCurrentTime() {
        return currentTime;
    }
}
