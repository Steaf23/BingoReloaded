package io.github.steaf23.bingoreloaded.placeholder;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.helper.YmlDataManager;

public class BingoPlaceholderFormatter
{
    private static final YmlDataManager data = BingoReloaded.createYmlDataManager("placeholders.yml");

    public String format(BingoReloadedPlaceholder placeholder) {
        return data.getConfig().getString("placeholders." + placeholder.getName() + ".format", "");
    }

    /**
     * Overloaded function to get the full team format when placeholder api is not used.
     * @return format for bingoreloaded_team_full placeholder
     */
    public String getTeamFullFormat() {
        return data.getConfig().getString("placeholders.team_full.format", "");
    }
}
