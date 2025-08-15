package io.github.steaf23.bingoreloaded.action;

import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.data.config.ConfigurationOption;
import io.github.steaf23.bingoreloaded.lib.action.ActionResult;
import io.github.steaf23.bingoreloaded.lib.action.ActionTree;
import io.github.steaf23.bingoreloaded.lib.util.ComponentUtils;
import io.github.steaf23.bingoreloaded.util.BingoPlayerSender;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Stream;

public class BingoConfigAction extends ActionTree {
    private final BingoConfigurationData configuration;

    public BingoConfigAction(BingoConfigurationData configuration) {
		super("bingoconfig", List.of("bingo.admin"));
		this.configuration = configuration;

        setAction((args) -> {
            if (getLastUser() == null) {
                return ActionResult.IGNORED;
            }

            if (args.length == 1) {
                return readOption(getLastUser(), args[0]);
            }
            if (args.length >= 2) {
                return writeOption(getLastUser(), args[0], String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
            }

            return ActionResult.INCORRECT_USE;
        }).addTabCompletion(args ->
                switch (args.length) {
                    case 1 -> allOptionKeys(true);
                    default -> List.of();
                })
                .addUsage("<option> [new_value]");
    }

    private ActionResult readOption(Audience sender, String optionKey) {
        Optional<ConfigurationOption<?>> someOption = configuration.getOptionFromName(optionKey);

        if (someOption.isEmpty()) {
            BingoPlayerSender.sendMessage(ComponentUtils.MINI_BUILDER.deserialize("Config option '<red>" + optionKey + "</red>' doesn't exist."), sender);
            return ActionResult.INCORRECT_USE;
        }

        ConfigurationOption<?> option = someOption.get();
        String value = configuration.getOptionValue(option).toString();

        BingoPlayerSender.sendMessage(
                ComponentUtils.MINI_BUILDER.deserialize("Config option <yellow>" + optionKey + "</yellow> is set to: ")
                        .append(Component.text(value).color(getColorOfOptionValue(value))), sender);
        return ActionResult.SUCCESS;
    }

    private ActionResult writeOption(Audience sender, String optionKey, String value) {
        Optional<ConfigurationOption<?>> someOption = configuration.getOptionFromName(optionKey);

        if (someOption.isEmpty()) {
            BingoPlayerSender.sendMessage(ComponentUtils.MINI_BUILDER.deserialize("Config option '<red>" + optionKey + "</red>' doesn't exist."), sender);
            return ActionResult.INCORRECT_USE;
        }

        ConfigurationOption<?> option = someOption.get();

        if (option.isLocked()) {
            BingoPlayerSender.sendMessage(ComponentUtils.MINI_BUILDER.deserialize("<red>This option is not (yet) available, please wait for a future update.</red>"), sender);
            return ActionResult.INCORRECT_USE;
        }
        if (!option.canBeEdited()) {
            BingoPlayerSender.sendMessage(ComponentUtils.MINI_BUILDER.deserialize("<red>This option cannot be changed in-game. Please change it in the config.yml file and restart the server."), sender);
            return ActionResult.IGNORED; // logically this is incorrect_use but technically the command was not used incorrectly.
        }
        if (!configuration.setOptionValueFromString(option, value)) {
            BingoPlayerSender.sendMessage(ComponentUtils.MINI_BUILDER.deserialize("The value of option <yellow>" + optionKey + "</yellow> cannot be set to value <red>" + value), sender);
            return ActionResult.IGNORED; // logically this is incorrect_use but technically the command was not used incorrectly.
        }

        String newValue = configuration.getOptionValue(option).toString();
        BingoPlayerSender.sendMessage(ComponentUtils.MINI_BUILDER.deserialize("Value of option <yellow>" + optionKey + "</yellow> has been set to: ")
                .append(Component.text(newValue).color(getColorOfOptionValue(newValue))), sender);
        switch (option.getEditUpdateTime()) {
			case IMMEDIATE -> {
			}
			case AFTER_GAME -> {
				BingoPlayerSender.sendMessage(ComponentUtils.MINI_BUILDER.deserialize("<gold> This option will be applied to the world at the end of the current/ upcoming game"), sender);
			}
			case AFTER_SESSION -> {
				BingoPlayerSender.sendMessage(ComponentUtils.MINI_BUILDER.deserialize("<gold> This option will be applied after the server has restarted, on a new world in configuration MULTIPLE, or using the <red>/bingo reload</red> command"), sender);
			}
			case AFTER_SERVER_RESTART -> {
				BingoPlayerSender.sendMessage(ComponentUtils.MINI_BUILDER.deserialize("<gold> This option will be applied after the server has been restarted, or using the <red>/bingo reload</red> command if it can be reloaded dynamically"), sender);
			}
		}

        return ActionResult.SUCCESS;
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

    private static boolean isValueNumeric(String value) {
        return value.matches("-?\\d+(\\.\\d+)?");
    }

    private TextColor getColorOfOptionValue(String value) {
        TextColor result = NamedTextColor.BLUE;
        if (BingoConfigAction.isValueNumeric(value)) {
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
