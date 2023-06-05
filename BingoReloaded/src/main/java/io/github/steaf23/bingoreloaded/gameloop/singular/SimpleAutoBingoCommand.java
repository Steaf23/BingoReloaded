package io.github.steaf23.bingoreloaded.gameloop.singular;

import io.github.steaf23.bingoreloaded.gameloop.BingoGameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SimpleAutoBingoCommand implements CommandExecutor
{
    private final BingoGameManager manager;

    public SimpleAutoBingoCommand(BingoGameManager manager)
    {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command autobingoCommand, @NotNull String alias, @NotNull String[] args)
    {
        return false;
    }
}
