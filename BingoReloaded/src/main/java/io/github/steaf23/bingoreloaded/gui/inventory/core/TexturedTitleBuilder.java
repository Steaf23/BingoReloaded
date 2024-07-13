package io.github.steaf23.bingoreloaded.gui.inventory.core;

import io.github.steaf23.bingoreloaded.data.CustomTextureData;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.Nullable;

import java.io.Console;

public class TexturedTitleBuilder
{
    private final TextComponent.Builder componentBuilder;

    private int spaceCounter;

    public TexturedTitleBuilder() {
        this.componentBuilder = Component.text().color(NamedTextColor.WHITE);
    }

    public TexturedTitleBuilder addSpace(int amount) {
        spaceCounter += amount;
        componentBuilder.append(Component.translatable("space." + amount));
        return this;
    }

    public TexturedTitleBuilder addTexture(CustomTextureData.Texture texture, @Nullable TextColor modulate) {
        spaceCounter += texture.textureEnd() + 2;
        componentBuilder.append(Component.text(texture.character()).color(modulate));
        return this;
    }

    public TexturedTitleBuilder addTexture(CustomTextureData.Texture texture) {
        return addTexture(texture, null);
    }

    public TexturedTitleBuilder resetSpace() {
        addSpace(-spaceCounter);
        return this;
    }

    public Component build() {
        return componentBuilder.build();
    }

    public Component resetAndBuild() {
        resetSpace();
        return build();
    }
}
