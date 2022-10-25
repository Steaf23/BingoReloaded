package io.github.steaf23.bingoreloaded.command;

import io.github.steaf23.bingoreloaded.BingoGame;
import io.github.steaf23.bingoreloaded.BingoGameSettings;
import io.github.steaf23.bingoreloaded.BingoGamemode;
import io.github.steaf23.bingoreloaded.data.BingoCardsData;
import io.github.steaf23.bingoreloaded.gui.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.gui.cards.CardSize;
import io.github.steaf23.bingoreloaded.player.PlayerKit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AutoBingoCommand implements CommandExecutor
{
    private BingoGameSettings settings;

    public AutoBingoCommand(BingoGameSettings settings)
    {
        this.settings = settings;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] args)
    {
        if (!(commandSender instanceof Player p) || p.hasPermission("bingo.admin"))
            return false;

        if (args.length > 0)
        {
            if (args.length > 1)
            {
                switch (args[0])
                {
                    case "kit":
                        try
                        {
                            settings.setKit(PlayerKit.valueOf(args[1].toUpperCase()));
                        }
                        catch (IllegalArgumentException e)
                        {
                            //TODO: send command failed
                            return false;
                        }
                        return true;

                    case "effects":
                        if (args[1] == "all")
                        {
                            settings.setEffects(EffectOptionFlags.ALL_ON);
                            return true;
                        }
                        else if (args[1] == "none")
                        {
                            settings.setEffects(EffectOptionFlags.ALL_OFF);
                            return true;
                        }

                        if (args.length > 2)
                        {
                            try
                            {
                                if (args[2] == "true")
                                {
                                    settings.effects.add(EffectOptionFlags.valueOf(args[1].toUpperCase()));
                                    return true;
                                }
                                else if (args[2] == "false")
                                {
                                    settings.effects.remove(EffectOptionFlags.valueOf(args[1].toUpperCase()));
                                    return true;
                                }
                            }
                            catch (IllegalArgumentException e)
                            {
                                //TODO: send command failed
                                return false;
                            }
                        }

                        //TODO: send command failed
                        return false;

                    case "card":
                        String card = args[1];
                        if (BingoCardsData.getCardNames().contains(card))
                        {
                            settings.card = card;
                            return true;
                        }
                        //TODO: send command failed
                        return false;

                    case "teammax":
                        try
                        {
                            int max = Integer.parseInt(args[1]);
                            if (max <= 0)
                            {
                                //TODO: send command failed
                                return false;
                            }

                            //TODO: add team max variable to teammanager
                        }
                        catch (NumberFormatException e)
                        {
                            //TODO: send command failed
                            return false;
                        }
                        return true;

                    case "start":
                        try
                        {
                            settings.mode = BingoGamemode.valueOf(args[1]);
                            if (args.length > 2)
                            {
                                switch (args[2])
                                {
                                    case "3":
                                        settings.cardSize = CardSize.X3;
                                        break;
                                    default:
                                        settings.cardSize = CardSize.X5;
                                        break;
                                }
                            }
                        }
                        catch (IllegalArgumentException e)
                        {
                            //TODO: send command failed
                            return false;
                        }

                        return true;

                    default:
                        //TODO: send command failed
                        return false;
                }
            }
            else
            {
                //TODO: send command failed
                return false;
            }
        }
        else
        {
            //TODO: send command failed
            return false;
        }
    }
}
