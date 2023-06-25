package io.github.steaf23.bingoreloaded.settings;

import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.data.helper.YmlDataManager;
import io.github.steaf23.bingoreloaded.gui.EffectOptionFlags;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SerializableAs("BingoSettings")
public record BingoSettings(String card,
                            BingoGamemode mode,
                            CardSize size,
                            int seed,
                            PlayerKit kit,
                            EnumSet<EffectOptionFlags> effects,
                            int maxTeamSize,
                            boolean enableCountdown,
                            int countdownDuration) implements ConfigurationSerializable
{
    public static BingoSettings getDefaultSettings()
    {
        return new BingoSettings(
                "default_card",
                BingoGamemode.REGULAR,
                CardSize.X5,
                0,
                PlayerKit.OVERPOWERED,
                PlayerKit.OVERPOWERED.defaultEffects,
                5,
                false,
                20);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize()
    {
        return new HashMap<>(){{
            put("card", card);
            put("mode", mode.getDataName());
            put("size", size.size);
            put("seed", seed);
            put("kit", kit.configName);
            put("effects", YmlDataManager.enumSetToList(effects));
            put("team_size", maxTeamSize);
            put("duration", countdownDuration);
            put("countdown", enableCountdown);
        }};
    }

    public static BingoSettings deserialize(Map<String, Object> data)
    {
        return new BingoSettings(
                (String) data.get("card"),
                BingoGamemode.fromDataString((String) data.get("mode")),
                CardSize.fromWidth((int) data.get("size")),
                (int) data.get("seed"),
                PlayerKit.fromConfig((String) data.get("kit")),
                YmlDataManager.enumSetFromList(EffectOptionFlags.class, (List<String>) data.get("effects")),
                (int) data.get("team_size"),
                (boolean) data.get("countdown"),
                (int) data.get("duration")
        );
    }
};
