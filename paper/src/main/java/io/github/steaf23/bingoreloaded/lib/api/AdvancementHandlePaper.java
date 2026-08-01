package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.papermc.paper.advancement.PaperAdvancementDisplay;
import io.papermc.paper.adventure.AdventureComponent;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.advancement.Advancement;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AdvancementHandlePaper implements AdvancementHandle {

	private final Advancement advancement;

	public AdvancementHandlePaper(Advancement advancement) {
		this.advancement = advancement;
	}

	@Override
	public boolean hasDisplay() {
		return advancement.getDisplay() != null;
	}

	@Override
	public ItemType displayIcon() {
		if (advancement.getDisplay() == null) {
			return ItemType.AIR;
		}
		return new ItemTypePaper(advancement.getDisplay().icon().getType());
	}

	@Override
	public Component displayName() {
		if (advancement.getDisplay() == null) {
			return Component.text(advancement.key().asString());
		}
		// For some reason paper decides to add extra formatting here that we don't want (specifically adding square brackets and making it green).
		// This will allow bingo to apply its own advancement task formatting on the title/name component.
		return PaperAdventure.asAdventure(((PaperAdvancementDisplay) advancement.getDisplay()).handle().getTitle());
	}

	@Override
	public Component description() {
		if (advancement.getDisplay() == null) {
			return Component.empty();
		}
		return advancement.getDisplay().description();
	}

	@Override
	public @NotNull Key key() {
		return advancement.key();
	}

	public Advancement handle() {
		return advancement;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AdvancementHandle other) {
			return key().equals(other.key());
		}

		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(advancement);
	}
}
