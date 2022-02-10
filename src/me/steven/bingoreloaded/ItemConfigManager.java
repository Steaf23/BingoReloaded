package me.steven.bingoreloaded;

import me.steven.bingoreloaded.GUIInventories.cards.BingoCard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemConfigManager
{
    public static Map<BingoCard.CardDifficulty, List<Material>> getBingoItems()
    {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(BingoReloaded.NAME);
        if (plugin == null) return null;
        File f = new File(plugin.getDataFolder(), "items.yml");
        YamlConfiguration dataConfig = YamlConfiguration.loadConfiguration(f);


        InputStream defaultStream = plugin.getResource("items.yml");
        if (defaultStream != null)
        {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            dataConfig.setDefaults(defaultConfig);
        }
        BingoReloaded.broadcast(defaultStream.toString());
        BingoReloaded.broadcast(dataConfig.toString());

        List<String> names = (List<String>)dataConfig.getList("easy");
        BingoReloaded.broadcast(names.toString());


        Map<BingoCard.CardDifficulty, List<Material>> items = new HashMap<>();
        List<Material> easyItems = new ArrayList<>();
        for (String name : names)
        {
            Material mat = Material.getMaterial(name);
            if (mat != null)
                easyItems.add(Material.getMaterial(name));
            else BingoReloaded.print(ChatColor.RED + "'" + name + "' is not a valid Material, it will be skipped! (check if you didn't make any typos)");
        }
        items.put(BingoCard.CardDifficulty.EASY, easyItems);
        return items;
    }
}
