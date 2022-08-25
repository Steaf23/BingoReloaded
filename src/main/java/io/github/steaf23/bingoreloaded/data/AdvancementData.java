package io.github.steaf23.bingoreloaded.data;

import org.bukkit.ChatColor;
import org.bukkit.advancement.Advancement;

public class AdvancementData
{
    private static final YMLDataManager data = new YMLDataManager("advancements.yml");

    public static String getAdvancementTitle(Advancement adv)
    {
        if (data.getConfig().getString(adv.getKey().getKey()) == null)
        {
            return "" + ChatColor.BOLD + ChatColor.DARK_RED + adv.getKey().toString();
        }
        return data.getConfig().getString(adv.getKey().getKey());
    }

    public static String getAdvancementDesc(Advancement adv)
    {
        return data.getConfig().getString(adv.getKey().getKey());
    }
}
