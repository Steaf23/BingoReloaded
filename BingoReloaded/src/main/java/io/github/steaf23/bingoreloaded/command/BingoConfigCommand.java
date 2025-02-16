package io.github.steaf23.bingoreloaded.command;

import io.github.steaf23.bingoreloaded.command.core.CommandTemplate;
import io.github.steaf23.bingoreloaded.command.core.SubCommand;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.data.config.ConfigurationOption;
import io.github.steaf23.bingoreloaded.util.BingoPlayerSender;
import io.github.steaf23.playerdisplay.PlayerDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Optional;

public class BingoConfigCommand extends CommandTemplate
{
    private final BingoConfigurationData configuration;

    public BingoConfigCommand(BingoConfigurationData configuration) {
        super(true, List.of("bingo.admin"));
        this.configuration = configuration;
    }

    private boolean readOption(CommandSender sender, String optionKey) {
        Optional<ConfigurationOption<?>> someOption = configuration.getOptionFromName(optionKey);

        if (someOption.isEmpty()) {
            BingoPlayerSender.sendMessage(PlayerDisplay.MINI_BUILDER.deserialize("Config option '<red>" + optionKey + "</red>' doesn't exist."), sender);
            return false;
        }

        ConfigurationOption<?> option = someOption.get();
        String value = configuration.getOptionValue(option).toString();

        BingoPlayerSender.sendMessage(
                PlayerDisplay.MINI_BUILDER.deserialize("Config option <yellow>" + optionKey + "</yellow> is set to: ")
                        .append(Component.text(value).color(getColorOfOptionValue(value))), sender);
        return true;
    }

    private boolean writeOption(CommandSender sender, String optionKey, String value) {
        Optional<ConfigurationOption<?>> someOption = configuration.getOptionFromName(optionKey);

        if (someOption.isEmpty()) {
            BingoPlayerSender.sendMessage(PlayerDisplay.MINI_BUILDER.deserialize("Config option '<red>" + optionKey + "</red>' doesn't exist."), sender);
            return false;
        }

        ConfigurationOption<?> option = someOption.get();

        if (option.isLocked()) {
            BingoPlayerSender.sendMessage(PlayerDisplay.MINI_BUILDER.deserialize("<red>This option is not (yet) available, please wait for a future update.</red>"), sender);
            return false;
        }
        if (!option.canBeEdited()) {
            BingoPlayerSender.sendMessage(PlayerDisplay.MINI_BUILDER.deserialize("<red>This option cannot be changed in-game. Please change it in the config.yml file and restart the server."), sender);
            return false;
        }
        if (!configuration.setOptionValueFromString(option, value)) {
            BingoPlayerSender.sendMessage(PlayerDisplay.MINI_BUILDER.deserialize("The value of option <yellow>" + optionKey + "</yellow> cannot be set to value <red>" + value), sender);
            return false;
        }

        String newValue = configuration.getOptionValue(option).toString();
        BingoPlayerSender.sendMessage(PlayerDisplay.MINI_BUILDER.deserialize("Value of option <yellow>" + optionKey + "</yellow> has been set to: ")
                .append(Component.text(newValue).color(getColorOfOptionValue(newValue))), sender);
        switch (option.getEditUpdateTime()) {
            case IMMEDIATE -> {}
            case AFTER_GAME -> {
                BingoPlayerSender.sendMessage(PlayerDisplay.MINI_BUILDER.deserialize("<gold> This option will be applied to the world at the end of the current/ upcoming game"), sender);
            }
            case AFTER_SESSION -> {
                BingoPlayerSender.sendMessage(PlayerDisplay.MINI_BUILDER.deserialize("<gold> This option will be applied after the server has restarted, on a new world in configuration MULTIPLE, or using the <red>/bingo reload</red> command"), sender);
            }
            case AFTER_SERVER_RESTART -> {
                BingoPlayerSender.sendMessage(PlayerDisplay.MINI_BUILDER.deserialize("<gold> This option will be applied after the server has been restarted, or using the <red>/bingo reload</red> command if it can be reloaded dynamically"), sender);
            }
        }

        // Perform additional setup to configure some options
        PlayerDisplay.enableDebugLogging(configuration.getOptionValue(BingoOptions.ENABLE_DEBUG_LOGGING));

        return true;
    }
    private List<String> allOptionKeys() {
        return allOptionKeys(false);
    }

    private List<String> allOptionKeys(boolean onlyEditable) {
        return configuration.getAvailableOptions().stream()
                .filter(o -> o.canBeEdited() || !onlyEditable)
                .map(ConfigurationOption::getConfigName)
                .toList();
    }

    @Override
    public SubCommand createCommand() {
        return new SubCommand("bingoconfig", args -> {
            if (getCurrentSender() == null) {
                return false;
            }

            if (args.length == 1) {
                return readOption(getCurrentSender(), args[0]);
            }
            if (args.length == 2) {
                return writeOption(getCurrentSender(), args[0], args[1]);
            }

            return false;
        }).addTabCompletion(args ->
                switch (args.length) {
                    case 1 -> allOptionKeys(true);
                    default -> List.of();
                })
                .addUsage("<option> [new_value]");
    }

    private static boolean isValueNumeric(String value) {
        return value.matches("-?\\d+(\\.\\d+)?");
    }

    private TextColor getColorOfOptionValue(String value) {
        TextColor result = NamedTextColor.BLUE;
        if (BingoConfigCommand.isValueNumeric(value)) {
            result = NamedTextColor.AQUA;
        }
        else if (value.equals("false")) {
            result = NamedTextColor.RED;
        }
        else if (value.equals("true")) {
            result = NamedTextColor.GREEN;
        }

        return result;
    }
}
