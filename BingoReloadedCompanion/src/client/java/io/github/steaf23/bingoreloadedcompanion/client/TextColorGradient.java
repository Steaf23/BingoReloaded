package io.github.steaf23.bingoreloadedcompanion.client;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class used to create and sample from a gradient, returning adventure TextColors
 */
public class TextColorGradient
{
	Map<TextColor, Float> colors;

	public TextColorGradient() {
		colors = new HashMap<>();
	}

	/**
	 * Adds a color to the gradient at the given position, where the position is a value from 0 to 1.
	 * Moves color to position if it is already in the gradient, without adding a new color.
	 * Note: adding 2 colors with the same value will produce undefined results!
	 */
	public TextColorGradient addColor(@NotNull TextColor color, float position) throws IllegalArgumentException {
		if (position < 0.0 || position > 1.0) {
			throw new IllegalArgumentException("position must be between 0.0 and 1.0.");
		}
		colors.put(color, position);
		return this;
	}

	/**
	 * @return false if the gradient does not contain the given color
	 */
	public boolean removeColor(@NotNull TextColor color) {
		return colors.remove(color) != null;
	}

	/**
	 * Samples the gradient, returning an interpolated color based on the given position, between 0 and 1
	 * @return interpolated color at float position between 0 and 1
	 */
	public TextColor sample(float position) {
		if (colors.isEmpty()) {
			return TextColor.fromLegacyFormat(ChatFormatting.WHITE);
		}

		if (colors.size() == 1) {
			return colors.keySet().stream().toList().getFirst();
		}

		List<TextColor> sortedColors = colors.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByValue())
				.map(Map.Entry::getKey)
				.toList();

		// find 2 colors closest to the actual position
		TextColor leftColor = sortedColors.get(0);
		TextColor rightColor = sortedColors.get(1);
		for (int i = 0; i < sortedColors.size() - 1; i++) {
			if (position >= colors.get(sortedColors.get(i)) && position <= colors.get(sortedColors.get(i + 1))) {
				leftColor = sortedColors.get(i);
				rightColor = sortedColors.get(i + 1);
			}
		}

		float leftPosition = colors.get(leftColor);
		float rightPosition = colors.get(rightColor);

		float lerpValue = ExtraMath.map(position, leftPosition, rightPosition, 0.0f, 1.0f);
		return lerpChatColor(leftColor, rightColor, lerpValue);
	}

	static public TextColor lerpChatColor(@NotNull TextColor left, @NotNull TextColor right, float value) {
		int leftRgb = left.getValue();
		int rightRgb = right.getValue();
		int red = (int)ExtraMath.lerp((leftRgb & 0xFF0000) >> 16, (rightRgb & 0xFF0000) >> 16, value);
		int green = (int)ExtraMath.lerp((leftRgb & 0x00FF00) >> 8, (rightRgb & 0x00FF00) >> 8, value);
		int blue = (int)ExtraMath.lerp((leftRgb & 0x0000FF), (rightRgb & 0x0000FF), value);
		return TextColor.fromRgb(new Color(Math.clamp(red, 0, 255), Math.clamp(green, 0, 255), Math.clamp(blue, 0, 255)).getRGB());
	}
}