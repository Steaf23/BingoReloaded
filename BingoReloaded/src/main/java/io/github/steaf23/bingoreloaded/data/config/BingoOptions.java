package io.github.steaf23.bingoreloaded.data.config;

public class BingoOptions
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

    // General
    public static final ConfigurationOption<PluginConfiguration> CONFIGURATION = new EnumOption<>("configuration", PluginConfiguration.class, PluginConfiguration.SINGULAR)
            .withEditUpdate(ConfigurationOption.EditUpdateTime.AFTER_SERVER_RESTART);
    public static final ConfigurationOption<String> DEFAULT_WORLD_NAME = new StringOption("defaultWorldName")
            .withEditUpdate(ConfigurationOption.EditUpdateTime.AFTER_SERVER_RESTART);
    public static final ConfigurationOption<String> LANGUAGE = new StringOption("language")
            .withEditUpdate(ConfigurationOption.EditUpdateTime.AFTER_SERVER_RESTART);
    public static final ConfigurationOption<Boolean> SAVE_PLAYER_STATISTICS = new BooleanOption("savePlayerStatistics");
    public static final ConfigurationOption<String> SEND_COMMAND_AFTER_GAME_ENDS = new StringOption("sendCommandAfterGameEnds");
    public static final ConfigurationOption<Boolean> VOTE_USING_COMMANDS_ONLY = new BooleanOption("voteUsingCommandsOnly");
    public static final ConfigurationOption<Boolean> SELECT_TEAMS_USING_COMMANDS_ONLY = new BooleanOption("selectTeamsUsingCommandsOnly");
    public static final ConfigurationOption<Boolean> DISABLE_SCOREBOARD_SIDEBAR = new BooleanOption("disableScoreboardSidebar")
            .withEditUpdate(ConfigurationOption.EditUpdateTime.AFTER_SESSION);
    public static final ConfigurationOption<Boolean> USE_INCLUDED_RESOURCE_PACK = new BooleanOption("useIncludedResourcePack")
            .withEditUpdate(ConfigurationOption.EditUpdateTime.AFTER_SERVER_RESTART);
    public static final ConfigurationOption<Boolean> USE_MAP_RENDERER = new BooleanOption("useMapRenderer")
            .withEditUpdate(ConfigurationOption.EditUpdateTime.AFTER_GAME);
    public static final ConfigurationOption<Boolean> SHOW_UNIQUE_TASK_ITEMS = new BooleanOption("showUniqueTaskItems")
            .withEditUpdate(ConfigurationOption.EditUpdateTime.AFTER_GAME);
    public static final ConfigurationOption<Boolean> ENABLE_DEBUG_LOGGING = new BooleanOption("enableDebugLogging");

    // Lobby
    public static final ConfigurationOption<Boolean> SINGLE_PLAYER_TEAMS = new BooleanOption("singlePlayerTeams")
            .withEditUpdate(ConfigurationOption.EditUpdateTime.AFTER_SESSION);
    public static final ConfigurationOption<Integer> MINIMUM_PLAYER_COUNT = new IntegerOption("minimumPlayerCount").withMin(0);
    public static final ConfigurationOption<Integer> PLAYER_WAIT_TIME = new IntegerOption("playerWaitTime").withMin(0)
            .withEditUpdate(ConfigurationOption.EditUpdateTime.AFTER_GAME);
    public static final ConfigurationOption<Integer> GAME_RESTART_TIME = new IntegerOption("gameRestartTime").withMin(0)
            .withEditUpdate(ConfigurationOption.EditUpdateTime.AFTER_GAME);
    public static final ConfigurationOption<Boolean> USE_VOTE_SYSTEM = new BooleanOption("useVoteSystem");
    public static final ConfigurationOption<Boolean> PREVENT_PLAYER_GRIEFING = new BooleanOption("preventPlayerGriefing");
    public static final ConfigurationOption<BingoConfigurationData.VoteList> VOTE_LIST = new NonSerializableOption<>("voteList");

    // Gameplay
    public static final ConfigurationOption<Integer> STARTING_COUNTDOWN_TIME = new IntegerOption("startingCountdownTime").withMin(0)
            .withEditUpdate(ConfigurationOption.EditUpdateTime.AFTER_GAME);
    public static final ConfigurationOption<Integer> TELEPORT_MAX_DISTANCE = new IntegerOption("teleportMaxDistance").withMin(0)
            .withEditUpdate(ConfigurationOption.EditUpdateTime.AFTER_GAME);
    public static final ConfigurationOption<PlayerTeleportStrategy> PLAYER_TELEPORT_STRATEGY = new EnumOption<>("playerTeleportStrategy", PlayerTeleportStrategy.class, PlayerTeleportStrategy.ALL)
            .withEditUpdate(ConfigurationOption.EditUpdateTime.AFTER_GAME);
    public static final ConfigurationOption<Boolean> TELEPORT_AFTER_DEATH = new BooleanOption("teleportAfterDeath");
    public static final ConfigurationOption<Integer> TELEPORT_AFTER_DEATH_PERIOD = new IntegerOption("teleportAfterDeathPeriod").withMin(0)
            .withEditUpdate(ConfigurationOption.EditUpdateTime.AFTER_GAME);
    public static final ConfigurationOption<Integer> GO_UP_WAND_UP_DISTANCE = new IntegerOption("GoUpWand.upDistance");
    public static final ConfigurationOption<Integer> GO_UP_WAND_DOWN_DISTANCE = new IntegerOption("GoUpWand.downDistance");
    public static final ConfigurationOption<Double> GO_UP_WAND_COOLDOWN = new DoubleOption("GoUpWand.cooldown");
    public static final ConfigurationOption<Integer> GO_UP_WAND_PLATFORM_LIFETIME = new IntegerOption("GoUpWand.platformLifetime").withMin(0);
    public static final ConfigurationOption<Integer> GRACE_PERIOD = new IntegerOption("gracePeriod").withMin(0);
    public static final ConfigurationOption<Boolean> REMOVE_TASK_ITEMS = new BooleanOption("removeTaskItems");
    public static final ConfigurationOption<Boolean> ENABLE_TEAM_CHAT = new BooleanOption("enableTeamChat")
            .withEditUpdate(ConfigurationOption.EditUpdateTime.AFTER_SERVER_RESTART);
    public static final ConfigurationOption<Boolean> KEEP_SCOREBOARD_VISIBLE = new BooleanOption("keepScoreboardVisible")
            .withEditUpdate(ConfigurationOption.EditUpdateTime.AFTER_GAME);
    public static final ConfigurationOption<Boolean> SHOW_PLAYER_IN_SCOREBOARD = new BooleanOption("showPlayerInScoreboard")
            .withEditUpdate(ConfigurationOption.EditUpdateTime.AFTER_SESSION);
    public static final ConfigurationOption<Boolean> DISABLE_ADVANCEMENTS = new BooleanOption("disableAdvancements")
            .withEditUpdate(ConfigurationOption.EditUpdateTime.AFTER_SERVER_RESTART);
    public static final ConfigurationOption<Boolean> DISABLE_STATISTICS = new BooleanOption("disableStatistics")
            .withEditUpdate(ConfigurationOption.EditUpdateTime.AFTER_SERVER_RESTART);
    public static final ConfigurationOption<Boolean> END_GAME_WITHOUT_TEAMS = new BooleanOption("endGameWithoutTeams");
    public static final ConfigurationOption<BingoConfigurationData.HotswapConfig> HOTSWAP_CONFIG = new NonSerializableOption<>("hotswapMode");
    public static final ConfigurationOption<Boolean> ALLOW_VIEWING_ALL_CARDS = new BooleanOption("allowViewingAllCards")
            .withEditUpdate(ConfigurationOption.EditUpdateTime.AFTER_GAME);

    // Player Data
    public static final ConfigurationOption<Boolean> SAVE_PLAYER_INFORMATION = new BooleanOption("savePlayerInformation");
    public static final ConfigurationOption<LoadPlayerInformationStrategy> LOAD_PLAYER_INFORMATION_STRATEGY = new EnumOption<>("loadPlayerInformationStrategy", LoadPlayerInformationStrategy.class, LoadPlayerInformationStrategy.AFTER_LEAVING_WORLD);

    // Configuration: MULTIPLE
    public static final ConfigurationOption<ConfigurationOption.StringList> DEFAULT_WORLDS = new NonSerializableOption<ConfigurationOption.StringList>("defaultWorlds")
            .withEditUpdate(ConfigurationOption.EditUpdateTime.AFTER_GAME);
    public static final ConfigurationOption<Boolean> CLEAR_DEFAULT_WORLDS = new BooleanOption("clearDefaultWorlds");
    public static final ConfigurationOption<String> CUSTOM_WORLD_GENERATION = new StringOption("customWorldGeneration")
            .withEditUpdate(ConfigurationOption.EditUpdateTime.AFTER_SERVER_RESTART);
}
