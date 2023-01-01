package io.github.steaf23.bingoreloaded.command;

import io.github.steaf23.bingoreloaded.data.BingoCardsData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AutoBingoTabCompleter implements TabCompleter
{
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] args)
    {
        switch (args.length)
        {
            case 1:
                return List.of("kit", "effects", "card", "team_max", "start");
            case 2:
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
            case 3:
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
