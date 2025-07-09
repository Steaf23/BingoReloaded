package io.github.steaf23.bingoreloaded.lib.api;

import net.kyori.adventure.key.Key;
import org.bukkit.advancement.Advancement;
import org.jetbrains.annotations.NotNull;

public class AdvancementHandlePaper implements AdvancementHandle {

	private final Advancement advancement;

	public AdvancementHandlePaper(Advancement advancement) {
		this.advancement = advancement;
	}

	@Override
	public ItemType displayIcon() {
		if (advancement.getDisplay() == null) {
			return ItemType.AIR;
		}
		return new ItemTypePaper(advancement.getDisplay().icon().getType());
	}

	@Override
	public @NotNull Key key() {
		return advancement.key();
	}

	public Advancement handle() {
		return advancement;
	}
}
