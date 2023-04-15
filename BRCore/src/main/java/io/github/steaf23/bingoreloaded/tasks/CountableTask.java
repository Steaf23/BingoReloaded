package io.github.steaf23.bingoreloaded.tasks;

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
