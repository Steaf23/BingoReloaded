package io.github.steaf23.bingoreloaded.game.singular;

import io.github.steaf23.bingoreloaded.BingoSettingsBuilder;
import io.github.steaf23.bingoreloaded.command.AutoBingoCommand;
import io.github.steaf23.bingoreloaded.game.BingoGameManager;
import io.github.steaf23.bingoreloaded.game.BingoSession;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

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
