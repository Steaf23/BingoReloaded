package io.github.steaf23.bingoreloaded.tasks;

import net.kyori.adventure.text.Component;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public interface TaskData extends ConfigurationSerializable, Serializable
{
    Component getName();
    Component getChatDescription();
    Component[] getItemDescription();
    default int getStackSize()
    {
        return 1;
    }
    boolean isTaskEqual(TaskData other);
    @NotNull PersistentDataContainer pdcSerialize(PersistentDataContainer stream);
}
