package io.github.steaf23.bingoreloaded.lib.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultilineComponent
{
    private TextColor color;
    private final List<TextDecoration> decoration;
    private final Component[] components;

    private MultilineComponent(Component... components) {
        this.color = null;
        this.decoration = new ArrayList<>();
        this.components = components;
    }

    public static MultilineComponent of(Component... components) {
        return new MultilineComponent(components);
    }

    public static Component[] from(TextColor color, List<TextDecoration> decoration, Component... components) {
        return new MultilineComponent(components).color(color).decorate(decoration.toArray(TextDecoration[]::new)).build();
    }

    public static Component[] from(TextColor color, TextDecoration decoration, Component... components) {
        return new MultilineComponent(components).color(color).decorate(decoration).build();
    }

    public MultilineComponent color(TextColor color) {
        this.color = color;
        return this;
    }

    public MultilineComponent decorate(TextDecoration... decorations) {
        decoration.addAll(Arrays.stream(decorations).toList());
        return this;
    }

    public Component[] build() {
        return Arrays.stream(this.components)
                .map(c -> c.color(this.color).decorate(this.decoration.toArray(TextDecoration[]::new)))
                .toArray(Component[]::new);
    }
}
