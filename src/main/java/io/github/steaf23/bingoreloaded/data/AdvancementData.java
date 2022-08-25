package io.github.steaf23.bingoreloaded.data;

import org.bukkit.ChatColor;
import org.bukkit.advancement.Advancement;

public class AdvancementData
{
    private static final YMLDataManager data = new YMLDataManager("advancements.yml");

    public static String getAdvancementTitle(Advancement adv)
    {
        if (data.getConfig().getConfigurationSection(adv.getKey().getKey()) == null)
        {
            return "" + ChatColor.BOLD + ChatColor.DARK_RED + adv.getKey().toString();
        }
        return data.getConfig().getConfigurationSection(adv.getKey().getKey()).getString("name");
    }

    public static String getAdvancementDesc(Advancement adv)
    {
        if (data.getConfig().getConfigurationSection(adv.getKey().getKey()) == null)
        {
            return getAdvancementTitle(adv);
        }
        return data.getConfig().getConfigurationSection(adv.getKey().getKey()).getString("desc");
    }
}
