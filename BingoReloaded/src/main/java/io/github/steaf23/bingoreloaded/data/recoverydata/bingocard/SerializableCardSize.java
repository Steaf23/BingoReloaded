package io.github.steaf23.bingoreloaded.data.recoverydata.bingocard;

import io.github.steaf23.bingoreloaded.cards.CardSize;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("CardSize")
public class SerializableCardSize implements ConfigurationSerializable {
    private String name;
    private final String NAME_ID = "name";

    public SerializableCardSize(CardSize cardSize) {
        name = cardSize.name();
    }

    public SerializableCardSize(Map<String, Object> data) {
        name = (String) data.getOrDefault(NAME_ID, CardSize.X5.name());
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
