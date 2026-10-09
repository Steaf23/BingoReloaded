package io.github.steaf23.bingoreloaded.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.data.config.ConfigurationOption;
import io.github.steaf23.bingoreloaded.data.config.EnumOption;
import io.github.steaf23.bingoreloaded.protocol.message.MessageParser;
import io.github.steaf23.bingoreloaded.util.BingoPlayerSender;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BingoConfigCommand<Source> extends MappedCommand<Source> {
    private final BingoConfigurationData configuration;

    public BingoConfigCommand(BingoConfigurationData configuration, Settings<Source> settings) {
		super("bingoconfig", List.of("bingo.admin"), settings);
		this.configuration = configuration;

		for (ConfigurationOption<?> option : allOptions(true)) {

			var valueArg = argument("value", option.argumentType())
					.executes(executor((source, context) -> {
						return writeOption(source.user(), option, context);
					}));
			if (option instanceof EnumOption<?> enumOption) {
				valueArg.suggests((context, builder) -> {
					return suggestAll(builder, Arrays.stream(enumOption.enumClass().getEnumConstants()).toList(), Enum::name);
				});
			}

			then(literal(option.getConfigName())
					.executes(executor((source, context) -> {
						return readOption(source.user(), option.getConfigName());
					}))
					.then(valueArg));
		}
    }

    private int readOption(Audience sender, String optionKey) {
        Optional<ConfigurationOption<?>> someOption = configuration.getOptionFromName(optionKey);

        if (someOption.isEmpty()) {
            BingoPlayerSender.sendMessage(MessageParser.MINI_BUILDER.deserialize("Config option '<red>" + optionKey + "</red>' doesn't exist."), sender);
            return 0;
        }

        ConfigurationOption<?> option = someOption.get();
        String value = configuration.getOptionValue(option).toString();

        BingoPlayerSender.sendMessage(
                MessageParser.MINI_BUILDER.deserialize("Config option <yellow>" + optionKey + "</yellow> is set to: ")
                        .append(Component.text(value).color(getColorOfOptionValue(value))), sender);
        return Command.SINGLE_SUCCESS;
    }

    private <T> int writeOption(Audience sender, ConfigurationOption<T> option, CommandContext<?> context) {
        if (option.isLocked()) {
            BingoPlayerSender.sendMessage(MessageParser.MINI_BUILDER.deserialize("<red>This option is not (yet) available, please wait for a future update.</red>"), sender);
            return 0;
        }
        if (!option.canBeEdited()) {
            BingoPlayerSender.sendMessage(MessageParser.MINI_BUILDER.deserialize("<red>This option cannot be changed in-game. Please change it in the config.yml file and restart the server."), sender);
            return 0;
        }
		Optional<T> value = option.fromArgument(context, "value");
		if (value.isEmpty()) {
			BingoPlayerSender.sendMessage(MessageParser.MINI_BUILDER.deserialize("The value of option <yellow>" + option.getConfigName() + "</yellow> cannot be set to value <red>" + value), sender);
			return 0;
		}
        configuration.setOptionValue(option, value.get());

		String newValue = value.get().toString();

        BingoPlayerSender.sendMessage(MessageParser.MINI_BUILDER.deserialize("Value of option <yellow>" + option.getConfigName() + "</yellow> has been set to: ")
                .append(Component.text(newValue).color(getColorOfOptionValue(newValue))), sender);
        switch (option.getEditUpdateTime()) {
			case IMMEDIATE -> {
			}
			case AFTER_GAME -> {
				BingoPlayerSender.sendMessage(MessageParser.MINI_BUILDER.deserialize("<gold> This option will be applied to the world at the end of the current/ upcoming game"), sender);
			}
			case AFTER_SESSION -> {
				BingoPlayerSender.sendMessage(MessageParser.MINI_BUILDER.deserialize("<gold> This option will be applied after the server has restarted, on a new world in configuration MULTIPLE, or using the <red>/bingo reload</red> command"), sender);
			}
			case AFTER_SERVER_RESTART -> {
				BingoPlayerSender.sendMessage(MessageParser.MINI_BUILDER.deserialize("<gold> This option will be applied after the server has been restarted, or using the <red>/bingo reload</red> command if it can be reloaded dynamically"), sender);
			}
		}

        return Command.SINGLE_SUCCESS;
    }

    private List<String> allOptionKeys(boolean onlyEditable) {
        return configuration.getAvailableOptions().stream()
                .filter(o -> o.canBeEdited() || !onlyEditable)
                .map(ConfigurationOption::getConfigName)
                .toList();
    }

	private List<ConfigurationOption<?>> allOptions(boolean onlyEditable) {
		return configuration.getAvailableOptions().stream()
				.filter(o -> o.canBeEdited() || !onlyEditable)
				.toList();
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
		else if (value.equals("null")) {
			result = NamedTextColor.DARK_PURPLE;
		}

        return result;
    }
}
