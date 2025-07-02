package io.github.steaf23.bingoreloaded.tasks.data;

import io.github.steaf23.bingoreloaded.lib.api.ItemType;
import net.kyori.adventure.text.Component;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

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
    @NotNull PersistentDataContainer pdcSerialize(PersistentDataContainer stream);
    boolean shouldItemGlow();

    ItemType getDisplayMaterial(boolean genericItem);

    int getRequiredAmount();
}
