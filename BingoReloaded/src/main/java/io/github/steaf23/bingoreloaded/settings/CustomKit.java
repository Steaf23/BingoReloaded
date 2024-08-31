package io.github.steaf23.bingoreloaded.settings;

import io.github.steaf23.bingoreloaded.data.core.node.BranchNode;
import io.github.steaf23.bingoreloaded.data.core.node.NodeBuilder;
import io.github.steaf23.bingoreloaded.data.core.node.datatype.NodeDataType;
import io.github.steaf23.bingoreloaded.data.core.node.NodeSerializer;
import io.github.steaf23.bingoreloaded.gui.inventory.item.SerializableItem;
import io.github.steaf23.playerdisplay.PlayerDisplay;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record CustomKit(Component name, PlayerKit slot, List<SerializableItem> items, int cardSlot) implements NodeSerializer
{
    public CustomKit(BranchNode node) {
        this(
                PlayerDisplay.MINI_BUILDER.deserialize(node.getString("name")),
                kitFromSlot(node.getInt("slot")),
                createSerializableItems(
                        node.getList("item_stacks", NodeDataType.ITEM_STACK),
                        node.getList("item_slots", NodeDataType.INT)),
                node.getInt("card_slot", 40)
        );
    }

    @Override
    public BranchNode toNode() {
        List<ItemStack> stacks = items.stream().map(SerializableItem::stack).toList();
        List<Integer> slots = items.stream().map(SerializableItem::slot).toList();

        return new NodeBuilder()
                .withString("name", PlayerDisplay.MINI_BUILDER.serialize(name))
                .withInt("slot", slotFromKit(slot))
                .withList("item_stacks", NodeDataType.ITEM_STACK, stacks)
                .withList("item_slots", NodeDataType.INT, slots)
                .withInt("card_slot", 40)
                .getNode();
    }

    private static List<SerializableItem> createSerializableItems(List<ItemStack> stacks, List<Integer> slots) {
        if (stacks.size() != slots.size()) {
            return List.of();
        }

        List<SerializableItem> result = new ArrayList<>();
        for (int i = 0; i < stacks.size(); i++) {
            result.add(new SerializableItem(slots.get(i), stacks.get(i)));
        }
        return result;
    }

    private static PlayerKit kitFromSlot(int slot) throws IllegalStateException {
        return switch (slot)
        {
            case 1 -> PlayerKit.CUSTOM_1;
            case 2 -> PlayerKit.CUSTOM_2;
            case 3 -> PlayerKit.CUSTOM_3;
            case 4 -> PlayerKit.CUSTOM_4;
            case 5 -> PlayerKit.CUSTOM_5;
            default -> throw new IllegalStateException("Unexpected value: " + slot);
        };
    }

    private static int slotFromKit(PlayerKit kit) throws IllegalStateException {
        return switch (kit)
        {
            case CUSTOM_1 -> 1;
            case CUSTOM_2 -> 2;
            case CUSTOM_3 -> 3;
            case CUSTOM_4 -> 4;
            case CUSTOM_5 -> 5;
            default -> throw new IllegalStateException("Unexpected kit" + kit.getCardSlot());
        };
    }

    public static CustomKit fromPlayerInventory(Player player, Component kitName, PlayerKit kitSlot)
    {
        List<SerializableItem> items = new ArrayList<>();
        int slot = 0;
        int cardSlot = 40;
        for (ItemStack itemStack : player.getInventory())
        {
            if (itemStack != null) {
                // if this item is the card, save the slot instead and disregard the item itself.
                if (PlayerKit.CARD_ITEM.isCompareKeyEqual(itemStack)) {
                    cardSlot = slot;
                }
                else {
                    items.add(new SerializableItem(slot, itemStack));
                }
            }
            slot += 1;
        }

        return new CustomKit(kitName, kitSlot, items, cardSlot);
    }
}
