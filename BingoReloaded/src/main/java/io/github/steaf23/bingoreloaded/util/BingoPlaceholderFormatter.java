package io.github.steaf23.bingoreloaded.util;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.helper.YmlDataManager;

import javax.xml.crypto.Data;

public class BingoPlaceholderFormatter
{
    private static final YmlDataManager data = BingoReloaded.createYmlDataManager("placeholders.yml");

    public String format(BingoReloadedPlaceholderExpansion.BingoReloadedPlaceholder placeholder) {
        String format = data.getConfig().getString("placeholders." + placeholder.getName() + ".format", "");
        return format;
    }
}
