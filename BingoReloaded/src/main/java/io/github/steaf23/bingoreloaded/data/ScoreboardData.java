package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.helper.YmlDataManager;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;

public class ScoreboardData
{
    // Arguments can be multiline, which is why every argument needs to be saved as a Component array
    public record SidebarTemplate(String title, Map<String, Component[]> arguments, String... lines) {}

    private final YmlDataManager data = BingoReloaded.createYmlDataManager("scoreboards.yml");

    public SidebarTemplate loadTemplate(String name, Map<String, Component[]> arguments) {
        String title = data.getConfig().getString(name + ".title", "");
        List<String> sidebar = data.getConfig().getStringList(name + ".sidebar");

        return new SidebarTemplate(title, arguments, sidebar.toArray(new String[]{}));
    }
}
