package io.github.steaf23.bingoreloaded.item.tasks;

import io.github.steaf23.bingoreloaded.item.itemtext.ItemText;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Map;

public interface TaskData extends ConfigurationSerializable, Serializable
{
    ItemText getDisplayName();
    ItemText getDescription();

    PersistentDataContainer pdcSerialize(PersistentDataContainer stream);
}
