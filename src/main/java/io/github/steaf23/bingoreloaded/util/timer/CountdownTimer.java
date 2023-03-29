package io.github.steaf23.bingoreloaded.util.timer;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.core.BingoSession;
import io.github.steaf23.bingoreloaded.core.event.CountdownTimerFinishedEvent;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

public class CountdownTimer extends GameTimer
{
    private int startTime = 0;
    private int medThreshold;
    private int lowThreshold;
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
    protected void updateTime(long newTime)
    {
        super.updateTime(newTime);
        if (getTime() <= 0)
        {
            CountdownTimerFinishedEvent event = new CountdownTimerFinishedEvent(session);
            Bukkit.getPluginManager().callEvent(event);
            stop();
        }
    }

    @Override
    public Message getTimeDisplayMessage()
    {
        ChatColor color = ChatColor.WHITE;
        if (getTime() <= lowThreshold)
            color = ChatColor.RED;
        else if (getTime() <= medThreshold)
            color = ChatColor.GOLD;
        return new TranslatedMessage("game.timer.time_left")
                .color(ChatColor.LIGHT_PURPLE).bold()
                .arg(GameTimer.getTimeAsString(getTime())).color(color);
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
