package io.github.steaf23.bingoreloaded.util;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.event.CountdownTimerFinishedEvent;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class CountdownTimer extends GameTimer
{
    private int startTime = 0;
    private static BukkitRunnable runnable;
    private int medThreshold;
    private int lowThreshold;

    public CountdownTimer(int seconds, String worldName)
    {
        this(seconds, 0, 0, worldName);
    }

    public CountdownTimer(int seconds, int medThreshold, int lowThreshold, String worldName)
    {
        super(worldName);
        this.medThreshold = medThreshold;
        this.lowThreshold = lowThreshold;
        this.startTime = seconds;
    }

    public int getStartTime()
    {
        return startTime;
    }

    @Override
    public void start()
    {
        updateTime(startTime);
        runnable = new BukkitRunnable() {

            @Override
            public void run()
            {
                updateTime(getTime() - 1);
                if (getTime() <= 0)
                {
                    CountdownTimerFinishedEvent event = new CountdownTimerFinishedEvent(worldName);
                    Bukkit.getPluginManager().callEvent(event);
                    stop();
                }
            }
        };
        runnable.runTaskTimer(BingoReloaded.getPlugin(BingoReloaded.class), 0, 20);
    }

    @Override
    public long pause()
    {
        return getTime();
    }

    @Override
    public long stop()
    {
        try
        {
            if (runnable != null)
                runnable.cancel();
        }
        catch (IllegalStateException e)
        {
            Message.log(ChatColor.RED + "Timer couldn't be stopped since it never started!");
            return -1;
        }
        return getTime();
    }

    @Override
    public Message getTimeDisplayMessage()
    {
        ChatColor color = ChatColor.WHITE;
        if (getTime() <= lowThreshold)
            color = ChatColor.RED;
        else if (getTime() <= medThreshold)
            color = ChatColor.GOLD;
        return new Message("game.timer.time_left")
                .color(ChatColor.LIGHT_PURPLE).bold()
                .arg(GameTimer.getTimeAsString(getTime())).color(color);
    }
}
