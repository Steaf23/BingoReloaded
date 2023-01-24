package io.github.steaf23.bingoreloaded.util;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.Message;
import io.github.steaf23.bingoreloaded.data.TranslationData;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class CounterTimer extends GameTimer
{
    private BukkitRunnable runnable;

    public CounterTimer(String worldName)
    {
        super(worldName);
    }

    @Override
    public void start()
    {
        updateTime(0);
        runnable = new BukkitRunnable() {

            @Override
            public void run()
            {
                updateTime(getTime() + 1);
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
        }
        return getTime();
    }

    @Override
    public Message getTimeDisplayMessage()
    {
        return new Message("game.timer.duration")
                .color(ChatColor.AQUA).bold()
                .arg(GameTimer.getTimeAsString(getTime())).color(ChatColor.WHITE);
    }
}
