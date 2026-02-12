package io.github.steaf23.bingoreloaded.gui;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.steaf23.bingoreloaded.cards.TaskCard;
import io.github.steaf23.bingoreloaded.lib.HytaleTaskItemConverter;
import org.jetbrains.annotations.NotNull;

public class BingoCardUIHud extends CustomUIHud {

	TaskCard card = null;

	public BingoCardUIHud(@NotNull PlayerRef playerRef) {
		super(playerRef);
	}

	@Override
	protected void build(@NotNull UICommandBuilder uiCommandBuilder) {
		uiCommandBuilder.append("bingo_card_hud.ui");
	}

	public void setCard(TaskCard card) {
		this.card = card;

		UICommandBuilder builder = new UICommandBuilder();

		if (card != null) {
			builder.set("#Tasks.ItemStacks", HytaleTaskItemConverter.extractAsItems(card));
			builder.set("#Tasks.SlotsPerRow", card.size.size);
		}

		update(false, builder);
	}
}
