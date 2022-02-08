package me.steven.bingoreloaded;

import me.steven.bingoreloaded.cards.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

public class BingoCommand implements CommandExecutor
{
    public BingoGame gameInstance;

    public BingoCommand(BingoGame game)
    {
        gameInstance = game;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args)
    {
        if (args.length > 0)
        {
            switch (args[0])
            {
                case "setup":
                    if (args.length == 2)
                    {
                        gameInstance.setup(BingoGameMode.fromCommand(args[1]));
                    }
                    else if (args.length == 1)
                    {
                        gameInstance.setup(BingoGameMode.REGULAR);
                    }
                    break;
                case "join":
                    if (!(commandSender instanceof Player player)) return false;

                    gameInstance.teamManager.openTeamSelector(player);
                    break;
                case "start":
                    gameInstance.start();
                    break;
            }
        }

        return false;
    }
}
