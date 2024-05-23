package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.helper.YmlDataManager;
import io.github.steaf23.easymenulib.scoreboard.HUDRegistry;
import io.github.steaf23.easymenulib.scoreboard.SidebarHUD;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ScoreboardData
{
    public record SidebarTemplate(String title, String... lines) {}

    private final YmlDataManager data = BingoReloaded.createYmlDataManager("scoreboards.yml");

    public SidebarTemplate loadTemplate(HUDRegistry registry, String name) {
        String title = data.getConfig().getString(name + ".title", "");
        List<String> sidebar = data.getConfig().getStringList(name + ".sidebar");

        List<String> finalSidebar = new ArrayList<>();
        Set<String> sectionKeys = data.getConfig().getConfigurationSection(name + ".sections").getKeys(false);
        for (String line : sidebar) {
            String key = line.substring(1, line.length() -2);
            if (sectionKeys.contains(key)) {
                List<String> sectionText = data.getConfig().getStringList(name + ".sections." + key);
                finalSidebar.addAll(sectionText);
            }
            else {
                finalSidebar.add(line);
            }
        }
        SidebarTemplate template = new SidebarTemplate(title, finalSidebar.toArray(new String[]{}));
        return template;
    }
}
