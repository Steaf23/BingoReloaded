package io.github.steaf23.bingoreloaded.data;

import org.bukkit.configuration.file.FileConfiguration;

import javax.annotation.Nullable;

public class ConfigData
{
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

    public int teleportMaxDistance;
    public PlayerTeleportStrategy playerTeleportStrategy;
    public boolean teleportAfterDeath;
    public int lobbySpawnHeight;
    public int wandUp;
    public int wandDown;
    public double wandCooldown;
    public int platformLifetime;
    public String defaultKit;
    public int gracePeriod;
    //TODO: Implement option
    public boolean resetPlayerItems;
    //TODO: Implement option
    public boolean resetPlayerPositions;
    public String selectedCard;
    public String language;
    public int defaultGameDuration;
    public int defaultTeamSize;
    public boolean savePlayerStatistics;
    public int cardSeed;
    public boolean enableTeamChat;
    public String sendCommandAfterGameEnded;
    public boolean keepScoreboardVisible;
    public boolean showPlayerInScoreboard;

    public boolean useStatistics;
    public boolean useAdvancements;

    // TODO: make defaultBingoWorld: "world" config option

    public void loadConfig(FileConfiguration config)
    {
        this.language = "languages/" + config.getString("language", "en_us.yml");
        this.teleportMaxDistance = config.getInt("teleportMaxDistance", 1000000);
        this.playerTeleportStrategy = PlayerTeleportStrategy.fromName(config.getString("playerTeleportStrategy", "ALL"));
        this.teleportAfterDeath = config.getBoolean("teleportBackAfterDeathMessage", true);
        this.lobbySpawnHeight = config.getInt("lobbySpawnHeight", 128);
        this.wandUp = config.getInt("GoUpWand.upDistance", 75);
        this.wandDown = config.getInt("GoUpWand.downDistance", 5);
        this.wandCooldown = config.getDouble("GoUpWand.cooldown", 5.0);
        this.platformLifetime = config.getInt("GoUPWand.platformLifetime", 10);
        this.defaultKit = config.getString("defaultKit", "HARDCORE");
        this.gracePeriod = config.getInt("gracePeriod", 30);
        this.resetPlayerItems = config.getBoolean("resetPlayerItems", true);
        this.resetPlayerPositions = config.getBoolean("resetPlayerPositions", true);
        this.selectedCard = config.getString("selectedCard", "default_card");
        this.savePlayerStatistics = config.getBoolean("savePlayerStatistics", false);
        this.defaultGameDuration = config.getInt("defaultGameDuration", 20);
        this.defaultTeamSize = config.getInt("defaultTeamSize", 64);
        this.cardSeed = config.getInt("cardSeed", 0);
        this.enableTeamChat = config.getBoolean("enableTeamChat", true);
        this.sendCommandAfterGameEnded = config.getString("sendCommandAfterGameEnds", "");
        this.keepScoreboardVisible = config.getBoolean("keepScoreboardVisible", true);
        this.showPlayerInScoreboard = config.getBoolean("showPlayerInScoreboard", true);
        //TODO: Finish implementation
        this.useStatistics = config.getBoolean("useStatistics", true);
        //TODO: Finish implementation
        this.useAdvancements = config.getBoolean("useAdvancements", true);
    }
}
