package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class InfoScoreboard
{
    private final Scoreboard board;
    private final Objective sidebar;

    public InfoScoreboard(String title, Scoreboard board)
    {
        this.board = board;
        this.sidebar = board.registerNewObjective("info", "dummy", title);
        sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (int i = 0; i < 15; i++)
        {
            Team team = board.registerNewTeam("LINE_" + i);
            team.addEntry(lineEntry(i));
        }
    }

    public void clearDisplay()
    {
        for (int i = 0; i < 15; i++)
        {
            setLineText(i, "");
        }
    }

    public void updatePlayerBoard(Player player)
    {
        player.setScoreboard(board);
    }

    public void clearPlayerBoard(Player player)
    {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    /**
     * Sets a line of text on the scoreboard, to update this for visually for the player,
     * updatePlayerBoard should be called
     * @param lineIndex
     * @param text
     */
    public void setLineText(int lineIndex, String text)
    {
        if (lineIndex < 0 || lineIndex > 14)
        {
            Message.log("Line index " + lineIndex + " out of range for scoreboard (use 0-14)");
            return;
        }

        Team team = board.getTeam("LINE_" + lineIndex);
        team.setPrefix(text);
        if (text.isEmpty())
            board.resetScores(lineEntry(lineIndex));
        else
            sidebar.getScore(lineEntry(lineIndex)).setScore(0);
    }

    private static String lineEntry(int index)
    {
        if (index < 0 || index > 14)
        {
            Message.log("Line index " + index + " out of range for scoreboard (use 0-14)");
            return "";
        }

        return ChatColor.values()[index].toString();
    }
}
