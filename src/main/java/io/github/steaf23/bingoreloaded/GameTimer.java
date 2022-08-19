package io.github.steaf23.bingoreloaded;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class GameTimer
{
    private int time;

    private BukkitRunnable runnable;
    private final BingoScoreboard scoreboard;

    public GameTimer(BingoScoreboard scoreboard)
    {
        this.time = 0;
        this.scoreboard = scoreboard;
    }

    public void start()
    {
        time = 0;
        runnable = new BukkitRunnable() {

            @Override
            public void run()
            {
                setTime(getTime() + 1);
            }
        };
        runnable.runTaskTimer(BingoReloaded.getPlugin(BingoReloaded.class), 0, 20);
    }

    public void stop()
    {
        try
        {
            if (runnable != null)
                runnable.cancel();
        }
        catch (IllegalStateException e)
        {
            BingoReloaded.print(ChatColor.RED + "Timer couldn't be stopped since it never started!");
        }
    }

    public static String getTimeAsString(int seconds)
    {
        if (seconds > 60)
        {
            int minutes = (seconds / 60) % 60;
            if (seconds > 3600)
            {
                int hours = seconds / 3600;
                return String.format("%02d:%02d:%02d", hours, minutes, seconds % 60);
            }
            return String.format("%02d:%02d", minutes, seconds % 60);
        }
        return String.format("00:%02d", seconds % 60);
    }

    public void setTime(int seconds)
    {
        time = seconds;
        scoreboard.updateGameTime(this);
    }

    public int getTime()
    {
        return time;
    }
}
