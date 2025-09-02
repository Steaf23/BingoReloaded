package io.github.steaf23.bingoreloadedcompanion.client;

import io.github.steaf23.bingoreloadedcompanion.card.BingoCard;
import io.github.steaf23.bingoreloadedcompanion.card.Task;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class BingoCardHudElement implements HudElement {

	private static final Integer ADVANCEMENT_COLOR = Formatting.GREEN.getColorValue();
	private static final Integer STATISTIC_COLOR = Formatting.LIGHT_PURPLE.getColorValue();

	private @Nullable BingoCard card;

	public void setCard(@Nullable BingoCard card) {
		this.card = card;
	}

	@Override
	public void render(DrawContext drawContext, RenderTickCounter renderTickCounter) {
		if (card == null) {
			return;
		}

		int spacing = 3;
		int itemSize = 16;

		int taskIdx = 0;
		for (int y = 0; y < card.size(); y++) {
			for (int x = 0; x < card.size(); x++) {
				int xStart = spacing + spacing * x + x * itemSize;
				int yStart = spacing + spacing * y + y * itemSize;

				int bannerStartX = xStart - 1;
				int bannerStartY = yStart + (itemSize / 4 * 3);

				Task task = card.tasks().get(taskIdx);

				Task.TaskCompletion completion = task.completion();
				if (completion.completed()) {
					drawContext.fill(xStart - 1, yStart - 1, xStart + itemSize + 1, yStart + itemSize + 1, addAlphaToColor(completion.teamColor(), 200));
				} else {
					drawContext.fill(xStart, yStart, xStart + itemSize, yStart + itemSize, 0x88000000);
				}

				drawContext.drawItem(task.itemType().getDefaultStack(), xStart, yStart);

				String taskType = task.taskType().toString();
				int bannerColor = switch (taskType) {
					case "bingoreloaded:advancement" -> addAlphaToColor(ADVANCEMENT_COLOR, 255);
					case "bingoreloaded:statistic" -> addAlphaToColor(STATISTIC_COLOR, 255);
					default -> 0;
				};

				drawContext.fill(bannerStartX, bannerStartY, bannerStartX + itemSize + 2, bannerStartY + itemSize / 4, bannerColor);

				taskIdx++;
			}
		}
	}

	private int addAlphaToColor(int color, int alpha) {
		alpha = alpha << 24;
		return alpha | color;
	}
}
