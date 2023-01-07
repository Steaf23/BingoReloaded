package io.github.steaf23.bingoreloaded.command;

import io.github.steaf23.bingoreloaded.data.BingoCardsData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AutoBingoTabCompleter implements TabCompleter
{
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] args)
    {
        if (commandSender instanceof Player p && !p.hasPermission("bingo.admin"))
        {
            return new ArrayList<>();
        }

        switch (args.length)
        {
            case 1:
                List<String> allWorlds = new ArrayList<>();
                for (var world : Bukkit.getWorlds())
                {
                    if (!world.getName().contains("_nether") && !world.getName().contains("_the_end"))
                    {
                        allWorlds.add(world.getName());
                    }
                }
                return allWorlds;
            case 2:
                return List.of("create", "destroy", "start", "kit", "effects", "card", "team_max", "duration");
            case 3:
                switch (args[0])
                {
                    case "kit":
                        return List.of("custom", "normal", "overpowered", "reloaded", "hardcore");
                    case "effects":
                        return List.of("all", "none", "water_breathing", "night_vision", "fire_resistance", "no_fall", "card_speed");
                    case "card":
                        return BingoCardsData.getCardNames().stream().toList();
                    case "start":
                        return List.of("regular", "lockout", "complete", "countdown");
                }
            case 4:
                switch (args[1])
                {
                    case "effects":
                        if (args[2].equals("all") || args[2].equals("none"))
                        {
                            return null;
                        }
                        return List.of("true", "false");
                    case "start":
                        return List.of("3", "5");
                }
            default:
                return null;
        }
    }
}
