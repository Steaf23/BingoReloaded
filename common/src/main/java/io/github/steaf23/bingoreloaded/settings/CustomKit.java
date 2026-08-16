package io.github.steaf23.bingoreloaded.settings;

import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorageSerializer;
import io.github.steaf23.bingoreloaded.lib.item.SerializableItem;
import io.github.steaf23.bingoreloaded.lib.util.ComponentUtils;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

public record CustomKit(Component name, PlayerKit slot, List<SerializableItem> items, int cardSlot) {

	public static final DataStorageSerializer<CustomKit> SERIALIZER = DataStorageSerializer.of(CustomKit.class,
			(storage, value) -> {
				storage.setByte("card_slot", (byte) value.cardSlot());
				storage.setString("name", ComponentUtils.MINI_BUILDER.serialize(value.name()));
				storage.setByte("kit_id", slotFromKit(value.slot()));
				storage.setSerializableList("items", SerializableItem.SERIALIZER, value.items());
			}, storage -> {
				return new CustomKit(ComponentUtils.MINI_BUILDER.deserialize(storage.getString("name", "")),
						kitFromSlot(storage.getByte("kit_id", (byte) 0)),
						storage.getSerializableList("items", SerializableItem.SERIALIZER),
						storage.getByte("card_slot", (byte) 40)); //off-hand slot
			});

	private static PlayerKit kitFromSlot(byte slot) throws IllegalStateException {
		return switch (slot) {
			case 1 -> PlayerKit.CUSTOM_1;
			case 2 -> PlayerKit.CUSTOM_2;
			case 3 -> PlayerKit.CUSTOM_3;
			case 4 -> PlayerKit.CUSTOM_4;
			case 5 -> PlayerKit.CUSTOM_5;
			default -> throw new IllegalStateException("Unexpected value: " + slot);
		};
	}

	private static byte slotFromKit(PlayerKit kit) throws IllegalStateException {
		return switch (kit) {
			case CUSTOM_1 -> 1;
			case CUSTOM_2 -> 2;
			case CUSTOM_3 -> 3;
			case CUSTOM_4 -> 4;
			case CUSTOM_5 -> 5;
			default -> throw new IllegalStateException("Unexpected kit slot for kit" + kit.getDisplayName());
		};
	}

	public static CustomKit fromPlayerInventory(PlayerHandle player, Component kitName, PlayerKit kitSlot) {
		List<SerializableItem> items = new ArrayList<>();
		int slot = 0;
		int cardSlot = 40;
		for (StackHandle itemStack : player.inventory().contents()) {
			if (itemStack != null && !itemStack.type().isAir()) {
				// if this item is the card, save the slot instead and disregard the item itself.
				if (PlayerKit.CARD_ITEM.isCompareKeyEqual(itemStack)) {
					cardSlot = slot;
				} else {
					items.add(new SerializableItem(slot, itemStack));
				}
			}
			slot += 1;
		}

		return new CustomKit(kitName, kitSlot, items, cardSlot);
	}
}
