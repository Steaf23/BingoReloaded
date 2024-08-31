package io.github.steaf23.bingoreloaded.data.core;

/**
 * Not safe for async access!
 */
public interface DataAccessor<T extends DataStorage<?>> extends DataStorage<T>
{
    String getLocation();
    void load();
    void saveChanges();
}
