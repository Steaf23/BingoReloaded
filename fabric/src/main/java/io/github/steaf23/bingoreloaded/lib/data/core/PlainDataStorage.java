package io.github.steaf23.bingoreloaded.lib.data.core;

import io.github.steaf23.bingoreloaded.lib.api.WorldPosition;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagAdapter;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagDataType;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlainDataStorage implements DataStorage {

	protected Map<String, Object> root;

	public PlainDataStorage() {
		this.root = new HashMap<>();
	}

	@Override
	public DataStorage createNew() {
		return new PlainDataStorage();
	}

	@Override
	public Set<String> getKeys() {
		return root.keySet();
	}

	@Override
	public void setByte(String path, byte value) {
		set(path, value);
	}

	@Override
	public byte getByte(String path, byte def) {
		Object v = get(path);
		return v instanceof Byte res ? res : def;
	}

	@Override
	public void setShort(String path, short value) {
		set(path, value);
	}

	@Override
	public short getShort(String path, short def) {
		Object v = get(path);
		return v instanceof Short res ? res : def;
	}

	@Override
	public void setInt(String path, int value) {
		set(path, value);
	}

	@Override
	public int getInt(String path, int def) {
		Object v = get(path);
		return v instanceof Integer res ? res : def;
	}

	@Override
	public void setLong(String path, long value) {
		set(path, value);
	}

	@Override
	public long getLong(String path, long def) {
		Object v = get(path);
		return v instanceof Long res ? res : def;
	}

	@Override
	public void setString(String path, @NotNull String value) {
		set(path, value);
	}

	@Override
	public @NotNull String getString(String path, String def) {
		Object v = get(path);
		return v instanceof String res ? res : def;
	}

	@Override
	public <T> void setList(String path, TagDataType<T> type, List<T> values) {
		set(path, values);
	}

	@Override
	public <T> List<T> getList(String path, TagDataType<T> dataType) {
		return List.of();
	}

	@Override
	public <T> void setList(String path, TagAdapter<T, ?> adapterType, List<T> values) {
		set(path, values);
	}

	@Override
	public <T> List<T> getList(String path, TagAdapter<T, ?> adapterType) {
		return List.of();
	}

	@Override
	public <T> void setSerializableList(String path, Class<T> dataType, List<T> values) {
		set(path, values);
	}

	@Override
	public <T> List<T> getSerializableList(String path, Class<T> dataType) {
		return List.of();
	}

	@Override
	public void setBoolean(String path, boolean value) {
		set(path, value);
	}

	@Override
	public boolean getBoolean(String path) {
		Object v = get(path);
		return v instanceof Boolean res ? res : false;
	}

	@Override
	public boolean getBoolean(String path, boolean def) {
		Object v = get(path);
		return v instanceof Boolean res ? res : def;
	}

	@Override
	public void setFloat(String path, float value) {
		set(path, value);
	}

	@Override
	public float getFloat(String path, float def) {
		Object v = get(path);
		return v instanceof Float res ? res : def;
	}

	@Override
	public void setDouble(String path, double value) {
		set(path, value);
	}

	@Override
	public double getDouble(String path, double def) {
		Object v = get(path);
		return v instanceof Double res ? res : def;
	}

	@Override
	public void setItemStack(String path, StackHandle value) {
		set(path, value);
	}

	@Override
	public @NotNull StackHandle getItemStack(String path) {
		Object v = get(path);
		return v instanceof StackHandle res ? res : StackHandle.create(ItemType.AIR);
	}

	@Override
	public void setUUID(String path, @Nullable UUID value) {
		set(path, value);
	}

	@Override
	public @Nullable UUID getUUID(String path) {
		Object v = get(path);
		return v instanceof UUID res ? res : null;
	}

	@Override
	public void setWorldPosition(String path, @NotNull WorldPosition value) {
		set(path, value);
	}

	@Override
	public @Nullable WorldPosition getWorldPosition(String path) {
		Object v = get(path);
		return v instanceof WorldPosition res ? res : null;
	}

	@Override
	public @NotNull WorldPosition getWorldPosition(String path, @NotNull WorldPosition def) {
		Object v = get(path);
		return v instanceof WorldPosition res ? res : def;
	}

	@Override
	public void setNamespacedKey(String path, @NotNull Key value) {
		set(path, value);
	}

	@Override
	public @NotNull Key getNamespacedKey(String path) {
		Object v = get(path);
		return v instanceof Key res ? res : Key.key("", "");
	}

	@Override
	public void setStorage(String path, DataStorage value) {
		set(path, value);
	}

	@Override
	public @Nullable DataStorage getStorage(String path) {
		return null;
	}

	@Override
	public void erase(String path) {
		removeNested(root, path);
	}

	@Override
	public boolean contains(String path) {
		return containsFullPath(root, path);
	}

	@Override
	public void clear() {
		root = new HashMap<>();
	}

	private @Nullable Object get(String key) {
		return getNested(root, key);
	}

	private Object getNested(Map<String, Object> root, String path) {
		String[] fullPath = Arrays.stream(path.split("\\.")).filter(s -> !s.isEmpty()).toList().toArray(new String[]{});
		if (fullPath.length == 0) {
			return null;
		}
		if (fullPath.length == 1) {
			return root.get(fullPath[0]);
		}
		// return empty value if node at fullPath is not a branch node
		Object data = root.get(fullPath[0]);
		Map<String, Object> subNode;
		if (data instanceof Map<?, ?> branch) {
			subNode = (Map<String, Object>) branch;
		} else { //if there is no node at the path yet or if it is not a branch node, return an empty node.
			return null;
		}
		return getNested(subNode, path.substring(fullPath[0].length() + 1));
	}

	private void set(String key, Object value) {
		setNested(root, key, value);
	}

	private static void setNested(Map<String, Object> root, String key, Object value) {
		String[] fullPath = Arrays.stream(key.split("\\.")).filter(s -> !s.isEmpty()).toList().toArray(new String[]{});
		if (fullPath.length == 0) {
			return;
		}
		if (fullPath.length == 1) {
			root.put(fullPath[0], value);
			return;
		}
		// get or create new branch node if node at fullPath is not a branch node
		Object data = root.get(fullPath[0]);
		Map<String, Object> subNode;
		if (data instanceof Map<?, ?> branch) {
			subNode = (Map<String, Object>)branch;
		} else { //if there is no node at the path yet or if it is not a branch node, overwrite it.
			subNode = new HashMap<>();
			// subNode is now a TagTree, but we cannot add a TagTree to root..., this means the value we should pass into putData has to be the compound, but
			root.put(fullPath[0], subNode);
		}
		setNested(subNode, key.substring(fullPath[0].length() + 1), value);
	}

	private static void removeNested(Map<String, Object> root, String key) {
		String[] fullPath = Arrays.stream(key.split("\\.")).filter(s -> !s.isEmpty()).toList().toArray(new String[]{});
		if (fullPath.length == 0) {
			return;
		}
		if (fullPath.length == 1) {
			root.remove(fullPath[0]);
			return;
		}
		// remove node if node at fullPath is not a branch node
		Object data = root.get(fullPath[0]);
		if (data instanceof  Map<?, ?> branch) {
			removeNested((Map<String, Object>) branch, key.substring(fullPath[0].length() + 1));
		} else {
			root.remove(fullPath[0]);
		}
	}

	private static boolean containsFullPath(Map<String, Object> root, String key) {
		String[] fullPath = Arrays.stream(key.split("\\.")).filter(s -> !s.isEmpty()).toList().toArray(new String[]{});
		if (fullPath.length == 0) {
			return false;
		}
		if (fullPath.length == 1) {
			return root.containsKey(fullPath[0]);
		}
		// return empty value if node at fullPath is not a branch node
		Object data = root.get(fullPath[0]);
		Map<String, Object> subNode;
		if (data instanceof Map<?, ?> branch) {
			subNode = (Map<String, Object>)branch;
		} else { //if there is no node at the path yet or if it is not a branch node, return an empty node.
			return false;
		}
		return containsFullPath(subNode, key.substring(fullPath[0].length() + 1));
	}
}
