package io.github.steaf23.bingoreloadedcompanion.card.taskslot;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

public record ItemTask(Identifier id, int count) implements TaskSlot {

	public ItemTask(Identifier id, int count) {
		this.id = id;
		this.count = Math.clamp(count, 0, 64);
	}

	public Item item() {
		return Registries.ITEM.get(id);
	}

	public Text name() {
		return Text.of(count + "x ").copy().withColor(Colors.LIGHT_YELLOW)
				.append(Text.translatable(item().getTranslationKey()));
	}

	@Override
	public int completeCount() {
		return 0;
	}

	@Override
	public ItemTask copyWithCount(int newCount) {
		return new ItemTask(id, newCount);
	}
}
