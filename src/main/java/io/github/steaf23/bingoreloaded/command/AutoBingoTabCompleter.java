package io.github.steaf23.bingoreloaded.command;

import io.github.steaf23.bingoreloaded.data.BingoCardsData;
import io.github.steaf23.bingoreloaded.util.FlexColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
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
                return List.of("create", "destroy", "start", "end","kit", "effects", "card", "team_max", "duration", "team");
            case 3:
                switch (args[1])
                {
                    case "kit":
                        return List.of("custom", "normal", "overpowered", "reloaded", "hardcore");
                    case "effects":
                        return List.of("all", "none", "water_breathing", "night_vision", "fire_resistance", "no_fall_damage", "card_speed");
                    case "card":
                        return BingoCardsData.getCardNames().stream().toList();
                    case "start":
                        return List.of("regular", "lockout", "complete", "countdown");
                    default:
                        return null;
                }
            case 4:
                switch (args[1])
                {
                    case "effects":
                        if (args[3].equals("all") || args[3].equals("none"))
                        {
                            return null;
                        }
                        return List.of("true", "false");
                    case "start":
                        return List.of("3", "5");
                    case "team":
                        FlexColor[] colors = FlexColor.values();
                        List<String> list = new ArrayList<>(Arrays.stream(colors).map(c -> c.name).toList());
                        list.add(0, "none");
                        return list;
                }
            default:
                return null;
        }
    }
}
