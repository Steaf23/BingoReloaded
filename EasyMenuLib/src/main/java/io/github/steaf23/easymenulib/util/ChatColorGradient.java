package io.github.steaf23.easymenulib.util;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;


import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class used to create and sample from a gradient, returning bungee.api.ChatColors
 */
public class ChatColorGradient
{
    Map<ChatColor, Float> colors;

    public ChatColorGradient() {
        colors = new HashMap<>();
    }

    /**
     * Adds a color to the gradient at the given position, where the position is a value from 0 to 1.
     * Moves color to position if it is already in the gradient, without adding a new color.
     * Note: adding 2 colors with the same value will produce undefined results!
     * @param position
     * @param color
     */
    public ChatColorGradient addColor(@NotNull ChatColor color, float position) throws IllegalArgumentException {
        if (position < 0.0 || position > 1.0) {
            throw new IllegalArgumentException("position must be between 0.0 and 1.0.");
        }
        colors.put(color, position);

        return this;
    }

    /**
     * @param color
     * @return false if the gradient does not contain the given color
     */
    public boolean removeColor(@NotNull ChatColor color) {
        return colors.remove(color) != null;
    }


    /**
     * Samples the gradient, returning an interpolated color based on the given position, between 0 and 1
     * @param position
     * @return
     */
    public ChatColor sample(float position) {
        if (colors.size() == 0) {
            return ChatColor.WHITE;
        }

        if (colors.size() == 1) {
            return colors.keySet().stream().toList().get(0);
        }

        List<ChatColor> sortedColors = colors.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // find 2 colors closest to the actual position
        ChatColor leftColor = sortedColors.get(0);
        ChatColor rightColor = sortedColors.get(1);
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

    static public ChatColor lerpChatColor(@NotNull ChatColor left, @NotNull ChatColor right, float value) {
        int red = (int)ExtraMath.lerp(left.getColor().getRed(), right.getColor().getRed(), value);
        int green = (int)ExtraMath.lerp(left.getColor().getGreen(), right.getColor().getGreen(), value);
        int blue = (int)ExtraMath.lerp(left.getColor().getBlue(), right.getColor().getBlue(), value);
        return ChatColor.of(new Color(ExtraMath.clamped(red, 0, 255), ExtraMath.clamped(green, 0, 255), ExtraMath.clamped(blue, 0, 255)));
    }
}
