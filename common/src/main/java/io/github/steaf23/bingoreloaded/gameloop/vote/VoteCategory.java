package io.github.steaf23.bingoreloaded.gameloop.vote;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A vote category is used to contain information about what can be voted on
 */
public abstract class VoteCategory<Result> implements ComponentLike
{
    private final String configName;
    private final Component displayName;

    public VoteCategory(String configName, Component displayName) {
        this.configName = configName;
        this.displayName = displayName;
    }

    abstract @NotNull List<String> getValidValues();

    abstract @Nullable Result createResultForValue(String value);

    public abstract Component getValueComponent(String value);

    String getConfigName() {
        return configName;
    }

    @Override
    public @NotNull Component asComponent() {
        return displayName;
    }

    public @Nullable Result getValidResultOrNull(VoteTicket ticket) {
        String value = ticket.getVote(this);
        if (value == null) {
            return null;
        }

        if (getValidValues().contains(value)) { // Validate if given value should be valid
            Result result = createResultForValue(value); // Parse the value into the voted result
            if (result != null) {
                return result;
            }
        }
        ConsoleMessenger.error("Invalid " + configName + " " + value + " found while collecting votes");
        return null;
    }

    @Override
    public String toString() {
        return configName;
    }
}
