package io.github.steaf23.bingoreloaded.util.timer;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.scheduler.BukkitTask;

public class CounterTimer extends GameTimer
{
    private BukkitTask task;

    @Override
    public Component getTimeDisplayMessage(boolean asSeconds)
    {
        String timeString = asSeconds ? GameTimer.getSecondsString(getTime()) : GameTimer.getTimeAsString(getTime());
        return BingoTranslation.DURATION.asSingleComponent(Component.text(timeString).color(NamedTextColor.WHITE))
                .color(NamedTextColor.AQUA)
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
        return 1;
    }
}
