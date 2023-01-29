package io.github.steaf23.bingoreloaded.util;

import io.github.steaf23.bingoreloaded.Message;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class GameTimer
{
    protected TimeNotifier notifier;
    private long time;
    protected String worldName;

    public abstract void start();
    public abstract long pause();
    public abstract long stop();
    public abstract Message getTimeDisplayMessage();

    public GameTimer(String worldName)
    {
        this.worldName = worldName;
    }
    public long getTime()
    {
        return time;
    }

    protected void updateTime(long newTime)
    {
        time = newTime;
        notifier.timeUpdated(newTime);
    }

    public static String getTimeAsString(long seconds)
    {
        seconds = Math.max(seconds, 0);
        if (seconds >= 60)
        {
            long minutes = (seconds / 60) % 60;
            if (seconds >= 3600)
            {
                long hours = seconds / 3600;
                return String.format("%02d:%02d:%02d", hours, minutes, seconds % 60);
            }
            return String.format("%02d:%02d", minutes, seconds % 60);
        }
        return String.format("00:%02d", seconds % 60);
    }

    public void setNotifier(TimeNotifier notifier)
    {
        this.notifier = notifier;
    }
}
