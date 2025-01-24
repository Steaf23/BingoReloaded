package io.github.steaf23.bingoreloaded.data.config;

import io.github.steaf23.bingoreloaded.data.core.configuration.ConfigDataAccessor;
import io.github.steaf23.bingoreloaded.data.core.tag.TagDataType;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class BingoConfigurationData
{
    public record VoteList(List<String> gamemodes, List<String> kits, List<String> cards, List<String> cardSizes)
    {
        public boolean isEmpty() {
            return this.gamemodes.isEmpty() && this.kits.isEmpty() && this.cards.isEmpty() && this.cardSizes.isEmpty();
        }
    }

    public record HotswapConfig (int minimumExpiration, int maximumExpiration, int recoveryTime, boolean showExpirationAsDurability){}

    private final ConfigDataAccessor config;

    Map<ConfigurationOption<?>, Object> options;

    public BingoConfigurationData(ConfigDataAccessor config) {
        this.config = config;
        this.options = new HashMap<>();

        // General
        setOptionValue(BingoOptions.CONFIGURATION, BingoOptions.PluginConfiguration.valueOf(config.getString("configuration", "SINGULAR")));
        setOptionValue(BingoOptions.DEFAULT_WORLD_NAME, config.getString("defaultWorldName", "world"));
        setOptionValue(BingoOptions.LANGUAGE, "languages/" + config.getString("language", "en_us.yml"));
        setOptionValue(BingoOptions.SAVE_PLAYER_STATISTICS, config.getBoolean("savePlayerStatistics", false));
        setOptionValue(BingoOptions.SEND_COMMAND_AFTER_GAME_ENDS, config.getString("sendCommandAfterGameEnds", ""));
        setOptionValue(BingoOptions.VOTE_USING_COMMANDS_ONLY, config.getBoolean("voteUsingCommandsOnly", false));
        setOptionValue(BingoOptions.SELECT_TEAMS_USING_COMMANDS_ONLY, config.getBoolean("selectTeamsUsingCommandsOnly", false));
        setOptionValue(BingoOptions.DISABLE_SCOREBOARD_SIDEBAR, config.getBoolean("disableScoreboardSidebar", false));
        // TODO: re-enable resource pack
//        setOptionValue(ConfigurationOption.USE_INCLUDED_RESOURCE_PACK, config.getBoolean("useIncludedResourcePack", true));
        setOptionValue(BingoOptions.USE_INCLUDED_RESOURCE_PACK, false);
        setOptionValue(BingoOptions.ENABLE_DEBUG_LOGGING, config.getBoolean("enableDebugLogging", false));

        // Lobby
        setOptionValue(BingoOptions.SINGLE_PLAYER_TEAMS, config.getBoolean("singlePlayerTeams", false));
        setOptionValue(BingoOptions.MINIMUM_PLAYER_COUNT, config.getInt("minimumPlayerCount", 4));
        setOptionValue(BingoOptions.PLAYER_WAIT_TIME, config.getInt("playerWaitTime", 30));
        setOptionValue(BingoOptions.GAME_RESTART_TIME, config.getInt("gameRestartTime", 20));
        setOptionValue(BingoOptions.USE_VOTE_SYSTEM, config.getBoolean("useVoteSystem", true));
        setOptionValue(BingoOptions.PREVENT_PLAYER_GRIEFING, config.getBoolean("preventPlayerGriefing", true));
        setOptionValue(BingoOptions.VOTE_LIST, new VoteList(
                config.getList("voteList.gamemodes", TagDataType.STRING),
                config.getList("voteList.kits", TagDataType.STRING),
                config.getList("voteList.cards", TagDataType.STRING),
                config.getList("voteList.cardsizes", TagDataType.STRING)));

        // Gameplay
        setOptionValue(BingoOptions.STARTING_COUNTDOWN_TIME, config.getInt("startingCountdownTime", 10));
        setOptionValue(BingoOptions.TELEPORT_MAX_DISTANCE, config.getInt("teleportMaxDistance", 1000000));
        setOptionValue(BingoOptions.PLAYER_TELEPORT_STRATEGY, BingoOptions.PlayerTeleportStrategy.valueOf(config.getString("playerTeleportStrategy", "ALL")));
        setOptionValue(BingoOptions.TELEPORT_AFTER_DEATH, config.getBoolean("teleportBackAfterDeathMessage", true));
        setOptionValue(BingoOptions.TELEPORT_AFTER_DEATH_PERIOD, config.getInt("teleportAfterDeathPeriod", 60));
        setOptionValue(BingoOptions.GO_UP_WAND_UP_DISTANCE, config.getInt("GoUpWand.upDistance", 75));
        setOptionValue(BingoOptions.GO_UP_WAND_DOWN_DISTANCE, config.getInt("GoUpWand.downDistance", 5));
        setOptionValue(BingoOptions.GO_UP_WAND_COOLDOWN, config.getDouble("GoUpWand.cooldown", 5.0D));
        setOptionValue(BingoOptions.GO_UP_WAND_PLATFORM_LIFETIME, config.getInt("GoUPWand.platformLifetime", 10));
        setOptionValue(BingoOptions.GRACE_PERIOD, config.getInt("gracePeriod", 30));
        setOptionValue(BingoOptions.REMOVE_TASK_ITEMS, config.getBoolean("removeTaskItems", true));
        setOptionValue(BingoOptions.ENABLE_TEAM_CHAT, config.getBoolean("enableTeamChat", true));
        setOptionValue(BingoOptions.KEEP_SCOREBOARD_VISIBLE, config.getBoolean("keepScoreboardVisible", true));
        setOptionValue(BingoOptions.SHOW_PLAYER_IN_SCOREBOARD, config.getBoolean("showPlayerInScoreboard", true));
        setOptionValue(BingoOptions.DISABLE_ADVANCEMENTS, config.getBoolean("disableAdvancements", false));
        setOptionValue(BingoOptions.DISABLE_STATISTICS, config.getBoolean("disableStatistics", false));
        setOptionValue(BingoOptions.END_GAME_WITHOUT_TEAMS, config.getBoolean("endGameWithoutTeams", true));
        setOptionValue(BingoOptions.USE_MAP_RENDERER, config.getBoolean("useMapRenderer", true));
        setOptionValue(BingoOptions.SHOW_ADVANCEMENT_ITEMS, config.getBoolean("showAdvancementItems", true));
        setOptionValue(BingoOptions.ALLOW_VIEWING_ALL_CARDS, config.getBoolean("allowViewingAllCard", true));
        setOptionValue(BingoOptions.HOTSWAP_CONFIG, new HotswapConfig(
                config.getInt("hotswapMode.minimumExpirationTime", 3),
                config.getInt("hotswapMode.maximumExpirationTime", 20),
                config.getInt("hotswapMode.recoverTime", 10),
                config.getBoolean("hotswapMode.showExpirationAsDurability", true)));

        // Player
        setOptionValue(BingoOptions.SAVE_PLAYER_INFORMATION, config.getBoolean("savePlayerInformation", true));
        setOptionValue(BingoOptions.LOAD_PLAYER_INFORMATION_STRATEGY, BingoOptions.LoadPlayerInformationStrategy.valueOf(
                config.getString("loadPlayerInformationStrategy", "AFTER_LEAVING_WORLD")));

        // Configuration: MULTIPLE
        setOptionValue(BingoOptions.DEFAULT_WORLDS, new ConfigurationOption.StringList(config.getList("defaultWorlds", TagDataType.STRING)));
        //FIXME: implement
        setOptionValue(BingoOptions.RESET_DEFAULT_WORLDS, config.getBoolean("resetDefaultWorlds", true));
        setOptionValue(BingoOptions.CUSTOM_WORLD_GENERATION, config.getString("customWorldGeneration", "bingoreloaded:small"));
    }

    public <DataType> DataType getOptionValue(@Nullable ConfigurationOption<DataType> option) {
        if (!options.containsKey(option))
        {
            if (option != null)
            {
                ConsoleMessenger.bug("Cannot read config option " + option.getConfigName(), this);
            }
            else
            {
                ConsoleMessenger.bug("Config option does not exist, throwing error!", this);
            }

            return null;
        }
        return (DataType) options.get(option);
    }

    public <DataType> void setOptionValue(@NotNull ConfigurationOption<DataType> option, DataType value) {
        options.put(option, value);
    }

    /**
     * @return true if the value was set to the option successfully.
     */
    public <DataType> boolean setOptionValueFromString(@NotNull ConfigurationOption<DataType> option, String value) {
        Optional<DataType> someValue = option.fromString(value);

        if (someValue.isPresent()) {
            DataType val = someValue.get();
            options.put(option, val);
            //TODO: check if this needs to be done for setOptionValue as well...
            option.toDataStorage(config, val);
            config.saveChanges();
            return true;
        } else {
            return false;
        }
    }

    public @NotNull Optional<ConfigurationOption<?>> getOptionFromName(String name) {
        for (var o : options.keySet()) {
            if (o.getConfigName().equals(name)) {
                return Optional.of(o);
            }
        }
        return Optional.empty();
    }

    public Set<ConfigurationOption<?>> getAvailableOptions() {
        return options.keySet();
    }
}
