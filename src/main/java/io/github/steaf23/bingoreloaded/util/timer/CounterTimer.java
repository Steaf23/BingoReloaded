package io.github.steaf23.bingoreloaded.util.timer;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.core.BingoGame;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class CounterTimer extends GameTimer
{
    private BukkitTask task;

    public CounterTimer(BingoGame game)
    {
        super(game);
    }

    @Override
    public Message getTimeDisplayMessage()
    {
        return new TranslatedMessage("game.timer.duration")
                .color(ChatColor.AQUA).bold()
                .arg(GameTimer.getTimeAsString(getTime())).color(ChatColor.WHITE);
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
        return 1;
    }
}
