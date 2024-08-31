package io.github.steaf23.bingoreloaded.data.core.node;

import io.github.steaf23.bingoreloaded.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.data.core.node.datatype.NodeDataType;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BranchNode implements Node, DataStorage<BranchNode>
{
    private final HashMap<String, Node> children = new HashMap<>();

    public BranchNode() {

    }

    public BranchNode(InputStream stream) throws IOException {
        deserialize(stream);
    }

    public Map<String, Node> getChildren() {
        return children;
    }

    public Set<String> getKeys() {
        return new HashSet<>(getChildren().keySet());
    }

    public void setString(String path, @NotNull String value) {
        set(path, new DataNode<>(NodeDataType.STRING, value));
    }

    public String getString(String path) {
        return getString(path, "");
    }

    public String getString(String path, String def) {
        Node node = get(path);
        if (node instanceof DataNode<?> data) {
            return data.getType() == NodeDataType.STRING ? (String) data.getValue() : def;
        } else {
            return def;
        }
    }

    public <T> void setList(String path, NodeDataType<T> type, List<T> values) {
        set(path, new DataListNode<>(type, values));
    }

    public <T extends NodeSerializer> void setList(String path, List<T> values) {
        set(path, new DataListNode<T>((NodeDataType<T>) NodeDataType.SERIALIZABLE, values));
    }

    public <T> List<T> getList(String path, NodeDataType<T> dataType) {
        Node node = get(path);
        if (node instanceof DataListNode<?> data) {
            return data.getType() == dataType ? (List<T>) data.getValues() : List.of();
        } else {
            return List.of();
        }
    }

    public <T extends NodeSerializer> List<T> getList(String path, Class<T> classType) {
        Node node = get(path);
        if (node instanceof DataListNode<?> data) {
            return data.getType() == NodeDataType.SERIALIZABLE ? (List<T>) data.getValues() : List.of();
        } else {
            return List.of();
        }
    }

    public void setSerializable(String path, NodeSerializer value) {
        set(path, new DataNode<>(NodeDataType.SERIALIZABLE, value));
    }

    public <T extends NodeSerializer> T getSerializable(String path, Class<T> classType) {
        Node node = get(path);
        if (node instanceof DataNode<?> data) {
            return data.getType() == NodeDataType.SERIALIZABLE ? classType.cast(data.getValue()) : null;
        } else {
            return null;
        }
    }

    public <T extends NodeSerializer> T getSerializable(String path, Class<T> classType, T def) {
        Node node = get(path);
        if (node.getKeys().isEmpty()) {
            return def;
        }
        try {
            // Use reflection to find a constructor that takes a Node
            Constructor<T> constructor = classType.getConstructor(BranchNode.class);
            if (!(node instanceof BranchNode branch)) {
                return def;
            }
            return constructor.newInstance(branch);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("The class " + classType.getName() + " must have a constructor that takes a Node.", e);
        } catch (Exception e) {
            throw new RuntimeException("Could not serialize class " + classType.getName() + " to Node, make sure it has a constructor that takes a Node", e);
        }
    }

    public void setBoolean(String path, boolean value) {
        set(path, new DataNode<>(NodeDataType.BOOLEAN, value));
    }

    public boolean getBoolean(String path) {
        return getBoolean(path, false);
    }

    public boolean getBoolean(String path, Boolean def) {
        Node node = get(path);
        if (node instanceof DataNode<?> data) {
            return data.getType() == NodeDataType.BOOLEAN ? (boolean) data.getValue() : def;
        } else {
            return def;
        }
    }

    public void setInt(String path, int value) {
        set(path, new DataNode<>(NodeDataType.INT, value));
    }

    public int getInt(String path) {
        return getInt(path, 0);
    }

    public int getInt(String path, int def) {
        Node node = get(path);
        if (node instanceof DataNode<?> data) {
            return data.getType() == NodeDataType.INT ? (int) data.getValue() : def;
        } else {
            return def;
        }
    }

    public void setDouble(String path, double value) {
        set(path, new DataNode<>(NodeDataType.DOUBLE, value));
    }

    public double getDouble(String path) {
        return getDouble(path, 0.0D);
    }

    public double getDouble(String path, double def) {
        Node node = get(path);
        if (node instanceof DataNode<?> data) {
            return data.getType() == NodeDataType.DOUBLE ? (double) data.getValue() : def;
        } else {
            return def;
        }
    }

    public void setBytes(String path, byte[] value) {
        set(path, new DataNode<>(NodeDataType.BYTES, value));
    }

    public byte[] getBytes(String path) {
        Node node = get(path);
        if (node instanceof DataNode<?> data) {
            return data.getType() == NodeDataType.BYTES ? (byte[]) data.getValue() : new byte[]{};
        } else {
            return new byte[]{};
        }
    }

    public void setItemStack(String path, ItemStack value) {
        set(path, new DataNode<>(NodeDataType.ITEM_STACK, value));
    }

    public @NotNull ItemStack getItemStack(String path) {
        Node node = get(path);
        if (node instanceof DataNode<?> data) {
            return data.getType() == NodeDataType.ITEM_STACK ? (ItemStack) data.getValue() : new ItemStack(Material.AIR);
        } else {
            return new ItemStack(Material.AIR);
        }
    }

    public void setUUID(String path, UUID value) {
        set(path, new DataNode<>(NodeDataType.UUID, value));
    }

    public @Nullable UUID getUUID(String path) {
        Node node = get(path);
        if (node instanceof DataNode<?> data) {
            return data.getType() == NodeDataType.UUID ? (UUID) data.getValue() : null;
        } else {
            return null;
        }
    }

    public void setLocation(String path, Location value) {
        set(path, new DataNode<>(NodeDataType.LOCATION, value));
    }

    public @Nullable Location getLocation(String path) {
        Node node = get(path);
        if (node instanceof DataNode<?> data) {
            return data.getType() == NodeDataType.LOCATION ? (Location) data.getValue() : null;
        } else {
            return null;
        }
    }

    public @NotNull Location getLocation(String path, @NotNull Location def) {
        Node node = get(path);
        if (node instanceof DataNode<?> data) {
            return data.getType() == NodeDataType.LOCATION ? (Location) data.getValue() : def;
        } else {
            return def;
        }
    }

    @Override
    public void setStorage(String path, BranchNode value) {
        set(path, value);
    }

    @Override
    public @Nullable BranchNode getStorage(String path) {
        return getNode(path, BranchNode.class);
    }

    public void set(String path, Node value) {
        String[] fullPath = Arrays.stream(path.split("\\.")).filter(s -> !s.isEmpty()).toList().toArray(new String[]{});
        if (fullPath.length == 0) {
            return;
        }
        if (fullPath.length == 1) {
            children.put(fullPath[0], value);
            return;
        }
        // get or create new branch node if node at fullPath is not a branch node
        Node subNode = children.get(fullPath[0]);
        BranchNode node;
        if (subNode instanceof BranchNode branch) {
            node = branch;
        } else { //if there is no node at the path yet or if it is not a branch node, overwrite it.
            node = new BranchNode();
            children.put(fullPath[0], node);
        }
        node.set(path.substring(fullPath[0].length() + 1), value);
    }

    public @NotNull Node get(String path) {
        String[] fullPath = Arrays.stream(path.split("\\.")).filter(s -> !s.isEmpty()).toList().toArray(new String[]{});
        if (fullPath.length == 0) {
            return new BranchNode();
        }
        if (fullPath.length == 1) {
            return children.get(fullPath[0]);
        }
        // return empty value if node at fullPath is not a branch node
        Node subNode = children.get(fullPath[0]);
        BranchNode node;
        if (subNode instanceof BranchNode branch) {
            node = branch;
        } else { //if there is no node at the path yet or if it is not a branch node, return an empty node.
            return new BranchNode();
        }
        return node.get(path.substring(fullPath[0].length() + 1));
    }

    public <T extends Node> @Nullable T getNode(String path, Class<T> nodeType) {
        try {
            return nodeType.cast(get(path));
        } catch (ClassCastException e) {
            return null;
        }
    }

    /**
     * Also erases parent nodes of data node if they are empty after removal
     */
    public void erase(String path) {
        System.out.println("erasing " + path);
        String[] fullPath = Arrays.stream(path.split("\\.")).filter(s -> !s.isEmpty()).toList().toArray(new String[]{});
        if (fullPath.length == 0) {
            return;
        }
        if (fullPath.length == 1) {
            children.remove(fullPath[0]);
            System.out.println("children: " + children);
            return;
        }
        // remove node if node at fullPath is not a branch node
        Node subNode = children.get(fullPath[0]);
        if (subNode instanceof BranchNode branch) {
            branch.erase(path.substring(fullPath[0].length() + 1));
            if (branch.children.isEmpty()) {
                erase(fullPath[0]);
            }
        } else {
            children.remove(fullPath[0]);
        }
    }

    public boolean contains(String path) {
        String[] fullPath = Arrays.stream(path.split("\\.")).filter(s -> !s.isEmpty()).toList().toArray(new String[]{});
        if (fullPath.length == 0) {
            return false;
        }
        if (fullPath.length == 1) {
            return children.containsKey(fullPath[0]);
        }
        Node subNode = children.get(fullPath[0]);
        BranchNode node;
        if (subNode instanceof BranchNode branch) {
            node = branch;
        } else { //if there is no node at the path yet or if it is not a branch node, it can't contain the path.
            return false;
        }
        return node.contains(path.substring(fullPath[0].length() + 1));
    }

    @Override
    public void serialize(OutputStream stream) throws IOException {
        NodeDataType.INT.serializeValue(stream, children.size());
        for (String c : children.keySet()) {
            NodeDataType.STRING.serializeValue(stream, c);

            Node node = children.get(c);
            if (node instanceof BranchNode) {
                NodeDataType.INT.serializeValue(stream, 0);
            } else if (node instanceof DataNode<?> data) {
                NodeDataType.INT.serializeValue(stream, 1);
                NodeDataType.INT.serializeValue(stream, NodeDataType.indexFromType(data.getType()));
            } else if (node instanceof DataListNode<?> list) {
                NodeDataType.INT.serializeValue(stream, 2);
                NodeDataType.INT.serializeValue(stream, NodeDataType.indexFromType(list.getType()));
            }
            node.serialize(stream);
        }
    }

    @Override
    public void deserialize(InputStream stream) throws IOException {
        children.clear();
        int size = NodeDataType.INT.deserializeValue(stream);
        for (int i = 0; i < size; i++) {
            String name = NodeDataType.STRING.deserializeValue(stream);
            int nodeType = NodeDataType.INT.deserializeValue(stream);
            Node node;
            switch (nodeType) {
                case 0 -> node = new BranchNode(stream);
                case 1 -> {
                    NodeDataType<?> type = NodeDataType.typeFromIndex(NodeDataType.INT.deserializeValue(stream));
                    if (type == null)
                    {
                        ConsoleMessenger.log("INVALID TYPE FROM DATA NODE");
                        return;
                    }
                    node = new DataNode<>(type, stream);
                }
                case 2 -> {
                    NodeDataType<?> type = NodeDataType.typeFromIndex(NodeDataType.INT.deserializeValue(stream));
                    if (type == null)
                    {
                        ConsoleMessenger.log("INVALID TYPE FROM DATA LIST NODE");
                        return;
                    }
                    node = new DataListNode<>(type, stream);
                }
                default -> {
                    System.out.println("NO NODE FOUND WITH NAME " + name);
                    ConsoleMessenger.error("NO NODE FOUND WITH NAME " + name);
                    return;
                }
            }
            children.put(name, node);
        }
    }

    @Override
    public String toString() {
        return "BranchNode{" +
                "children=" + children +
                '}';
    }

    public void clear() {
        children.clear();
    }
}
