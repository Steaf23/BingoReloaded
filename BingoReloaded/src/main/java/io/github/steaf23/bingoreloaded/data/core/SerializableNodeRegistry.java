package io.github.steaf23.bingoreloaded.data.core;

import io.github.steaf23.bingoreloaded.data.core.node.NodeSerializer;
import io.github.steaf23.bingoreloaded.tasks.BingoStatistic;
import io.github.steaf23.bingoreloaded.tasks.ItemTask;
import io.github.steaf23.bingoreloaded.tasks.StatisticTask;

import java.util.HashMap;

public class SerializableNodeRegistry extends HashMap<String, Class<? extends NodeSerializer>>
{
    public SerializableNodeRegistry() {
        put(StatisticTask.class.getName(), StatisticTask.class);
        put(BingoStatistic.class.getName(), BingoStatistic.class);
        put(ItemTask.class.getName(), ItemTask.class);
    }
}

