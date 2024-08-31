package io.github.steaf23.bingoreloaded.data.core.node.datatype;

import io.github.steaf23.bingoreloaded.data.core.SerializableNodeRegistry;
import io.github.steaf23.bingoreloaded.data.core.node.BranchNode;
import io.github.steaf23.bingoreloaded.data.core.node.NodeSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerializableDataType<T extends NodeSerializer> implements NodeDataType<NodeSerializer>
{
    public static SerializableNodeRegistry registry = new SerializableNodeRegistry();

    @Override
    public void serializeValue(OutputStream stream, NodeSerializer value) throws IOException {
        NodeDataType.STRING.serializeValue(stream, value.getClass().getName());
        value.toNode().serialize(stream);
    }

    @Override
    public T deserializeValue(InputStream stream) throws IOException {
        String className = NodeDataType.STRING.deserializeValue(stream);
        BranchNode node = new BranchNode(stream);
        Class<? extends NodeSerializer> classType = registry.get(className);
        if (classType == null) {
            return null;
        }

        try {
            NodeSerializer serializable = classType.getConstructor(BranchNode.class).newInstance(node);
            return (T)serializable;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("The class " + classType.getName() + " must have a constructor that takes a Node.", e);
        } catch (Exception e) {
            throw new RuntimeException("Could not serialize class " + classType.getName() + " to Node, make sure it has a constructor that takes a Node", e);
        }
    }
}
