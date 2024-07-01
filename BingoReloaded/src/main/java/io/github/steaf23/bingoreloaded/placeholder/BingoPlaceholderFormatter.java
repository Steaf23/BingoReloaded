package io.github.steaf23.bingoreloaded.placeholder;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
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

    //FIXME: use components for this...
    public static String createLegacyTextFromMessage(String message, String... args) {
        //for any given message like "{#00bb33}Completed {0} by team {1}! At {2}" split the arguments from the message.
        String[] rawSplit = message.split("\\{[^{}#@]*}"); //[{#00bb33}Completed, by team, ! At]

        // convert custom hex colors to legacyText: {#00bb33} -> ChatColor.of("#00bb33")
        // convert "&" to "§" and "&&" to "&"
        for (int i = 0; i < rawSplit.length; i++) {
            String part = BingoMessage.convertConfigString(rawSplit[i]);
            rawSplit[i] = part;
        }

        String finalMessage = "";
        // keep the previous message part for format retention
        // for each translated part of the message
        int i = 0;
        while (i < rawSplit.length) {
            finalMessage += rawSplit[i];
            if (args.length > i) {
                finalMessage += args[i];
            }
            i++;
        }

        if (i == 0 && args.length > 0) {
            for (String arg : args)
            {
                finalMessage += arg;
            }
        }
        return finalMessage;
    }
}
