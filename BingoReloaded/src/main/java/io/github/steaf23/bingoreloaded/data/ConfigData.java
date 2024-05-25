package io.github.steaf23.bingoreloaded.data;

import org.bukkit.configuration.file.FileConfiguration;

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

    public record VoteList(List<String> gamemodes, List<String> kits, List<String> cards)
    {
        public boolean isEmpty() {
            return this.gamemodes.isEmpty() && this.kits.isEmpty() && this.cards.isEmpty();
        }
    }

    // General options
    public final PluginConfiguration configuration;
    public final String defaultWorldName;
    public final String language;
    public final boolean savePlayerStatistics;
    public final String sendCommandAfterGameEnded;
    public final boolean voteUsingCommandsOnly;
    public final boolean selectTeamsUsingCommandsOnly;
    public final boolean disableScoreboardSidebar;

    // Lobby options
    public final boolean singlePlayerTeams;
    public final int minimumPlayerCount;
    public final int playerWaitTime;
    public final int gameRestartTime;
    public final boolean useVoteSystem;
    public final VoteList voteList;

    // Gameplay options
    public final int startingCountdownTime;
    public final int teleportMaxDistance;
    public final PlayerTeleportStrategy playerTeleportStrategy;
    public final boolean teleportAfterDeath;
    public final int teleportAfterDeathPeriod;
    public final int wandUp;
    public final int wandDown;
    public final double wandCooldown;
    public final int platformLifetime;
    public final int gracePeriod;
    public final boolean removeTaskItems;
    public final boolean enableTeamChat;
    public final boolean keepScoreboardVisible;
    public final boolean showPlayerInScoreboard;
    public final boolean disableAdvancements;
    public final boolean disableStatistics;
    public final boolean endGameWithoutTeams;

    // Player data options
    public final boolean savePlayerInformation;
    public final LoadPlayerInformationStrategy loadPlayerInformationStrategy;

    public ConfigData(FileConfiguration config) {
        // General
        this.configuration = PluginConfiguration.valueOf(config.getString("configuration", "SINGULAR"));
        this.defaultWorldName = config.getString("defaultWorldName", "world");
        this.language = "languages/" + config.getString("language", "en_us.yml");
        this.savePlayerStatistics = config.getBoolean("savePlayerStatistics", false);
        this.sendCommandAfterGameEnded = config.getString("sendCommandAfterGameEnds", "");
        this.voteUsingCommandsOnly = config.getBoolean("voteUsingCommandsOnly", false);
        this.selectTeamsUsingCommandsOnly = config.getBoolean("selectTeamsUsingCommandsOnly", false);
        this.disableScoreboardSidebar = config.getBoolean("disableScoreboardSidebar", false);

        // Lobby
        this.singlePlayerTeams = config.getBoolean("singlePlayerTeams", false);
        this.minimumPlayerCount = Math.max(0, config.getInt("minimumPlayerCount", 4));
        this.playerWaitTime = Math.max(0, config.getInt("playerWaitTime", 30));
        this.gameRestartTime = Math.max(0, config.getInt("gameRestartTime", 20));
        this.useVoteSystem = config.getBoolean("useVoteSystem", false);
        this.voteList = new VoteList(
                config.getStringList("voteList.gamemodes"),
                config.getStringList("voteList.kits"),
                config.getStringList("voteList.cards"));

        // Gameplay
        this.startingCountdownTime = Math.max(0, config.getInt("startingCountdownTime", 10));
        this.teleportMaxDistance = Math.max(0, config.getInt("teleportMaxDistance", 1000000));
        this.playerTeleportStrategy = PlayerTeleportStrategy.valueOf(config.getString("playerTeleportStrategy", "ALL"));
        this.teleportAfterDeath = config.getBoolean("teleportBackAfterDeathMessage", true);
        this.teleportAfterDeathPeriod = config.getInt("teleportAfterDeathPeriod", 60);
        this.wandUp = config.getInt("GoUpWand.upDistance", 75);
        this.wandDown = config.getInt("GoUpWand.downDistance", 5);
        this.wandCooldown = config.getDouble("GoUpWand.cooldown", 5.0);
        this.platformLifetime = Math.max(0, config.getInt("GoUPWand.platformLifetime", 10));
        this.gracePeriod = Math.max(0, config.getInt("gracePeriod", 30));
        this.removeTaskItems = config.getBoolean("removeTaskItems", true);
        this.enableTeamChat = config.getBoolean("enableTeamChat", true);
        this.keepScoreboardVisible = config.getBoolean("keepScoreboardVisible", true);
        this.showPlayerInScoreboard = config.getBoolean("showPlayerInScoreboard", true);
        this.disableAdvancements = config.getBoolean("disableAdvancements", false);
        this.disableStatistics = config.getBoolean("disableStatistics", false);
        this.endGameWithoutTeams = config.getBoolean("endGameWithoutTeams", true);

        // Player
        this.savePlayerInformation = config.getBoolean("playerLoadStrategy", true);
        this.loadPlayerInformationStrategy = LoadPlayerInformationStrategy.valueOf(
                config.getString("loadPlayerInformationStrategy", "AFTER_LEAVING_WORLD"));
    }
}
