package io.github.steaf23.bingoreloaded.data;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class BingoConfigurationData
{
    public enum PluginConfiguration
    {
        SINGULAR,
        MULTIPLE,
    }

    public enum PlayerTeleportStrategy
    {
        ALONE,
        TEAM,
        ALL,
        NONE,
    }

    public enum LoadPlayerInformationStrategy
    {
        AFTER_GAME,
        AFTER_LEAVING_WORLD,
    }

    public record VoteList(List<String> gamemodes, List<String> kits, List<String> cards, List<String> cardSizes)
    {
        public boolean isEmpty() {
            return this.gamemodes.isEmpty() && this.kits.isEmpty() && this.cards.isEmpty() && this.cardSizes.isEmpty();
        }
    }

    public record HotswapConfig (int minimumExpiration, int maximumExpiration, int recoveryTime, boolean showExpirationAsDurability){}

    private final FileConfiguration config;

    // General options
    public final PluginConfiguration configuration;
    public final String defaultWorldName;
    public final String language;
    public final boolean savePlayerStatistics;
    public final String sendCommandAfterGameEnds;
    public final boolean voteUsingCommandsOnly;
    public final boolean selectTeamsUsingCommandsOnly;
    public final boolean disableScoreboardSidebar;
    public final boolean useIncludedResourcePack;
    public final boolean enableDebugLogging;

    // Lobby options
    public final boolean singlePlayerTeams;
    public final int minimumPlayerCount;
    public final int playerWaitTime;
    public final int gameRestartTime;
    public final boolean useVoteSystem;
    public final boolean preventPlayerGriefing; //FIXME: implement
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
    public final HotswapConfig hotswapMode;

    // Player data options
    public final boolean savePlayerInformation;
    public final LoadPlayerInformationStrategy loadPlayerInformationStrategy;

    // Configuration: MULTIPLE options
    public final List<String> defaultWorlds;
    public final boolean resetDefaultWorlds; //FIXME: implement

    public BingoConfigurationData(FileConfiguration config) {
        this.config = config;
        // General
        this.configuration = PluginConfiguration.valueOf(config.getString("configuration", "SINGULAR"));
        this.defaultWorldName = config.getString("defaultWorldName", "world");
        this.language = "languages/" + config.getString("language", "en_us.yml");
        this.savePlayerStatistics = config.getBoolean("savePlayerStatistics", false);
        this.sendCommandAfterGameEnds = config.getString("sendCommandAfterGameEnds", "");
        this.voteUsingCommandsOnly = config.getBoolean("voteUsingCommandsOnly", false);
        this.selectTeamsUsingCommandsOnly = config.getBoolean("selectTeamsUsingCommandsOnly", false);
        this.disableScoreboardSidebar = config.getBoolean("disableScoreboardSidebar", false);
        // TODO: re-enable resource pack
//        this.useIncludedResourcepack = config.getBoolean("useIncludedResourcepack", true);
        this.useIncludedResourcePack = false;
        this.enableDebugLogging = config.getBoolean("enableDebugLogging", false);

        // Lobby
        this.singlePlayerTeams = config.getBoolean("singlePlayerTeams", false);
        this.minimumPlayerCount = Math.max(0, config.getInt("minimumPlayerCount", 4));
        this.playerWaitTime = Math.max(0, config.getInt("playerWaitTime", 30));
        this.gameRestartTime = Math.max(0, config.getInt("gameRestartTime", 20));
        this.useVoteSystem = config.getBoolean("useVoteSystem", false);
        this.preventPlayerGriefing = config.getBoolean("preventPlayerGriefing", true);
        this.voteList = new VoteList(
                config.getStringList("voteList.gamemodes"),
                config.getStringList("voteList.kits"),
                config.getStringList("voteList.cards"),
                config.getStringList("voteList.cardsizes"));

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
        this.hotswapMode = new HotswapConfig(
                config.getInt("hotswapMode.minimumExpirationTime", 3),
                config.getInt("hotswapMode.maximumExpirationTime", 20),
                config.getInt("hotswapMode.recoverTime", 10),
                config.getBoolean("hotswapMode.showExpirationAsDurability", true));

        // Player
        this.savePlayerInformation = config.getBoolean("savePlayerInformation", true);
        this.loadPlayerInformationStrategy = LoadPlayerInformationStrategy.valueOf(
                config.getString("loadPlayerInformationStrategy", "AFTER_LEAVING_WORLD"));

        // Configuration: MULTIPLE
        this.defaultWorlds = config.getStringList("defaultWorlds");
        this.resetDefaultWorlds = config.getBoolean("resetDefaultWorlds", true);
    }

    public <DataType> DataType getOptionValue(ConfigurationOption<DataType> option) {
        return (DataType) config.get(option.getConfigName());
    }

    public <DataType> void setOptionValue(ConfigurationOption<DataType> option, DataType value) {
        config.set(option.getConfigName(), value);
    }
}
