package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.core.NodeDataAccessor;
import io.github.steaf23.bingoreloaded.data.core.node.BranchNode;
import io.github.steaf23.bingoreloaded.data.core.node.NodeBuilder;
import io.github.steaf23.bingoreloaded.data.core.node.NodeSerializer;
import io.github.steaf23.playerdisplay.PlayerDisplay;
import io.github.steaf23.playerdisplay.util.BlockColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.apache.commons.text.WordUtils;
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
    public record TeamTemplate(String stringName, TextColor color) implements NodeSerializer
    {
        public TeamTemplate(BranchNode node) {
            this(node.getString("name"), TextColor.fromHexString(node.getString("color", "#808080")));
        }

        @Override
        public BranchNode toNode() {
            return new NodeBuilder()
                    .withString("name", stringName)
                    .withString("color", color.asHexString())
                    .getNode();
        }

        public Component nameComponent() {
            return PlayerDisplay.MINI_BUILDER.deserialize(stringName);
        }
    }

    private final NodeDataAccessor data = BingoReloaded.getOrCreateDataAccessor("data/teams.yml", NodeDataAccessor.class);

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
        data.setSerializable(key, new TeamTemplate(name, color));
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
            addTeam(col.name, WordUtils.capitalize(col.name.replace("_", " ")), col.textColor);
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
