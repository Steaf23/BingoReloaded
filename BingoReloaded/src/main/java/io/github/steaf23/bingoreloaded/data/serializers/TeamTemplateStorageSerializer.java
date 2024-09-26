package io.github.steaf23.bingoreloaded.data.serializers;

import io.github.steaf23.bingoreloaded.data.TeamData;
import io.github.steaf23.bingoreloaded.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.data.core.tag.DataStorageSerializer;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TeamTemplateStorageSerializer implements DataStorageSerializer<TeamData.TeamTemplate>
{
    @Override
    public void toDataStorage(@NotNull DataStorage storage, TeamData.@NotNull TeamTemplate value) {
        storage.setString("name", value.stringName());
        storage.setString("color", value.color().asHexString());
    }

    @Override
    public @Nullable TeamData.TeamTemplate fromDataStorage(@NotNull DataStorage storage) {
        return new TeamData.TeamTemplate(
                storage.getString("name", ""),
                TextColor.fromHexString(storage.getString("color", "#808080"))
        );
    }
}
