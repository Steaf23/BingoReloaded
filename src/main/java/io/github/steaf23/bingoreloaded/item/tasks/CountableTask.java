package io.github.steaf23.bingoreloaded.item.tasks;

public interface CountableTask extends TaskData
{
    public int getCount();

    public CountableTask updateTask(int newCount);
}
