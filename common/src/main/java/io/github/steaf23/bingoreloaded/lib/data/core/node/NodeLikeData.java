package io.github.steaf23.bingoreloaded.lib.data.core.node;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Supplier;

public class NodeLikeData
{
    public interface Node {

    }

    public interface NodeBranch<T extends Node> extends Node
    {
        @Nullable
        T getData(String path);
        void putData(String path, @Nullable T data);
        void removeData(String path);
        boolean contains(String path);
        boolean isEmpty();
    }

    public static <T extends Node> void setNested(NodeBranch<T> root, String key, T value, Supplier<NodeBranch<T>> subNodeSupplier) {
        String[] fullPath = Arrays.stream(key.split("\\.")).filter(s -> !s.isEmpty()).toList().toArray(new String[]{});
        if (fullPath.length == 0) {
            return;
        }
        if (fullPath.length == 1) {
            root.putData(fullPath[0], value);
            return;
        }
        // get or create new branch node if node at fullPath is not a branch node
        Node data = root.getData(fullPath[0]);
        NodeBranch<T> subNode;
        if (data instanceof NodeBranch<?> branch) {
            subNode = (NodeBranch<T>)branch;
        } else { //if there is no node at the path yet or if it is not a branch node, overwrite it.
            subNode = subNodeSupplier.get();
            // subNode is now a TagTree, but we cannot add a TagTree to root..., this means the value we should pass into putData has to be the compound, but
            root.putData(fullPath[0], (T) subNode);
        }
        setNested(subNode, key.substring(fullPath[0].length() + 1), value, subNodeSupplier);
    }

    public static <T extends Node> @Nullable T getNested(NodeBranch<T> root, String key) {
        String[] fullPath = Arrays.stream(key.split("\\.")).filter(s -> !s.isEmpty()).toList().toArray(new String[]{});
        if (fullPath.length == 0) {
            return null;
        }
        if (fullPath.length == 1) {
            return root.getData(fullPath[0]);
        }
        // return empty value if node at fullPath is not a branch node
        Node data = root.getData(fullPath[0]);
        NodeBranch<T> subNode;
        if (data instanceof NodeBranch<?> branch) {
            subNode = (NodeBranch<T>) branch;
        } else { //if there is no node at the path yet or if it is not a branch node, return an empty node.
            return null;
        }
        return getNested(subNode, key.substring(fullPath[0].length() + 1));
    }

    public static <T extends Node> void removeNested(NodeBranch<T> root, String key) {
        String[] fullPath = Arrays.stream(key.split("\\.")).filter(s -> !s.isEmpty()).toList().toArray(new String[]{});
        if (fullPath.length == 0) {
            return;
        }
        if (fullPath.length == 1) {
            root.removeData(fullPath[0]);
            return;
        }
        // remove node if node at fullPath is not a branch node
        Node data = root.getData(fullPath[0]);
        if (data instanceof NodeBranch<?> branch) {
            removeNested(branch, key.substring(fullPath[0].length() + 1));
            //TODO: decide if to remove itself if it has no children... its good because its auto cleanup but bad because of potential invalid access.
//            if (branch.isEmpty()) {
//                removeNested(root, fullPath[0]);
//            }
        } else {
            root.removeData(fullPath[0]);
        }
    }

    public static <T extends Node> boolean containsFullPath(NodeBranch<T> root, String key) {
        String[] fullPath = Arrays.stream(key.split("\\.")).filter(s -> !s.isEmpty()).toList().toArray(new String[]{});
        if (fullPath.length == 0) {
            return false;
        }
        if (fullPath.length == 1) {
            return root.contains(fullPath[0]);
        }
        // return empty value if node at fullPath is not a branch node
        Node data = root.getData(fullPath[0]);
        NodeBranch<T> subNode;
        if (data instanceof NodeBranch<?> branch) {
            subNode = (NodeBranch<T>) branch;
        } else { //if there is no node at the path yet or if it is not a branch node, return an empty node.
            return false;
        }
        return containsFullPath(subNode, key.substring(fullPath[0].length() + 1));
    }
}
