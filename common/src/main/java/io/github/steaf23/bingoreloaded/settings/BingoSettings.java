package io.github.steaf23.bingoreloaded.settings;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.gui.inventory.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public record BingoSettings(String card,
                            BingoGamemode mode,
                            CardSize size,
                            int seed,
                            PlayerKit kit,
                            EnumSet<EffectOptionFlags> effects,
                            int maxTeamSize,
                            CountdownType countdownType,
                            int countdownDuration,
                            int hotswapGoal,
                            boolean expireHotswapTasks,
                            int completeGoal,
                            boolean differentCardPerTeam)
{
    public enum CountdownType implements Keyed
    {
        DISABLED(Key.key(BingoReloaded.getInstance(), "countdown_type.disabled")), // No countdown timer is used at all, only the normal game timer is used for events.
        DURATION(Key.key(BingoReloaded.getInstance(), "countdown_type.duration")), // Use the countdown timer and ignore score based goals.
        TIME_LIMIT(Key.key(BingoReloaded.getInstance(), "countdown_type.time_limit")),
        ; // Use the countdown timer, but if a score based goal got reached, end the game.

        private final Key key;

        CountdownType(Key key) {
            this.key = key;
        }

        @Override
        public @NotNull Key key() {
            return key;
        }

        public static CountdownType fromNamespace(Key key) {
            if (key.equals(CountdownType.DISABLED.key())) {
                return CountdownType.DISABLED;
            } else if (key.equals(CountdownType.DURATION.key())) {
                return CountdownType.DURATION;
            } else if (key.equals(CountdownType.TIME_LIMIT.key())) {
                return CountdownType.TIME_LIMIT;
            } else {
                ConsoleMessenger.bug("Unimplemented Countdown type when converting namespace", CountdownType.class);
                return CountdownType.DISABLED;
            }
        }
    }

    public boolean useCountdown() {
        return countdownType == CountdownType.TIME_LIMIT || countdownType == CountdownType.DURATION;
    }

    public boolean useScoreAsWinCondition() {
        return countdownType == CountdownType.DISABLED || countdownType == CountdownType.TIME_LIMIT;
    }
}
