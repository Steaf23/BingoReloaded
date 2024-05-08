package io.github.steaf23.bingoreloaded.tasks;

import io.github.steaf23.easymenulib.menu.item.ItemText;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public interface TaskData extends ConfigurationSerializable, Serializable
{
    ItemText getItemDisplayName();
    ItemText[] getItemDescription();
    BaseComponent getDescription();
    default int getStackSize()
    {
        return 1;
    }
    boolean isTaskEqual(TaskData other);
    @NotNull PersistentDataContainer pdcSerialize(PersistentDataContainer stream);
}
