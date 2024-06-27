package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.helper.YmlDataManager;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.easymenulib.scoreboard.HUDRegistry;
import io.github.steaf23.easymenulib.scoreboard.SidebarHUD;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ScoreboardData
{
    public record SidebarTemplate(String title, Map<String, Component> arguments, String... lines) {}

    private final YmlDataManager data = BingoReloaded.createYmlDataManager("scoreboards.yml");

    public SidebarTemplate loadTemplate(String name, Map<String, Component> arguments) {
        String title = data.getConfig().getString(name + ".title", "");
        List<String> sidebar = data.getConfig().getStringList(name + ".sidebar");

        SidebarTemplate template = new SidebarTemplate(title, arguments, sidebar.toArray(new String[]{}));
        return template;
    }
}
