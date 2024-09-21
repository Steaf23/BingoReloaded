package io.github.steaf23.bingoreloaded.settings;

import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.data.core.tag.TagDataType;
import io.github.steaf23.bingoreloaded.data.core.tag.DataStorageSerializer;
import io.github.steaf23.bingoreloaded.gui.inventory.EffectOptionFlags;

import java.util.EnumSet;

public record BingoSettings(String card,
                            BingoGamemode mode,
                            CardSize size,
                            int seed,
                            PlayerKit kit,
                            EnumSet<EffectOptionFlags> effects,
                            int maxTeamSize,
                            boolean enableCountdown,
                            int countdownDuration,
                            int hotswapGoal)
{
}
