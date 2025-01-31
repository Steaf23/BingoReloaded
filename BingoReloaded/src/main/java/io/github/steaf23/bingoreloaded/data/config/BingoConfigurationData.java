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
import java.util.function.Function;

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
        setDefaultOption(BingoOptions.CONFIGURATION, name -> BingoOptions.PluginConfiguration.valueOf(config.getString(name, "SINGULAR")));
        setDefaultOption(BingoOptions.DEFAULT_WORLD_NAME, name -> config.getString(name, "world"));
        setDefaultOption(BingoOptions.LANGUAGE, name -> "languages/" + config.getString(name, "en_us.yml"));
        setDefaultOption(BingoOptions.SAVE_PLAYER_STATISTICS, name -> config.getBoolean(name, false));
        setDefaultOption(BingoOptions.SEND_COMMAND_AFTER_GAME_ENDS, name -> config.getString(name, ""));
        setDefaultOption(BingoOptions.VOTE_USING_COMMANDS_ONLY, name -> config.getBoolean(name, false));
        setDefaultOption(BingoOptions.SELECT_TEAMS_USING_COMMANDS_ONLY, name -> config.getBoolean(name, false));
        setDefaultOption(BingoOptions.DISABLE_SCOREBOARD_SIDEBAR, name -> config.getBoolean(name, false));
        // TODO: re-enable resource pack
//        setOptionValue(ConfigurationOption.USE_INCLUDED_RESOURCE_PACK, config.getBoolean("useIncludedResourcePack", true));
        setDefaultOption(BingoOptions.USE_INCLUDED_RESOURCE_PACK, name -> false);
        setDefaultOption(BingoOptions.USE_MAP_RENDERER, name -> config.getBoolean(name, true));
        setDefaultOption(BingoOptions.SHOW_UNIQUE_TASK_ITEMS, name -> config.getBoolean(name, true));
        setDefaultOption(BingoOptions.ENABLE_DEBUG_LOGGING, name -> config.getBoolean(name, false));

        // Lobby
        setDefaultOption(BingoOptions.SINGLE_PLAYER_TEAMS, name -> config.getBoolean(name, false));
        setDefaultOption(BingoOptions.MINIMUM_PLAYER_COUNT, name -> config.getInt(name, 4));
        setDefaultOption(BingoOptions.PLAYER_WAIT_TIME, name -> config.getInt(name, 30));
        setDefaultOption(BingoOptions.GAME_RESTART_TIME, name -> config.getInt(name, 20));
        setDefaultOption(BingoOptions.USE_VOTE_SYSTEM, name -> config.getBoolean(name, true));
        setDefaultOption(BingoOptions.PREVENT_PLAYER_GRIEFING, name -> config.getBoolean(name, true));
        setDefaultOption(BingoOptions.VOTE_LIST, name -> new VoteList(
                config.getList("voteList.gamemodes", TagDataType.STRING),
                config.getList("voteList.kits", TagDataType.STRING),
                config.getList("voteList.cards", TagDataType.STRING),
                config.getList("voteList.cardsizes", TagDataType.STRING)));

        // Gameplay
        setDefaultOption(BingoOptions.STARTING_COUNTDOWN_TIME, name -> config.getInt(name, 10));
        setDefaultOption(BingoOptions.TELEPORT_MAX_DISTANCE, name -> config.getInt(name, 1000000));
        setDefaultOption(BingoOptions.PLAYER_TELEPORT_STRATEGY, name -> BingoOptions.PlayerTeleportStrategy.valueOf(config.getString(name, "ALL")));
        setDefaultOption(BingoOptions.TELEPORT_AFTER_DEATH, name -> config.getBoolean(name, true));
        setDefaultOption(BingoOptions.TELEPORT_AFTER_DEATH_PERIOD, name -> config.getInt(name, 60));
        setDefaultOption(BingoOptions.GO_UP_WAND_UP_DISTANCE, name -> config.getInt(name, 75));
        setDefaultOption(BingoOptions.GO_UP_WAND_DOWN_DISTANCE, name -> config.getInt(name, 5));
        setDefaultOption(BingoOptions.GO_UP_WAND_COOLDOWN, name -> config.getDouble(name, 5.0D));
        setDefaultOption(BingoOptions.GO_UP_WAND_PLATFORM_LIFETIME, name -> config.getInt(name, 10));
        setDefaultOption(BingoOptions.GRACE_PERIOD, name -> config.getInt(name, 30));
        setDefaultOption(BingoOptions.REMOVE_TASK_ITEMS, name -> config.getBoolean(name, true));
        setDefaultOption(BingoOptions.ENABLE_TEAM_CHAT, name -> config.getBoolean(name, true));
        setDefaultOption(BingoOptions.KEEP_SCOREBOARD_VISIBLE, name -> config.getBoolean(name, true));
        setDefaultOption(BingoOptions.SHOW_PLAYER_IN_SCOREBOARD, name -> config.getBoolean(name, true));
        setDefaultOption(BingoOptions.DISABLE_ADVANCEMENTS, name -> config.getBoolean(name, false));
        setDefaultOption(BingoOptions.DISABLE_STATISTICS, name -> config.getBoolean(name, false));
        setDefaultOption(BingoOptions.END_GAME_WITHOUT_TEAMS, name -> config.getBoolean(name, true));
        setDefaultOption(BingoOptions.HOTSWAP_CONFIG, name -> new HotswapConfig(
                config.getInt(name + "minimumExpirationTime", 3),
                config.getInt(name + "maximumExpirationTime", 20),
                config.getInt(name + "recoverTime", 10),
                config.getBoolean(name + "showExpirationAsDurability", true)));
        setDefaultOption(BingoOptions.ALLOW_VIEWING_ALL_CARDS, name -> config.getBoolean(name, true));

        // Player
        setDefaultOption(BingoOptions.SAVE_PLAYER_INFORMATION, name -> config.getBoolean(name, true));
        setDefaultOption(BingoOptions.LOAD_PLAYER_INFORMATION_STRATEGY, name -> BingoOptions.LoadPlayerInformationStrategy.valueOf(
                config.getString(name, "AFTER_LEAVING_WORLD")));

        // Configuration: MULTIPLE
        setDefaultOption(BingoOptions.DEFAULT_WORLDS, name -> new ConfigurationOption.StringList(config.getList(name, TagDataType.STRING)));
        //FIXME: implement
        setDefaultOption(BingoOptions.CLEAR_DEFAULT_WORLDS, name -> config.getBoolean(name, true));
        setDefaultOption(BingoOptions.CUSTOM_WORLD_GENERATION, name -> config.getString(name, "bingoreloaded:small"));
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
        if (option.isLocked())
            return;

        options.put(option, value);
    }

    /**
     * @return true if the value was set to the option successfully.
     */
    public <DataType> boolean setOptionValueFromString(@NotNull ConfigurationOption<DataType> option, String value) {
        Optional<DataType> someValue = option.fromString(value);

        if (someValue.isPresent()) {
            DataType val = someValue.get();
            setOptionValue(option, val);
            //TODO: check if this needs to be inside setOptionValue
            option.toDataStorage(config, val);
            config.saveChanges();
            return true;
        } else {
            return false;
        }
    }

    public <T> void setDefaultOption(ConfigurationOption<T> option, Function<String, T> defaultValue) {
        setOptionValue(option, defaultValue.apply(option.getConfigName()));
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
