package io.github.steaf23.bingoreloaded.data.core;

public interface DataAccessor extends DataStorage
{
    String getLocation();
    String getFileExtension();
    void load();
    void saveChanges();
}
