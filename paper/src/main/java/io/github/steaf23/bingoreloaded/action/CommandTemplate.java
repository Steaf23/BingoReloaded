package io.github.steaf23.bingoreloaded.action;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.lib.action.ActionTree;
import io.github.steaf23.bingoreloaded.lib.api.ActionUser;
import io.github.steaf23.bingoreloaded.lib.api.PlayerHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.platform.GameContext;
import io.github.steaf23.bingoreloaded.lib.api.platform.PaperServer;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.protocol.message.MessageParser;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;

public class CommandTemplate implements BasicCommand
{
    private final boolean allowConsole;
    private final ActionTree command;
    private final PaperServer server;
    private final BingoReloaded bingo;

    public CommandTemplate(PaperServer server, BingoReloaded bingo, boolean allowConsole, ActionTree command) {
        this.command = command;
        this.allowConsole = allowConsole;
        this.server = server;
        this.bingo = bingo;
    }

    @Override
    public void execute(@NonNull CommandSourceStack commandSourceStack, String @NonNull [] args) {
        CommandSender commandSender = commandSourceStack.getSender();
        if (commandSender instanceof ConsoleCommandSender && !allowConsole) {
            return;
        }

        ActionUser user;
        if (commandSender instanceof Player player) {
            user = new PlayerHandlePaper(server, player);
        } else if (commandSender instanceof ConsoleCommandSender console){
            user = new ConsoleActionUser(console);
        } else {
            ConsoleMessenger.bug("Cannot execute command for this command sender..?", this);
            return;
        }


        if (!command.hasPermission(user)) {
            return;
        }

        switch (command.execute(new GameContext(server, bingo), user, args)) {
            case INCORRECT_USE -> {
                commandSender.sendMessage(MessageParser.MINI_BUILDER.deserialize("<dark_gray>- <red>Usage: " + command.usage(args)));
			}
            case NO_PERMISSION -> {
                commandSender.sendMessage(Component.text("You do not have permission to execute this command.").color(NamedTextColor.RED));
			}
            case IGNORED -> {
                commandSender.sendMessage(Component.text("There was an issue running this command.").color(NamedTextColor.RED));
			}
            default -> {
            }
        }
    }

    @Override
    public @NonNull Collection<String> suggest(@NonNull CommandSourceStack commandSourceStack, String @NonNull [] args) {
        CommandSender sender = commandSourceStack.getSender();
        ActionUser user;
        if (sender instanceof Player player) {
            user = new PlayerHandlePaper(server, player);
        } else if (sender instanceof ConsoleCommandSender console){
            user = new ConsoleActionUser(console);
        } else {
            return List.of();
        }

        List<String> tabComplete = this.command.tabComplete(new GameContext(server, bingo), user, args);
        if (tabComplete == null) return null;

        return tabComplete.stream().filter(s -> StringUtils.containsIgnoreCase(s, args[args.length - 1])).toList();
    }
}
