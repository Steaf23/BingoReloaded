package io.github.steaf23.bingoreloaded.core.tasks;

public interface CountableTask extends TaskData
{
    public int getCount();

    public CountableTask updateTask(int newCount);
}
