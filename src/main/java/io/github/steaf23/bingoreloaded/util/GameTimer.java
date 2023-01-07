package io.github.steaf23.bingoreloaded.util;

import io.github.steaf23.bingoreloaded.Message;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class GameTimer
{
    protected TimeNotifier notifier;
    private int time;
    protected String worldName;

    public abstract void start();
    public abstract int pause();
    public abstract int stop();
    public abstract Message getTimeDisplayMessage();

    public GameTimer(String worldName)
    {
        this.worldName = worldName;
    }
    public int getTime()
    {
        return time;
    }

    protected void updateTime(int newTime)
    {
        time = newTime;
        notifier.timeUpdated(newTime);
    }

    public static String getTimeAsString(int seconds)
    {
        if (seconds >= 60)
        {
            int minutes = (seconds / 60) % 60;
            if (seconds >= 3600)
            {
                int hours = seconds / 3600;
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
