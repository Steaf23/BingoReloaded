package io.github.steaf23.bingoreloaded.util.timer;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class CounterTimer extends GameTimer
{

    public CounterTimer(WorldHandle world) {
        super(world);
    }

    @Override
    public Component getTimeDisplayMessage(boolean asSeconds)
    {
        String timeString = asSeconds ? GameTimer.getSecondsString(getTime()) : GameTimer.getTimeAsString(getTime());
        return BingoMessage.DURATION.asPhrase(Component.text(timeString).color(NamedTextColor.WHITE))
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
