package io.github.steaf23.bingoreloaded.util.timer;

import io.github.steaf23.bingoreloaded.lib.api.ExtensionTask;
import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class GameTimer
{
    private final List<Consumer<Long>> notifiers;
    private long time;
    private ExtensionTask task;

    public abstract Component getTimeDisplayMessage(boolean asSeconds);
    public abstract int getStartDelay();
    public abstract int getUpdateInterval();
    public abstract int getStep();

    public GameTimer()
    {
        this.time = 0;
        this.task = null;
        this.notifiers = new ArrayList<>();
    }

    public void start()
    {
        stop();
        this.task = PlatformResolver.get().runTaskTimer(getUpdateInterval(), getStartDelay(), (task) -> {
            updateTime(time + getStep());
        });
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
            ConsoleMessenger.bug("A Timer couldn't be stopped since it never started!", this);
            return -1;
        }
        task = null;
        return getTime();
    }

    public long getTime()
    {
        return time;
    }

    protected void updateTime(long newTime)
    {
        time = newTime;
        for (var notifier : notifiers) {
            notifier.accept(newTime);
        }
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

    public static Component getTimeAsComponent(long seconds) {
        return Component.text(getTimeAsString(seconds));
    }

    public static String getSecondsString(long seconds)
    {
        return String.format("%d", seconds);
    }

    public void addNotifier(@NotNull Consumer<Long> notifier)
    {
        this.notifiers.add(notifier);
    }

    public boolean isRunning() {
        return task != null && !task.isCancelled();
    }
}
