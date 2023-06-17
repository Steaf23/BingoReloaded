package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.helper.YmlDataManager;
import io.github.steaf23.bingoreloaded.util.FlexColor;
import io.github.steaf23.bingoreloaded.util.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TeamData {
    @SerializableAs("TeamTemplate")
    public record TeamTemplate(String name, ChatColor color) implements ConfigurationSerializable {
        @NotNull
        @Override
        public Map<String, Object> serialize() {
            Map<String, Object> data = new HashMap<>();
            data.put("name", name);
            data.put("color", FlexColor.asHex(color));
            return data;
        }

        public static TeamTemplate deserialize(Map<String, Object> data) {
            String name = (String) data.getOrDefault("name", "");
            ChatColor color = ChatColor.of((String) data.getOrDefault("color", "#808080"));
            return new TeamTemplate(name, color);
        }
    }

    ;

    private final YmlDataManager data = BingoReloaded.createYmlDataManager("data/teams.yml");

    public Map<String, TeamTemplate> getTeams() {
        Map<String, TeamTemplate> teams = new HashMap<>();
        for (String key : data.getConfig().getKeys(false)) {
            teams.put(key, data.getConfig().getSerializable(key, TeamTemplate.class));
        }
        return teams;
    }

    public void addTeam(String key, String name, ChatColor color) {
        data.getConfig().set(key, new TeamTemplate(name, color));
        data.saveConfig();
    }

    public TeamTemplate getTeam(String key) {
        return data.getConfig().getSerializable(key, TeamTemplate.class, null);
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

        for (FlexColor col : FlexColor.values()) {
            addTeam(col.name, col.getTranslatedName(), col.chatColor);
        }
    }
}
