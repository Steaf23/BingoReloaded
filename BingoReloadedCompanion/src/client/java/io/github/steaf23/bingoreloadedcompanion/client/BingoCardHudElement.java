package io.github.steaf23.bingoreloadedcompanion.client;

import io.github.steaf23.bingoreloadedcompanion.card.BingoCard;
import io.github.steaf23.bingoreloadedcompanion.card.Task;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class BingoCardHudElement implements HudElement {

	private static final Identifier GAMEMODE_LOGOS = Identifier.of("bingoreloadedcompanion:textures/gui/gamemode_logos.png");

	private static final Integer ADVANCEMENT_COLOR = Formatting.GREEN.getColorValue();
	private static final Integer STATISTIC_COLOR = Formatting.LIGHT_PURPLE.getColorValue();

	private @Nullable BingoCard card;

	public void setCard(@Nullable BingoCard card) {
		this.card = card;
	}

	@Override
	public void render(DrawContext drawContext, RenderTickCounter renderTickCounter) {
		if (card == null || card.tasks().isEmpty()) {
			return;
		}

		int spacing = 3;
		int startOffset = 18;
		int itemSize = 16;

		int taskIdx = 0;
		for (int y = 0; y < card.size(); y++) {
			for (int x = 0; x < card.size(); x++) {
				int xStart = startOffset + spacing * x + x * itemSize;
				int yStart = spacing + spacing * y + y * itemSize;

				if (card.size() == 3) {
					xStart += spacing + itemSize;
					yStart += spacing + itemSize;
				}

				int bannerStartX = xStart - 1;
				int bannerStartY = yStart + (itemSize / 4 * 3);

				Task task = card.tasks().get(taskIdx);

				Task.TaskCompletion completion = task.completion();
				if (completion.completed()) {
					drawContext.fill(xStart - 1, yStart - 1, xStart + itemSize + 1, yStart + itemSize + 1, addAlphaToColor(completion.teamColor(), 200));
					drawContext.drawBorder(xStart - 1, yStart - 1, itemSize + 2, itemSize + 2, addAlphaToColor(completion.teamColor(), 255));
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

				TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
				String text = String.valueOf(task.requiredAmount());
				int textWidth = textRenderer.getWidth(text);
				int textHeight = textRenderer.fontHeight;
				if (task.requiredAmount() > 1) {
					drawContext.drawText(textRenderer, text, xStart + itemSize - textWidth + 1, yStart + itemSize - textHeight + 2, 0xFFFFFFFF, true);
				}
				taskIdx++;
			}
		}

		int textureIndex = card.mode().getIndex();
		int gamemodeBannerSizeX = 128;
		int gamemodeBannerSizeY = 32;
		int gamemodeStartX = 0;
		int gamemodeStartY = spacing + spacing * 5 + itemSize * 5;
		drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, GAMEMODE_LOGOS, gamemodeStartX, gamemodeStartY,
				0, textureIndex * gamemodeBannerSizeY, gamemodeBannerSizeX, gamemodeBannerSizeY,
				gamemodeBannerSizeX, gamemodeBannerSizeY, 128, 128);

	}

	private int addAlphaToColor(int color, int alpha) {
		alpha = alpha << 24;
		return alpha | color;
	}
}
