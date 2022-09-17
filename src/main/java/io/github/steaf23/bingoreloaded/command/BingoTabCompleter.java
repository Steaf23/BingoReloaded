package io.github.steaf23.bingoreloaded.command;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BingoTabCompleter implements TabCompleter
{
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if (args.length == 1)
        {
            if (sender instanceof Player player && !player.hasPermission("bingo.admin"))
            {
                return List.of("join", "getcard", "back", "leave");
            }
            else
            {
                return List.of("join", "getcard", "back", "leave", "start", "end", "creator", "deathmatch");
            }
        }
        return null;
    }
}
