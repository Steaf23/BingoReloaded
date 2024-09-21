package io.github.steaf23.bingoreloaded.data.serializers;

import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.data.core.tag.DataStorageSerializer;
import io.github.steaf23.bingoreloaded.data.core.tag.TagDataType;
import io.github.steaf23.bingoreloaded.gui.inventory.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class BingoSettingsStorageSerializer implements DataStorageSerializer<BingoSettings>
{
    @Override
    public void toDataStorage(@NotNull DataStorage storage, BingoSettings value) {
        storage.setString("card", value.card());
        storage.setString("mode", value.mode().getDataName());
        storage.setInt("size", value.size().size);
        storage.setInt("seed", value.seed());
        storage.setString("kit", value.kit().configName);
        storage.setList("effects", TagDataType.STRING, enumSetToList(value.effects()));
        storage.setInt("team_size", value.maxTeamSize());
        storage.setInt("duration", value.countdownDuration());
        storage.setBoolean("countdown", value.enableCountdown());
        storage.setInt("hotswap_goal", value.hotswapGoal());
    }

    @Override
    public @Nullable BingoSettings fromDataStorage(@NotNull DataStorage storage) {
        return new BingoSettings(
                storage.getString("card", ""),
                BingoGamemode.fromDataString(storage.getString("mode", "")),
                CardSize.fromWidth(storage.getInt("size", 5)),
                storage.getInt("seed", 0),
                PlayerKit.fromConfig(storage.getString("kit", "")),
                enumSetFromList(EffectOptionFlags.class, storage.getList("effects", TagDataType.STRING)),
                storage.getInt("team_size", 1),
                storage.getBoolean("countdown", false),
                storage.getInt("duration", 0),
                storage.getInt("hotswap_goal", 10)
        );
    }

    private static List<String> enumSetToList(EnumSet<? extends Enum<?>> set)
    {
        List<String> list = new ArrayList<>();
        set.forEach(entry -> list.add(entry.name()));
        return list;
    }

    private static <E extends Enum<E>> EnumSet<E> enumSetFromList(Class<E> enumType, List<String> list)
    {
        EnumSet<E> result = EnumSet.noneOf(enumType);
        list.forEach(entry -> result.add(Enum.<E>valueOf(enumType, entry)));
        return result;
    }
}
