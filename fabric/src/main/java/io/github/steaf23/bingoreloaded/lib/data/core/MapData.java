package io.github.steaf23.bingoreloaded.lib.data.core;

import io.github.steaf23.bingoreloaded.lib.data.core.node.NodeLikeData;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class MapData extends HashMap<String, MapData.DataNode> implements NodeLikeData.NodeBranch<MapData.DataNode> {

	public record DataNode(Object value) implements NodeLikeData.Node {
	}

	@Override
	public @Nullable DataNode getData(String path) {
		return get(path);
	}

	@Override
	public void putData(String path, @Nullable DataNode data) {
		put(path, data);
	}

	@Override
	public void removeData(String path) {
		remove(path);
	}

	@Override
	public boolean contains(String path) {
		return containsKey(path);
	}
}
