package io.github.steaf23.bingoreloaded.data;

import java.util.ArrayList;

public class ConfigurationOption<Data>
{
    // General
    public static final ConfigurationOption<BingoConfigurationData.PluginConfiguration> CONFIGURATION = new ConfigurationOption<>("configuration", BingoConfigurationData.PluginConfiguration.class);
    public static final ConfigurationOption<String> DEFAULT_WORLD_NAME = new ConfigurationOption<>("defaultWorldName", String.class);
    public static final ConfigurationOption<String> LANGUAGE = new ConfigurationOption<>("language", String.class);
    public static final ConfigurationOption<Boolean> SAVE_PLAYER_STATISTICS = new ConfigurationOption<>("savePlayerStatistics", Boolean.class);
    public static final ConfigurationOption<String> SEND_COMMAND_AFTER_GAME_ENDS = new ConfigurationOption<>("sendCommandAfterGameEnds", String.class);
    public static final ConfigurationOption<Boolean> VOTE_USING_COMMANDS_ONLY = new ConfigurationOption<>("voteUsingCommandsOnly", Boolean.class);
    public static final ConfigurationOption<Boolean> SELECT_TEAMS_USING_COMMANDS_ONLY = new ConfigurationOption<>("selectTeamsUsingCommandsOnly", Boolean.class);
    public static final ConfigurationOption<Boolean> DISABLE_SCOREBOARD_SIDEBAR = new ConfigurationOption<>("disableScoreboardSidebar", Boolean.class);
    public static final ConfigurationOption<Boolean> USE_INCLUDED_RESOURCE_PACK = new ConfigurationOption<>("useIncludedResourcePack", Boolean.class);
    public static final ConfigurationOption<Boolean> ENABLE_DEBUG_LOGGING = new ConfigurationOption<>("enableDebugLogging", Boolean.class);

    // Lobby
    public static final ConfigurationOption<Boolean> SINGLE_PLAYER_TEAMS = new ConfigurationOption<>("singlePlayerTeams", Boolean.class);
    public static final ConfigurationOption<Integer> MINIMUM_PLAYER_COUNT = new ConfigurationOption<>("minimumPlayerCount", Integer.class);
    public static final ConfigurationOption<Integer> PLAYER_WAIT_TIME = new ConfigurationOption<>("playerWaitTime", Integer.class);
    public static final ConfigurationOption<Integer> GAME_RESTART_TIME = new ConfigurationOption<>("gameRestartTime", Integer.class);
    public static final ConfigurationOption<Boolean> USE_VOTE_SYSTEM = new ConfigurationOption<>("useVoteSystem", Boolean.class);
    public static final ConfigurationOption<Boolean> PREVENT_PLAYER_GRIEFING = new ConfigurationOption<>("preventPlayerGriefing", Boolean.class);
    public static final ConfigurationOption<BingoConfigurationData.VoteList> VOTE_LIST = new ConfigurationOption<>("voteList", BingoConfigurationData.VoteList.class);

    // Gameplay
    public static final ConfigurationOption<Integer> STARTING_COUNTDOWN_TIME = new ConfigurationOption<>("startingCountdownTime", Integer.class);
    public static final ConfigurationOption<Integer> TELEPORT_MAX_DISTANCE = new ConfigurationOption<>("teleportMaxDistance", Integer.class);
    public static final ConfigurationOption<BingoConfigurationData.PlayerTeleportStrategy> PLAYER_TELEPORT_STRATEGY = new ConfigurationOption<>("playerTeleportStrategy", BingoConfigurationData.PlayerTeleportStrategy.class);
    public static final ConfigurationOption<Boolean> TELEPORT_AFTER_DEATH = new ConfigurationOption<>("teleportAfterDeath", Boolean.class);
    public static final ConfigurationOption<Integer> TELEPORT_AFTER_DEATH_PERIOD = new ConfigurationOption<>("teleportAfterDeathPeriod", Integer.class);
    public static final ConfigurationOption<Integer> GO_UP_WAND_UP_DISTANCE = new ConfigurationOption<>("GoUpWand.upDistance", Integer.class);
    public static final ConfigurationOption<Integer> GO_UP_WAND_DOWN_DISTANCE = new ConfigurationOption<>("GoUpWand.downDistance", Integer.class);
    public static final ConfigurationOption<Double> GO_UP_WAND_COOLDOWN = new ConfigurationOption<>("GoUpWand.cooldown", Double.class);
    public static final ConfigurationOption<Integer> GO_UP_WAND_PLATFORM_LIFETIME = new ConfigurationOption<>("GoUpWand.platformLifetime", Integer.class);
    public static final ConfigurationOption<Integer> GRACE_PERIOD = new ConfigurationOption<>("gracePeriod", Integer.class);
    public static final ConfigurationOption<Boolean> REMOVE_TASK_ITEMS = new ConfigurationOption<>("removeTaskItems", Boolean.class);
    public static final ConfigurationOption<Boolean> ENABLE_TEAM_CHAT = new ConfigurationOption<>("enableTeamChat", Boolean.class);
    public static final ConfigurationOption<Boolean> KEEP_SCOREBOARD_VISIBLE = new ConfigurationOption<>("keepScoreboardVisible", Boolean.class);
    public static final ConfigurationOption<Boolean> SHOW_PLAYER_IN_SCOREBOARD = new ConfigurationOption<>("showPlayerInScoreboard", Boolean.class);
    public static final ConfigurationOption<Boolean> DISABLE_ADVANCEMENTS = new ConfigurationOption<>("disableAdvancements", Boolean.class);
    public static final ConfigurationOption<Boolean> DISABLE_STATISTICS = new ConfigurationOption<>("disableStatistics", Boolean.class);
    public static final ConfigurationOption<Boolean> END_GAME_WITHOUT_TEAMS = new ConfigurationOption<>("endGameWithoutTeams", Boolean.class);
    public static final ConfigurationOption<BingoConfigurationData.HotswapConfig> HOTSWAP_CONFIG = new ConfigurationOption<>("hotswapConfig", BingoConfigurationData.HotswapConfig.class);

    // Player Data
    public static final ConfigurationOption<Boolean> SAVE_PLAYER_INFORMATION = new ConfigurationOption<>("savePlayerInformation", Boolean.class);
    public static final ConfigurationOption<BingoConfigurationData.LoadPlayerInformationStrategy> LOAD_PLAYER_INFORMATION_STRATEGY = new ConfigurationOption<>("loadPlayerInformationStrategy", BingoConfigurationData.LoadPlayerInformationStrategy.class);

    // Configuration: MULTIPLE
    public static class StringList extends ArrayList<String> {}
    public static final ConfigurationOption<StringList> DEFAULT_WORLDS = new ConfigurationOption<>("defaultWorlds", StringList.class);
    public static final ConfigurationOption<Boolean> RESET_DEFAULT_WORLDS = new ConfigurationOption<>("resetDefaultWorlds", Boolean.class);

    private final String configName;
    private final Class<Data> dataType;

    public ConfigurationOption(String configName, Class<Data> type) {
        this.configName = configName;
        this.dataType = type;
    }

    public String getConfigName() {
        return configName;
    }
}
