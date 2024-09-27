package io.github.steaf23.playerdisplay.util;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class used to create and sample from a gradient, returning bungee.api.ChatColors
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
            return NamedTextColor.WHITE;
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
        int red = (int)ExtraMath.lerp(left.red(), right.red(), value);
        int green = (int)ExtraMath.lerp(left.green(), right.green(), value);
        int blue = (int)ExtraMath.lerp(left.blue(), right.blue(), value);
        return TextColor.color(Math.clamp(red, 0, 255), Math.clamp(green, 0, 255), Math.clamp(blue, 0, 255));
    }
}
