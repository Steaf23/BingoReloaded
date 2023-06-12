package io.github.steaf23.bingoreloaded.data;

import org.bukkit.configuration.file.FileConfiguration;

import javax.annotation.Nullable;
import java.util.List;

public class ConfigData
{
    public enum PluginConfiguration
    {
        SINGULAR("singular"),
        MULTIPLE("multiple"),
        ;

        public final String configName;

        PluginConfiguration(String configName)
        {
            this.configName = configName;
        }

        static PluginConfiguration fromName(@Nullable String name)
        {
            if (name == null)
                return SINGULAR;
            return switch (name.toLowerCase())
                    {
                        case "singular" -> SINGULAR;
                        case "multiple" -> MULTIPLE;
                        default -> SINGULAR;
                    };
        }
    }

    public enum PlayerTeleportStrategy
    {
        ALONE("alone"),
        TEAM("team"),
        ALL("all"),
        NONE("none"),
        CUSTOM("custom");

        public final String name;

        PlayerTeleportStrategy(String name)
        {
            this.name = name;
        }

        static PlayerTeleportStrategy fromName(@Nullable String name)
        {
            if (name == null)
                return ALL;
            return switch (name.toLowerCase())
                    {
                        case "alone" -> ALONE;
                        case "team" -> TEAM;
                        case "all" -> ALL;
                        case "custom" -> CUSTOM;
                        default -> NONE;
                    };
        }
    }

    public class VoteList
    {
        public final List<String> gamemodes;
        public final List<String> kits;
        public final List<String> cards;

        public VoteList(List<String> gamemodes, List<String> kits, List<String> cards)
        {
            this.gamemodes = gamemodes;
            this.kits = kits;
            this.cards = cards;
        }
    }

    // General options
    public final String defaultWorldName;
    public final PluginConfiguration configuration;
    public final String language;
    public final boolean savePlayerStatistics;
    public final boolean useVoteSystem;
    public final VoteList voteList;

    // Gameplay options
    public final String defaultSettingsPreset;
    public final int teleportMaxDistance;
    public final PlayerTeleportStrategy playerTeleportStrategy;
    public final boolean teleportAfterDeath;
    public final boolean teleportToTeammates;
    public final int wandUp;
    public final int wandDown;
    public final double wandCooldown;
    public final int platformLifetime;
    public final int gracePeriod;
    public final boolean enableTeamChat;
    public final boolean keepScoreboardVisible;
    public final boolean showPlayerInScoreboard;
    public final boolean disableAdvancements;
    public final boolean disableStatistics;

    // Public options
    public final String sendCommandAfterGameEnded;


    // Private options
    //TODO: implement
    public final boolean restorePlayerAfterGameEnds;

    public ConfigData(FileConfiguration config)
    {
        // General
        this.defaultWorldName = config.getString("defaultWorldName", "world");
        this.configuration = PluginConfiguration.fromName(config.getString("configuration", "singular"));
        this.language = "languages/" + config.getString("language", "en_us.yml");
        this.savePlayerStatistics = config.getBoolean("savePlayerStatistics", false);
        this.useVoteSystem = config.getBoolean("useVoteSystem", false);
        this.voteList = new VoteList(
                config.getStringList("voteList.gamemodes"),
                config.getStringList("voteList.kits"),
                config.getStringList("voteList.cards"));

        // Gameplay
        this.defaultSettingsPreset = config.getString("defaultSettingsPreset", "default_settings");
        this.teleportMaxDistance = config.getInt("teleportMaxDistance", 1000000);
        this.playerTeleportStrategy = PlayerTeleportStrategy.fromName(config.getString("playerTeleportStrategy", "ALL"));
        this.teleportAfterDeath = config.getBoolean("teleportBackAfterDeathMessage", true);
        this.teleportToTeammates = config.getBoolean("teleportToTeammates", false);
        this.wandUp = config.getInt("GoUpWand.upDistance", 75);
        this.wandDown = config.getInt("GoUpWand.downDistance", 5);
        this.wandCooldown = config.getDouble("GoUpWand.cooldown", 5.0);
        this.platformLifetime = config.getInt("GoUPWand.platformLifetime", 10);
        this.gracePeriod = config.getInt("gracePeriod", 30);
        this.enableTeamChat = config.getBoolean("enableTeamChat", true);
        this.keepScoreboardVisible = config.getBoolean("keepScoreboardVisible", true);
        this.showPlayerInScoreboard = config.getBoolean("showPlayerInScoreboard", true);
        this.disableAdvancements = config.getBoolean("disableAdvancements", true);
        this.disableStatistics = config.getBoolean("disableStatistics", true);

        // Public
        this.sendCommandAfterGameEnded = config.getString("sendCommandAfterGameEnds", "");

        // Private
        this.restorePlayerAfterGameEnds = config.getBoolean("restorePlayersAfterGameEnds", true);
    }
}
