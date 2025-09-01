package io.github.steaf23.bingoreloadedcompanion.client;

import io.github.steaf23.bingoreloadedcompanion.card.BingoCard;
import io.github.steaf23.bingoreloadedcompanion.card.Task;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import org.jetbrains.annotations.Nullable;

public class BingoCardHudElement implements HudElement {

	private @Nullable BingoCard card;

	public void setCard(@Nullable BingoCard card) {
		this.card = card;
	}

	@Override
	public void render(DrawContext drawContext, RenderTickCounter renderTickCounter) {
		if (card == null) {
			return;
		}

		int spacing = 2;
		int itemSize = 16;

		int taskIdx = 0;
		for (int y = 0; y < card.size(); y++) {
			for (int x = 0; x < card.size(); x++) {
				int xStart = spacing + spacing * x + x * itemSize;
				int yStart = spacing + spacing * y + y * itemSize;

				Task task = card.tasks().get(taskIdx);

				if (task.completed()) {
					drawContext.fill(xStart, yStart, xStart + itemSize, yStart + itemSize, 0x8800FF00);
				} else {
					drawContext.fill(xStart, yStart, xStart + itemSize, yStart + itemSize, 0x88000000);
				}

				drawContext.drawItem(task.itemType().getDefaultStack(), xStart, yStart);
				taskIdx++;
			}
		}
	}
}
