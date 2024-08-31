package io.github.steaf23.bingoreloaded.data.core.node.datatype;

import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ItemStackDataType implements NodeDataType<ItemStack>
{
    @Override
    public void serializeValue(OutputStream stream, ItemStack value) throws IOException {
        byte[] bytes = value.serializeAsBytes();
        NodeDataType.INT.serializeValue(stream, bytes.length);
        stream.write(bytes);
    }

    @Override
    public ItemStack deserializeValue(InputStream stream) throws IOException {
        int length = NodeDataType.INT.deserializeValue(stream);
        try {
            return ItemStack.deserializeBytes(stream.readNBytes(length));
        } catch (IOException e) {
            ConsoleMessenger.bug(e.getMessage(), this);
        }
        return ItemStack.of(Material.AIR);
    }
}
