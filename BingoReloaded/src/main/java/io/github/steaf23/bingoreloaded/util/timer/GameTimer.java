package io.github.steaf23.bingoreloaded.util.timer;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.util.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public abstract class GameTimer
{
    protected Consumer<Long> notifier;
    private long time;
    private BukkitTask task;

    public abstract Message getTimeDisplayMessage(boolean asSeconds);
    public abstract int getStartDelay();
    public abstract int getUpdateInterval();
    public abstract int getStep();

    public GameTimer()
    {
        this.time = 0;
    }

    public void start()
    {
        this.task = Bukkit.getScheduler().runTaskTimer(BingoReloaded.getPlugin(BingoReloaded.class), () -> {
            updateTime(time + getStep());
        }, getStartDelay(), getUpdateInterval());
    }

    public long pause()
    {
        //TODO: Add timer pauses
        return getTime();
    }

    public long stop()
    {
        try
        {
            if (task != null)
                task.cancel();
        }
        catch (IllegalStateException e)
        {
            Message.log(ChatColor.RED + "Timer couldn't be stopped since it never started!");
            return -1;
        }
        return getTime();
    }

    public long getTime()
    {
        return time;
    }

    public void updateTime(long newTime)
    {
        time = newTime;
        if (notifier != null)
            notifier.accept(newTime);
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

    public static String getSecondsString(long seconds)
    {
        return String.format("%d", seconds);
    }

    public void setNotifier(Consumer<Long> notifier)
    {
        this.notifier = notifier;
    }
}
