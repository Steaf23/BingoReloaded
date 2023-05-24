package io.github.steaf23.bingoreloaded.game.singular;

import io.github.steaf23.bingoreloaded.BingoSettingsBuilder;
import io.github.steaf23.bingoreloaded.command.AutoBingoCommand;
import io.github.steaf23.bingoreloaded.game.BingoSession;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class SimpleAutoBingoCommand implements AutoBingoCommand
{
    private final SingularGameManager manager;

    public SimpleAutoBingoCommand(SingularGameManager manager)
    {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command autobingoCommand, @NotNull String alias, @NotNull String[] args)
    {
        return false;
    }

    @Override
    public boolean start(String worldName)
    {
        return false;
    }

    @Override
    public boolean setKit(BingoSession session, BingoSettingsBuilder settings, String worldName, String[] extraArguments)
    {
        return false;
    }

    @Override
    public boolean setEffect(BingoSession session, BingoSettingsBuilder settings, String worldName, String[] extraArguments)
    {
        return false;
    }

    @Override
    public boolean setCard(BingoSettingsBuilder settings, String worldName, String[] extraArguments)
    {
        return false;
    }

    @Override
    public boolean setCountdown(BingoSettingsBuilder settings, String worldName, String[] extraArguments)
    {
        return false;
    }

    @Override
    public boolean setDuration(BingoSettingsBuilder settings, String worldName, String[] extraArguments)
    {
        return false;
    }

    @Override
    public boolean setPlayerTeam(String worldName, String[] extraArguments)
    {
        return false;
    }

    @Override
    public boolean setTeamSize(BingoSettingsBuilder settings, String worldName, String[] extraArguments)
    {
        return false;
    }

    @Override
    public boolean setGamemode(BingoSettingsBuilder settings, String worldName, String[] extraArguments)
    {
        return false;
    }

    @Override
    public boolean end(String worldName)
    {
        return false;
    }

    @Override
    public boolean preset(BingoSettingsBuilder settingsBuilder, String worldName, String[] extraArguments)
    {
        return false;
    }
}
