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
    public void toDataStorage(@NotNull DataStorage storage, @NotNull BingoSettings value) {
        storage.setString("card", value.card());
        storage.setString("mode", value.mode().getDataName());
        storage.setInt("size", value.size().size);
        storage.setInt("seed", value.seed());
        storage.setString("kit", value.kit().configName);
        storage.setList("effects", TagDataType.STRING, enumSetToList(value.effects()));
        storage.setInt("team_size", value.maxTeamSize());
        storage.setInt("duration", value.countdownDuration());
        storage.setNamespacedKey("countdown_type", value.countdownType().key()); //TODO: fix this??
        storage.setInt("hotswap_goal", value.hotswapGoal());
        storage.setBoolean("expire_hotswap_tasks", value.expireHotswapTasks());
        storage.setInt("complete_goal", value.completeGoal());
        storage.setBoolean("different_card_per_team", value.differentCardPerTeam());
    }

    @Override
    public @Nullable BingoSettings fromDataStorage(@NotNull DataStorage storage) {
        CardSize size = CardSize.fromWidth(storage.getInt("size", 5));
        return new BingoSettings(
                storage.getString("card", ""),
                BingoGamemode.fromDataString(storage.getString("mode", "")),
                size,
                storage.getInt("seed", 0),
                PlayerKit.fromConfig(storage.getString("kit", "")),
                enumSetFromList(EffectOptionFlags.class, storage.getList("effects", TagDataType.STRING)),
                storage.getInt("team_size", 1),
                BingoSettings.CountdownType.fromNamespace(storage.getNamespacedKey("countdown_type")),
                storage.getInt("duration", 0),
                storage.getInt("hotswap_goal", 10),
                storage.getBoolean("expire_hotswap_tasks", true),
                storage.getInt("complete_goal", size.fullCardSize),
                storage.getBoolean("different_card_per_team", false)
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
