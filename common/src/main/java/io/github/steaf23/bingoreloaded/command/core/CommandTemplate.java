package io.github.steaf23.bingoreloaded.command.core;

import io.github.steaf23.bingoreloaded.command.Executor;
import io.github.steaf23.bingoreloaded.lib.PlayerDisplay;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class CommandTemplate implements Executor
{
    private final boolean allowConsole;
    private final List<String> permissions;
    private final SubCommand command;

    private Audience currentSender = null;

    public CommandTemplate(boolean allowConsole, List<String> permissionWhitelist) {
        this.command = createCommand();
        this.allowConsole = allowConsole;
        this.permissions = permissionWhitelist;
    }

    public abstract SubCommand createCommand();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof ConsoleCommandSender && !allowConsole) {
            return false;
        }

        if (commandSender instanceof Player player) {
            boolean playerHasPermission = false;

            for (String permission: permissions) {
                if (player.hasPermission(permission))
                {
                    playerHasPermission = true;
                    break;
                }
            }

            if (!playerHasPermission) {
                return false;
            }
        }

        currentSender = commandSender;

        if (!this.command.execute(strings)) {
            commandSender.sendMessage(PlayerDisplay.MINI_BUILDER.deserialize("<dark_gray> - <red>Usage: " + this.command.usage(strings)));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return this.command.tabComplete(strings);
    }

    public Audience getCurrentSender() {
        return currentSender;
    }
}
