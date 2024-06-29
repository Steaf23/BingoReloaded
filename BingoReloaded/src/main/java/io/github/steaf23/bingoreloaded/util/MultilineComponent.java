package io.github.steaf23.bingoreloaded.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MultilineComponent implements Iterable<Component>, ComponentLike
{
    private final List<Component> components;

    private final NamedTextColor color;
    private final TextDecoration[] decorations;

    private MultilineComponent(NamedTextColor color, TextDecoration... decorations) {
        this.color = color;
        this.decorations = decorations;

        this.components = new ArrayList<>();
    }

    public void add(Component component) {
        components.add(component.color(color).decorate(decorations));
    }

    @Override
    public @NotNull Iterator<Component> iterator() {
        return components.iterator();
    }

    @Override
    public @NotNull Component asComponent() {
        return Component.text().append(components).build();
    }
}
