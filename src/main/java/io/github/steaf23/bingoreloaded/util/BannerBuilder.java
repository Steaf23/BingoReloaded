package io.github.steaf23.bingoreloaded.util;


import io.github.steaf23.bingoreloaded.BingoReloaded;
import org.bukkit.Material;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BannerBuilder
{
    public static Pattern baseBanner = Pattern.compile("(?<=minecraft:)[\\w]+");
    public static Pattern pattern = Pattern.compile("(?<=Pattern:)[\\w]+");
    public static Pattern color = Pattern.compile("((?<=Color:)[\\w]+)");

    public static ItemStack fromCommand(String command)
    {
        ItemStack banner = new ItemStack(Material.WHITE_BANNER);
        List<org.bukkit.block.banner.Pattern> patterns = new ArrayList<>();

        Matcher bannerMatcher = baseBanner.matcher(command);
        while (bannerMatcher.find())
        {
            banner = new ItemStack(Material.valueOf(bannerMatcher.group().toUpperCase()));
        }
        if (banner == null)
        {
            BingoReloaded.print("Malformed banner command! (Cannot find banner base!)");
        }

        Matcher patMatcher = pattern.matcher(command);
        List<PatternType> types = new ArrayList<>();
        while (patMatcher.find())
        {
            types.add(PatternType.getByIdentifier(patMatcher.group()));
        }

        Matcher colMatcher = color.matcher(command);
        List<FlexibleColor> colors = new ArrayList<>();
        while (colMatcher.find())
        {
            int i = Integer.parseInt(colMatcher.group());
            colors.add(FlexibleColor.fromNbt(i));
        }

        if (types.size() == colors.size())
        {
            for (int i = 0; i < types.size(); i++)
            {
                patterns.add(new org.bukkit.block.banner.Pattern(colors.get(i).dyeColor, types.get(i)));
            }
            BannerMeta meta = (BannerMeta) banner.getItemMeta();
            meta.setPatterns(patterns);
            banner.setItemMeta(meta);
        }
        else
        {
            BingoReloaded.print("Malformed banner command! (Mismatch count of pattern types and colors)");
        }
        return banner;
    }
}
