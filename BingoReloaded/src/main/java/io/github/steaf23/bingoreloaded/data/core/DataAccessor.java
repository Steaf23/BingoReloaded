package io.github.steaf23.bingoreloaded.data.core;

import org.bukkit.plugin.java.JavaPlugin;

public interface DataAccessor extends DataStorage
{
    String getLocation();
    void load();
    void saveChanges();
    JavaPlugin getPlugin();
}
