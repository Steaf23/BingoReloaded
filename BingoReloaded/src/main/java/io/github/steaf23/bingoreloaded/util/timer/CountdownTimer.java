package io.github.steaf23.bingoreloaded.util.timer;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.event.CountdownTimerFinishedEvent;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

public class CountdownTimer extends GameTimer
{
    private int startTime = 0;
    public final int medThreshold;
    public final int lowThreshold;
    private final BingoSession session;

    public CountdownTimer(int seconds, BingoSession session)
    {
        this(seconds, 0, 0, session);
    }

    public CountdownTimer(int seconds, int medThreshold, int lowThreshold, BingoSession session)
    {
        this.medThreshold = medThreshold;
        this.lowThreshold = lowThreshold;
        this.startTime = seconds;
        this.session = session;
    }

    public int getStartTime()
    {
        return startTime;
    }

    @Override
    public void start()
    {
        updateTime(startTime);
        super.start();
    }

    @Override
    public void updateTime(long newTime)
    {
        super.updateTime(newTime);
        if (getTime() <= 0)
        {
            CountdownTimerFinishedEvent event = new CountdownTimerFinishedEvent(session, this);
            Bukkit.getPluginManager().callEvent(event);
            stop();
        }
    }

    @Override
    public Message getTimeDisplayMessage(boolean asSeconds)
    {
        String timeString = asSeconds ? GameTimer.getSecondsString(getTime()) : GameTimer.getTimeAsString(getTime());
        ChatColor color = ChatColor.WHITE;
        if (getTime() <= lowThreshold)
            color = ChatColor.RED;
        else if (getTime() <= medThreshold)
            color = ChatColor.GOLD;
        return new TranslatedMessage(BingoTranslation.TIME_LEFT)
                .color(ChatColor.LIGHT_PURPLE).bold()
                .arg(timeString).color(color);
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
