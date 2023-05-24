package io.github.steaf23.bingoreloaded.command;

import io.github.steaf23.bingoreloaded.BingoSettingsBuilder;
import io.github.steaf23.bingoreloaded.game.BingoSession;
import org.bukkit.command.CommandExecutor;

public interface AutoBingoCommand extends CommandExecutor
{
    boolean start(String worldName);
    boolean setKit(BingoSession session, BingoSettingsBuilder settings, String worldName, String[] extraArguments);
    boolean setEffect(BingoSession session, BingoSettingsBuilder settings, String worldName, String[] extraArguments);
    boolean setCard(BingoSettingsBuilder settings, String worldName, String[] extraArguments);
    boolean setCountdown(BingoSettingsBuilder settings, String worldName, String[] extraArguments);
    boolean setDuration(BingoSettingsBuilder settings, String worldName, String[] extraArguments);
    boolean setPlayerTeam(String worldName, String[] extraArguments);
    boolean setTeamSize(BingoSettingsBuilder settings, String worldName, String[] extraArguments);
    boolean setGamemode(BingoSettingsBuilder settings, String worldName, String[] extraArguments);
    boolean end(String worldName);
    boolean preset(BingoSettingsBuilder settingsBuilder, String worldName, String[] extraArguments);

    /**
     * @param in
     * @param defaultValue
     * @return Integer the string represents or defaultValue if a conversion failed.
     */
    static int toInt(String in, int defaultValue)
    {
        try
        {
            return Integer.parseInt(in);
        }
        catch (NumberFormatException e)
        {
            return defaultValue;
        }
    }

}
