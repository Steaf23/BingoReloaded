package io.github.steaf23.bingoreloaded.core.tasks;

public interface CountableTask extends TaskData
{
    int getCount();

    CountableTask updateTask(int newCount);

    @Override
    default int getStackSize()
    {
        return getCount();
    }
}
