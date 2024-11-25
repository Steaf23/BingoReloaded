package io.github.steaf23.bingoreloaded.command;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.command.core.CommandTemplate;
import io.github.steaf23.bingoreloaded.command.core.SubCommand;
import io.github.steaf23.bingoreloaded.data.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.util.BingoPlayerSender;
import io.github.steaf23.playerdisplay.PlayerDisplay;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class BingoConfigCommand extends CommandTemplate
{
    private final BingoConfigurationData configuration;
    private final BingoReloaded plugin;

    public BingoConfigCommand(BingoReloaded plugin, BingoConfigurationData configuration) {
        super(true, List.of("bingo.admin"));
        this.configuration = configuration;
        this.plugin = plugin;
    }

    private boolean readOption(CommandSender sender, String optionKey) {
        if (!plugin.getConfig().contains(optionKey)) {
            BingoPlayerSender.sendMessage(PlayerDisplay.MINI_BUILDER.deserialize("Config option '<red>" + optionKey + "</red>' doesn't exist."), sender);
            return false;
        }
        String value = plugin.getConfig().get(optionKey, "INVALID (Please report!)").toString();
        TextColor valueColor = NamedTextColor.BLUE;
        if (BingoConfigCommand.isValueNumeric(value)) {
            valueColor = NamedTextColor.AQUA;
        }
        else if (value.equals("false")) {
            valueColor = NamedTextColor.RED;
        }
        else if (value.equals("true")) {
            valueColor = NamedTextColor.GREEN;
        }

        BingoPlayerSender.sendMessage(
                PlayerDisplay.MINI_BUILDER.deserialize("Config option <yellow>" + optionKey + "</yellow> is set to: ")
                        .append(Component.text(value).color(valueColor)), sender);
        return true;
    }

    private boolean writeOption(CommandSender sender, String optionKey, String value) {
        ConsoleMessenger.log("WRITING " + value + " TO " + optionKey);
        return false;
    }

    private List<String> allOptionKeys() {
        return plugin.getConfig().getKeys(false).stream().toList();
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
        }).addTabCompletion(args -> {
            return switch (args.length) {
                case 1 -> allOptionKeys();
                default -> List.of();
            };
        }).addUsage("<option> [new_value]");
    }

    private static boolean isValueNumeric(String value) {
        return value.matches("-?\\d+(\\.\\d+)?");
    }
}
