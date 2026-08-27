package io.github.steaf23.bingoreloadedcompanion.client.util;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

public class ScreenHelper {

	public static final Style INVENTORY_STYLE = Style.EMPTY.withColor(0xff404040).withShadowColor(0);

	public static final Identifier GUI_BACKGROUND = Identifier.parse("bingoreloadedcompanion:inventory_background");
	public static final Identifier INNER_GUI_BACKGROUND = Identifier.parse("bingoreloadedcompanion:inner_inventory_background");
	public static final Identifier SCROLLABLE_BACKGROUND = Identifier.parse("bingoreloadedcompanion:scrollable_background");
	public static final Identifier ROUNDED_RECT_BACKGROUND = Identifier.parse("bingoreloadedcompanion:rounded_rect_background");


	public static void extractInventoryBackground(GuiGraphicsExtractor graphics, ScreenRectangle rect) {
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, GUI_BACKGROUND, rect.left() - 4, rect.top() - 4, rect.width() + 8, rect.height() + 8);
	}

	public static void extractScrollAreaBackground(GuiGraphicsExtractor graphics, ScreenRectangle rect) {
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLLABLE_BACKGROUND, rect.left(), rect.top() - 1, rect.width() + 1, rect.height() + 2);
	}

	public static void extractInnerInventoryBackground(GuiGraphicsExtractor graphics, ScreenRectangle rect) {
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, INNER_GUI_BACKGROUND, rect.left() - 3, rect.top() - 3, rect.width() + 6, rect.height() + 6);
	}

	public static void extractRoundedRectBackground(GuiGraphicsExtractor graphics, ScreenRectangle rect, int color) {
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ROUNDED_RECT_BACKGROUND, rect.left() - 1, rect.top() - 1, rect.width() + 2, rect.height() + 2, color);
	}

	public static void extractRoundedRectBackground(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color) {
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ROUNDED_RECT_BACKGROUND, x - 1, y - 1, width + 2, height + 2, color);
	}

	public static boolean isPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY) {
		return pointX >= (double) (x - 1) && pointX < (double) (x + width + 1) && pointY >= (double) (y - 1) && pointY < (double) (y + height + 1);
	}

	public static int addAlphaToColor(int color, int alpha) {
		alpha = alpha << 24;
		return alpha | color;
	}

}
