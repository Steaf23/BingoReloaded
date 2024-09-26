package io.github.steaf23.bingoreloaded.placeholder;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.core.DataAccessor;

public class BingoPlaceholderFormatter
{
    private static final DataAccessor DATA = BingoReloaded.getDataAccessor("placeholders");

    public String format(BingoReloadedPlaceholder placeholder) {
        return DATA.getString("placeholders." + placeholder.getName() + ".format", "");
    }

    /**
     * Overloaded function to get the full team format when placeholder api is not used.
     * @return format for bingoreloaded_team_full placeholder
     */
    public String getTeamFullFormat() {
        return DATA.getString("placeholders.team_full.format", "");
    }
}
