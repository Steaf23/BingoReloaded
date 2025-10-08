package io.github.steaf23.bingoreloadedcompanion.card.taskslot;

import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public record AdvancementTask(AdvancementEntry advancement, boolean counted) implements TaskSlot {

	@Override
	public Identifier id() {
		return advancement.id();
	}

	@Override
	public Item item() {
		if (advancement.value().display().isPresent()) {
			return advancement.value().display().get().getIcon().getItem();
		}
		return Items.BEDROCK;
	}

	@Override
	public Text name() {
		return advancement.value().name().orElse(Text.of("UNNAMED ADVANCEMENT"));
	}

	@Override
	public int completeCount() {
		return counted ? 1 : 0;
	}

	@Override
	public TaskSlot copyWithCount(int newCount) {
		return new AdvancementTask(advancement, newCount > 0);
	}
}
