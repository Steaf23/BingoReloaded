package io.github.steaf23.bingoreloaded.gui.inventory.item;

import io.github.steaf23.bingoreloaded.data.core.node.BranchNode;
import io.github.steaf23.bingoreloaded.data.core.node.NodeBuilder;
import io.github.steaf23.bingoreloaded.data.core.node.NodeSerializer;
import io.github.steaf23.playerdisplay.inventory.item.ItemTemplate;
import org.bukkit.inventory.ItemStack;


public record SerializableItem(int slot, ItemStack stack) implements NodeSerializer
{
    public SerializableItem(BranchNode node) {
        this(
                node.getInt("slot"),
                node.getItemStack("stack")
        );
    }

    @Override
    public BranchNode toNode() {
        return new NodeBuilder()
                .withInt("slot", slot)
                .withItemStack("stack", stack)
                .getNode();
    }

    public static SerializableItem fromItemTemplate(ItemTemplate template) {
        return new SerializableItem(template.getSlot(), template.buildItem(false));
    }
}