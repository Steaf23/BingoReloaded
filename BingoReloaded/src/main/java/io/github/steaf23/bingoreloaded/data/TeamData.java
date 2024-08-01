package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.helper.YmlDataManager;
import io.github.steaf23.playerdisplay.PlayerDisplay;
import io.github.steaf23.playerdisplay.util.BlockColor;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.apache.commons.text.WordUtils;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamData {
    /**
     * @param stringName name to use for the team, which is retrieved using minimessage deserialization
     * @param color
     */
    @SerializableAs("TeamTemplate")
    public record TeamTemplate(String stringName, TextColor color) implements ConfigurationSerializable {
        @NotNull
        @Override
        public Map<String, Object> serialize() {
            Map<String, Object> data = new HashMap<>();
            data.put("name", stringName);
            data.put("color", color.asHexString());
            return data;
        }

        public static TeamTemplate deserialize(Map<String, Object> data) {
            String name = (String) data.getOrDefault("name", "");
            TextColor color = TextColor.fromHexString((String) data.getOrDefault("color", "#808080"));
            return new TeamTemplate(name, color);
        }

        public Component nameComponent() {
            return PlayerDisplay.MINI_BUILDER.deserialize(stringName);
        }
    }

    private final YmlDataManager data = BingoReloaded.createYmlDataManager("data/teams.yml");

    public Map<String, TeamTemplate> getTeams() {
        Map<String, TeamTemplate> teams = new HashMap<>();
        for (String key : data.getConfig().getKeys(false)) {
            teams.put(key, data.getConfig().getSerializable(key, TeamTemplate.class));
        }
        return teams;
    }

    public void addTeam(@NotNull String key, String name, TextColor color) {
        if (key.isEmpty()) {
            key = getNewTeamId();
        }
        data.getConfig().set(key, new TeamTemplate(name, color));
        data.saveConfig();
    }

    public void addTeam(@NotNull String key, TeamTemplate template) {
        addTeam(key, template.stringName(), template.color());
    }

    public TeamTemplate getTeam(String key, TeamTemplate def) {
        return data.getConfig().getSerializable(key, TeamTemplate.class, def);
    }

    public void removeTeam(String key) {
        data.getConfig().set(key, null);
        data.saveConfig();
    }

    public void reset() {
        for (String team : getTeams().keySet()) {
            data.getConfig().set(team, null);
        }
        data.saveConfig();

        for (BlockColor col : BlockColor.values()) {
            addTeam(col.name, WordUtils.capitalize(col.name.replace("_", " ")), col.textColor);
        }
    }

    // TODO: find cheaper way to generate valid team id
    public String getNewTeamId() {
        int id = 0;
        List<String> keys = data.getConfig().getKeys(false).stream().sorted().toList();
        while (keys.contains(String.valueOf(id))) {
            id += 1;
        }

        return String.valueOf(id);
    }
}
