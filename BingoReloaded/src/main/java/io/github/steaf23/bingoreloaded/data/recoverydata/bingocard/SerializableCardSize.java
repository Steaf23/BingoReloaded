package io.github.steaf23.bingoreloaded.data.recoverydata.bingocard;

import io.github.steaf23.bingoreloaded.cards.CardSize;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

@SerializableAs("CardSize")
public record SerializableCardSize(
        String name
) implements ConfigurationSerializable {
    private static final String NAME_ID = "name";

    public SerializableCardSize(CardSize cardSize) {
        this(cardSize.name());
    }

    public static SerializableCardSize deserialize(Map<String, Object> data)
    {
        return new SerializableCardSize((String) data.getOrDefault(NAME_ID, CardSize.X5.name()));
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put(NAME_ID, name);

        return data;
    }

    public CardSize toCardSize() {
        return CardSize.valueOf(name);
    }
}
