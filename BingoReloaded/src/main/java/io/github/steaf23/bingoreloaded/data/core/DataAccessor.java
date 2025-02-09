package io.github.steaf23.bingoreloaded.data.core;

/**
 * A data accessor acts like a file handle to read and write from a plugin resource or any other file depending on the implementation.
 * While also giving all benefits of the data storage to put and retrieve data.
 */
public interface DataAccessor extends DataStorage
{
    String getLocation();
    String getFileExtension();

    void load();
    void saveChanges();

    boolean isInternalReadOnly();
}
