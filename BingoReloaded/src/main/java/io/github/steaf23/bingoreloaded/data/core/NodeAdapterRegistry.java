package io.github.steaf23.bingoreloaded.data.core;

import io.github.steaf23.bingoreloaded.data.core.node.NodeAdapter;

import java.util.HashMap;
import java.util.Map;

public class NodeAdapterRegistry
{
    private final Map<Class<?>, NodeAdapter<?>> entries;

    public NodeAdapterRegistry() {
        this.entries = new HashMap<>();
    }

    /**
     * Registers a NodeAdapter so that the plugin knows how to adapt its data to a node.
     * <p>
     * There can only be one adapter registered per class type.
     */
    public <T> void registerAdapter(Class<T> classType, NodeAdapter<T> adapter) {
        entries.put(classType, adapter);
    }

    /**
     * Unregisters the adapter that adapts the given class type previously registered.
     */
    public void unregisterAdapter(Class<?> classType) {
        entries.remove(classType);
    }

    public NodeAdapter<?> getAdapter(Class<?> classType) {
        return entries.get(classType);
    }
}
