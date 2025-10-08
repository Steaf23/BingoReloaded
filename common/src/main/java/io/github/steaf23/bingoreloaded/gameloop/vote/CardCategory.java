package io.github.steaf23.bingoreloaded.gameloop.vote;

import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CardCategory extends VoteCategory<String>
{
    public CardCategory() {
        super("card", BingoMessage.OPTIONS_CARD.asPhrase());
    }

    @Override
    @NotNull
    List<String> getValidValues() {
        return new BingoCardData().getCardNames().stream().toList();
    }

    @Override
    String createResultForValue(String value) {
        return value;
    }

    @Override
    public Component getValueComponent(String value) {
        return Component.text(value).color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.ITALIC);
    }
}
