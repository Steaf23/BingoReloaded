package io.github.steaf23.bingoreloaded.tasks.data;

import net.kyori.adventure.text.Component;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

public interface TaskData
{
    Component getName();
    Component getChatDescription();
    Component[] getItemDescription();
    boolean isTaskEqual(TaskData other);
    @NotNull PersistentDataContainer pdcSerialize(PersistentDataContainer stream);

    int getRequiredAmount();
}
