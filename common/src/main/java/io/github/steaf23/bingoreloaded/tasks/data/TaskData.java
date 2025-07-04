package io.github.steaf23.bingoreloaded.tasks.data;

import io.github.steaf23.bingoreloaded.lib.api.ItemType;
import net.kyori.adventure.text.Component;

public interface TaskData
{
    enum TaskType
    {
        ITEM,
        STATISTIC,
        ADVANCEMENT,
    }

    TaskType getType();
    Component getName();
    Component getChatDescription();
    Component[] getItemDescription();
    boolean isTaskEqual(TaskData other);
    boolean shouldItemGlow();

    ItemType getDisplayMaterial(boolean genericItem);

    int getRequiredAmount();
}
