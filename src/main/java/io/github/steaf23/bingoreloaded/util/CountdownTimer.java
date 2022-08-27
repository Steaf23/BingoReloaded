package io.github.steaf23.bingoreloaded.util;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CountdownTimer
{
    private int startTime = 0;
    private int time = 0;
    private BukkitRunnable timer;
    private int medThreshold;
    private int lowThreshold;

    public CountdownTimer(int seconds)
    {
        this(seconds, 0, 0);
    }

    public CountdownTimer(int seconds, int medThreshold, int lowThreshold)
    {
        this.medThreshold = medThreshold;
        this.lowThreshold = lowThreshold;
        start(seconds);
    }

    public void start(int seconds)
    {
        this.startTime = seconds;
        this.time = seconds;
        timer = new BukkitRunnable() {

            @Override
            public void run()
            {
                time--;
                if (time <= 0)
                {
                    stop();
                }
            }
        };
        timer.runTaskTimer(BingoReloaded.getPlugin(BingoReloaded.class), 0, 20);
    }

    public int getStartTime()
    {
        return startTime;
    }

    public int getTimeLeft()
    {
        return time;
    }

    public int stop()
    {
        timer.cancel();
        return time;
    }

    public void showTime(Player player)
    {
        ChatColor color = ChatColor.GREEN;
        if (time <= lowThreshold)
            color = ChatColor.RED;
        else if (time <= medThreshold)
            color = ChatColor.GOLD;
        player.sendTitle(color + "" + time, "", -1, -1, -1);
    }
}
