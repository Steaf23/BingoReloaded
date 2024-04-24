package io.github.steaf23.bingoreloaded.command;


import io.github.steaf23.bingoreloaded.data.world.WorldManager;
import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BingoTestCommand implements TabExecutor
{
    private final JavaPlugin plugin;

    public BingoTestCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, String[] args) {
        if (commandSender instanceof Player p && !p.hasPermission("bingo.admin")) {
            return false;
        }

        if (args.length < 3) {
            return false;
        }

        switch (args[0]) {
            case "world" -> {
                if (args.length < 3)
                {
                    break;
                }


                String worldName = args[1];
                boolean doesWorldExist = WorldManager.doesWorldExist(plugin, worldName);
                switch (args[2])
                {
                    case "tp":
                        if (!(commandSender instanceof Player p)) {
                            return false;
                        }
                        if (!doesWorldExist)
                        {
                            Message.log("Cannot TP player to non-existing world " + worldName);
                            return false;
                        }
                        WorldManager.getOrCreateWorldGroup(plugin, worldName).teleportPlayer(p);
                        break;
                    case "create":
                        if (doesWorldExist)
                        {
                            Message.log("Cannot create world " + worldName + ", because it already exists!");
                        }
                        WorldManager.getOrCreateWorldGroup(plugin, worldName);
                        break;
                    case "destroy":
                        if (!doesWorldExist) {
                            Message.log("Cannot remove non-existing world " + worldName);
                            return false;
                        }
                        WorldManager.destroyWorldGroup(plugin, WorldManager.getOrCreateWorldGroup(plugin, worldName));
                        break;
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
