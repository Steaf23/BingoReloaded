package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.lib.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.lib.util.ComponentUtils;
import io.github.steaf23.bingoreloaded.lib.util.StringAdditions;
import io.github.steaf23.bingoreloaded.lib.util.BlockColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamData {
    /**
     * @param stringName name to use for the team, which is retrieved using minimessage deserialization
     * @param color
     */
    public record TeamTemplate(String stringName, TextColor color)
    {
        public Component nameComponent() {
            return ComponentUtils.MINI_BUILDER.deserialize(stringName);
        }
    }

    private final DataAccessor data = BingoReloaded.getDataAccessor("data/teams");

    public Map<String, TeamTemplate> getTeams() {
        Map<String, TeamTemplate> teams = new HashMap<>();
        for (String key : data.getKeys()) {
            teams.put(key, data.getSerializable(key, TeamTemplate.class));
        }
        return teams;
    }

    public void addTeam(@NotNull String key, String name, TextColor color) {
        if (key.isEmpty()) {
            key = getNewTeamId();
        }
        data.setSerializable(key, TeamTemplate.class, new TeamTemplate(name, color));
        data.saveChanges();
    }

    public void addTeam(@NotNull String key, TeamTemplate template) {
        addTeam(key, template.stringName(), template.color());
    }

    public TeamTemplate getTeam(String key, TeamTemplate def) {
        return data.getSerializable(key, TeamTemplate.class, def);
    }

    public void removeTeam(String key) {
        data.erase(key);
        data.saveChanges();
    }

    public void reset() {
        for (String team : getTeams().keySet()) {
            data.erase(team);
        }
        data.saveChanges();

        for (BlockColor col : BlockColor.values()) {
            addTeam(col.name, StringAdditions.capitalize(col.name), col.textColor);
        }
    }

    // TODO: find cheaper way to generate valid team id
    public String getNewTeamId() {
        int id = 0;
        List<String> keys = data.getKeys().stream().sorted().toList();
        while (keys.contains(String.valueOf(id))) {
            id += 1;
        }

        return String.valueOf(id);
    }
}
