package io.github.steaf23.bingoreloaded.data;

import org.bukkit.configuration.file.FileConfiguration;

import javax.annotation.Nullable;
import java.util.List;

public class ConfigData
{
    public enum PluginConfiguration
    {
        SINGULAR,
        MULTIPLE,
        ;
    }

    public enum PlayerTeleportStrategy
    {
        ALONE,
        TEAM,
        ALL,
        NONE,
        ;
    }

    public enum LoadPlayerInformationStrategy
    {
        AFTER_GAME,
        AFTER_LEAVING_WORLD,
        ;
    }

    public class VoteList
    {
        public final List<String> gamemodes;
        public final List<String> kits;
        public final List<String> cards;

        public VoteList(List<String> gamemodes, List<String> kits, List<String> cards) {
            this.gamemodes = gamemodes;
            this.kits = kits;
            this.cards = cards;
        }

        public boolean isEmpty() {
            return this.gamemodes.size() == 0 && this.kits.size() == 0 && this.cards.size() == 0;
        }
    }

    // General options
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
    //TODO: implement
    public final boolean voteUsingCommandsOnly;
    public final boolean selectTeamUsingCommandsOnly;

    // Private options
    public final String defaultWorldName;
    //TODO: implement
    public final boolean savePlayerInformation;
    public final LoadPlayerInformationStrategy loadPlayerInformationStrategy;

    public ConfigData(FileConfiguration config) {
        // General
        this.configuration = PluginConfiguration.valueOf(config.getString("configuration", "SINGULAR"));
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
        this.playerTeleportStrategy = PlayerTeleportStrategy.valueOf(config.getString("playerTeleportStrategy", "ALL"));
        this.teleportAfterDeath = config.getBoolean("teleportBackAfterDeathMessage", true);
        this.wandUp = config.getInt("GoUpWand.upDistance", 75);
        this.wandDown = config.getInt("GoUpWand.downDistance", 5);
        this.wandCooldown = config.getDouble("GoUpWand.cooldown", 5.0);
        this.platformLifetime = config.getInt("GoUPWand.platformLifetime", 10);
        this.gracePeriod = config.getInt("gracePeriod", 30);
        this.enableTeamChat = config.getBoolean("enableTeamChat", true);
        this.keepScoreboardVisible = config.getBoolean("keepScoreboardVisible", true);
        this.showPlayerInScoreboard = config.getBoolean("showPlayerInScoreboard", true);
        this.disableAdvancements = config.getBoolean("disableAdvancements", false);
        this.disableStatistics = config.getBoolean("disableStatistics", false);

        // Public
        this.sendCommandAfterGameEnded = config.getString("sendCommandAfterGameEnds", "");
        this.voteUsingCommandsOnly = config.getBoolean("voteUsingCommandsOnly", false);
        this.selectTeamUsingCommandsOnly = config.getBoolean("selectTeamsUsingCommandsOnly", false);

        // Private
        this.defaultWorldName = config.getString("defaultWorldName", "world");
        //TODO: test this with nether/end dimension too!
        this.savePlayerInformation = config.getBoolean("playerLoadStrategy", true);
        this.loadPlayerInformationStrategy = LoadPlayerInformationStrategy.valueOf(
                config.getString("loadPlayerInformationStrategy", "AFTER_LEAVING_WORLD"));
    }
}
