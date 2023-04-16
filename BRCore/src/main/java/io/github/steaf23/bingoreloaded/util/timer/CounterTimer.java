package io.github.steaf23.bingoreloaded.util.timer;

import io.github.steaf23.bingoreloaded.BingoReloadedCore;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.scheduler.BukkitTask;

public class CounterTimer extends GameTimer
{
    private BukkitTask task;

    @Override
    public Message getTimeDisplayMessage()
    {
        return new TranslatedMessage(BingoTranslation.DURATION)
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
        return BingoReloadedCore.ONE_SECOND;
    }

    @Override
    public int getStep()
    {
        return 1;
    }
}
