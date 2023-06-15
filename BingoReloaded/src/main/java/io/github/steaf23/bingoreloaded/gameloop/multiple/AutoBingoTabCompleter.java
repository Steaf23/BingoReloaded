package io.github.steaf23.bingoreloaded.gameloop.multiple;

import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.util.FlexColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutoBingoTabCompleter implements TabCompleter
{
    public AutoBingoTabCompleter()
    {
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String alias, @NonNull String[] args)
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
                return List.of("create", "destroy", "start", "end", "kit", "effects", "card", "countdown", "team");
            case 3:
                switch (args[1])
                {
                    case "kit":
                        return List.of("normal", "overpowered", "reloaded", "hardcore", "custom1", "custom2", "custom3", "custom4", "custom5");
                    case "effects":
                        return List.of("all", "none", "water_breathing", "night_vision", "fire_resistance", "no_fall_damage", "card_speed");
                    case "card":
                        BingoCardData cardsData = new BingoCardData();
                        return cardsData.getCardNames().stream().toList();
                    case "start":
                        return List.of("regular", "lockout", "complete");
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
