package io.github.steaf23.bingoreloaded.data.core.node;

import org.jetbrains.annotations.NotNull;

/**
 * Based on Gson TypeAdapter, adapting a complex object into a node used for storing data.
 *
 * @param <T> Type of class to serialize
 */
public interface NodeAdapter<T>
{
    @NotNull
    T fromNode(BranchNode node);

    @NotNull
    BranchNode toNode(T value);
}
