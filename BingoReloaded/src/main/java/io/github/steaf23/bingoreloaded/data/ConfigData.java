package io.github.steaf23.bingoreloaded.data;

import org.bukkit.configuration.file.FileConfiguration;

import javax.annotation.Nullable;

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

    // General options
    public String defaultWorldName;
    public PluginConfiguration configuration;
    public String language;
    public boolean savePlayerStatistics;
    public boolean useVoteSystem;

    // Gameplay options
    public String defaultSettingsPreset;
    public int teleportMaxDistance;
    public PlayerTeleportStrategy playerTeleportStrategy;
    public boolean teleportAfterDeath;
    public int wandUp;
    public int wandDown;
    public double wandCooldown;
    public int platformLifetime;
    public int gracePeriod;
    public boolean enableTeamChat;
    public boolean keepScoreboardVisible;
    public boolean showPlayerInScoreboard;
    public boolean disableAdvancements;
    public boolean disableStatistics;

    // Public options
    public String sendCommandAfterGameEnded;

    public void loadConfig(FileConfiguration config)
    {
        this.defaultWorldName = config.getString("defaultWorldName", "world");
        this.configuration = PluginConfiguration.fromName(config.getString("configuration", "singular"));
        this.language = "languages/" + config.getString("language", "en_us.yml");
        this.savePlayerStatistics = config.getBoolean("savePlayerStatistics", false);
        this.useVoteSystem = config.getBoolean("useVoteSystem", false);

        this.defaultSettingsPreset = config.getString("defaultSettingsPreset", "default_settings");
        this.teleportMaxDistance = config.getInt("teleportMaxDistance", 1000000);
        this.playerTeleportStrategy = PlayerTeleportStrategy.fromName(config.getString("playerTeleportStrategy", "ALL"));
        this.teleportAfterDeath = config.getBoolean("teleportBackAfterDeathMessage", true);
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

        this.sendCommandAfterGameEnded = config.getString("sendCommandAfterGameEnds", "");
    }

    public void saveConfig()
    {

    }
}
