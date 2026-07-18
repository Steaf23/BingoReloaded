package io.github.steaf23.bingoreloaded.data.serializers;

import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorageSerializer;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagDataType;
import io.github.steaf23.bingoreloaded.player.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.bingoreloaded.settings.gamemode.BingoGamemodes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class BingoSettingsStorageSerializer implements DataStorageSerializer<BingoSettings>
{
    @Override
    public void toDataStorage(@NotNull DataStorage storage, @NotNull BingoSettings value) {
        storage.setString("card", value.cardName());
        storage.setString("mode", value.mode().configName());
        storage.setInt("size", value.size().size);
        storage.setInt("seed", value.seed());
        storage.setString("kit", value.kit().configName);
        storage.setList("effects", TagDataType.STRING, enumSetToList(value.effects()));
        storage.setInt("team_size", value.maxTeamSize());
        storage.setInt("team_count", value.maxTeamCount());
        storage.setInt("duration", value.countdownDuration());
        storage.setNamespacedKey("countdown_type", value.countdownType().key());
        storage.setInt("hotswap_goal", value.hotswapGoal());
        storage.setBoolean("expire_hotswap_tasks", value.expireHotswapTasks());
        storage.setInt("complete_goal", value.completeGoal());
        storage.setBoolean("different_card_per_team", value.differentCardPerTeam());
        storage.setInt("blitz_start_duration", value.blitzStartDuration());
        storage.setInt("blitz_bonus_duration", value.blitzBonusDuration());
        storage.setInt("blitz_recovery_delay", value.blitzRecoveryDelay());
    }

    @Override
    public @Nullable BingoSettings fromDataStorage(@NotNull DataStorage storage) {
        CardSize size = CardSize.fromWidth(storage.getInt("size", 5));

        return new BingoSettings(
                storage.getString("card", ""),
                BingoGamemodes.fromDataString(storage.getString("mode", "")),
                size,
                storage.getInt("seed", 0),
                PlayerKit.fromConfig(storage.getString("kit", "")),
                enumSetFromList(EffectOptionFlags.class, storage.getList("effects", TagDataType.STRING)),
                storage.getInt("team_size", 1),
                storage.getInt("team_count", 64),
                BingoSettings.CountdownType.fromNamespace(storage.getNamespacedKey("countdown_type")),
                storage.getInt("duration", 0),
                storage.getInt("hotswap_goal", 10),
                storage.getBoolean("expire_hotswap_tasks", true),
                storage.getInt("complete_goal", size.fullCardSize),
                storage.getBoolean("different_card_per_team", false),
                storage.getInt("blitz_start_duration", 4 * 6),// blitz counts in 10 seconds, so 4 * 6 * 10 == 4 minutes.
                storage.getInt("blitz_bonus_duration", 1 * 6),// blitz counts in 10 seconds, so 1 * 6 * 10 == 1 minute.
                storage.getInt("blitz_recovery_delay", 4)
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
