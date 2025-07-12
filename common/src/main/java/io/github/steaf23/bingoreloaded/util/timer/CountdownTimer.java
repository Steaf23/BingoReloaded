package io.github.steaf23.bingoreloaded.util.timer;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class CountdownTimer extends GameTimer
{
    private final int startTime;
    public final int medThreshold;
    public final int lowThreshold;
    private final Runnable onTimeoutCallback;

    public CountdownTimer(int seconds, @Nullable Runnable onTimeoutCallback)
    {
        this(seconds, 0, 0, onTimeoutCallback);
    }

    public CountdownTimer(int seconds, int medThreshold, int lowThreshold, @Nullable Runnable onTimeoutCallback)
    {
        this.medThreshold = medThreshold;
        this.lowThreshold = lowThreshold;
        this.startTime = seconds;
		this.onTimeoutCallback = onTimeoutCallback;
	}

    public int getStartTime()
    {
        return startTime;
    }

    /**
     * Attempts to start the timer, if this timer is already running it will be restarted.
     */
    @Override
    public void start()
    {
        updateTime(startTime);

        if (startTime == 0) {
            stop();
            return;
        }
        super.start();
    }

    @Override
    protected void updateTime(long newTime)
    {
        super.updateTime(newTime);
        if (getTime() <= 0)
        {
            if (onTimeoutCallback != null) {
                onTimeoutCallback.run();
            }
            stop();
        }
    }

    @Override
    public Component getTimeDisplayMessage(boolean asSeconds)
    {
        String timeString = asSeconds ? GameTimer.getSecondsString(getTime()) : GameTimer.getTimeAsString(getTime());
        NamedTextColor color = NamedTextColor.WHITE;
        if (getTime() <= lowThreshold)
            color = NamedTextColor.RED;
        else if (getTime() <= medThreshold)
            color = NamedTextColor.GOLD;
        return BingoMessage.TIME_LEFT.asPhrase(Component.text(timeString).color(color))
                .color(NamedTextColor.LIGHT_PURPLE)
                .decorate(TextDecoration.BOLD);
    }

    @Override
    public int getStartDelay()
    {
        return 0;
    }

    @Override
    public int getUpdateInterval()
    {
        return BingoReloaded.ONE_SECOND;
    }

    @Override
    public int getStep()
    {
        return -1;
    }
}
