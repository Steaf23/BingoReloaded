package io.github.steaf23.bingoreloaded.action;

import io.github.steaf23.bingoreloaded.lib.action.ActionTree;
import io.github.steaf23.bingoreloaded.lib.api.ActionUser;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandlePaper;
import io.github.steaf23.bingoreloaded.lib.util.ComponentUtils;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CommandTemplate implements TabExecutor
{
    private final boolean allowConsole;
    private final ActionTree command;

    public CommandTemplate(boolean allowConsole, ActionTree command) {
        this.command = command;
        this.allowConsole = allowConsole;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command bukkitCmd, @NotNull String s, String @NotNull [] arguments) {
        if (commandSender instanceof ConsoleCommandSender && !allowConsole) {
            return false;
        }

        ActionUser user;
        if (commandSender instanceof Player player) {
            user = new PlayerHandlePaper(player);
        } else if (commandSender instanceof ConsoleCommandSender console){
            user = new ConsoleActionUser(console);
        } else {
            ConsoleMessenger.bug("Cannot execute command for this command sender..?", this);
            return false;
        }


        if (!command.hasPermission(user)) {
            return false;
        }

        if (!command.execute(user, arguments)) {
            commandSender.sendMessage(ComponentUtils.MINI_BUILDER.deserialize("<dark_gray> - <red>Usage: " + command.usage(arguments)));
            return false;
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return this.command.tabComplete(strings);
    }
}
