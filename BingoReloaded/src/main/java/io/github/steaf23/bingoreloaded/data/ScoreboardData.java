package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.data.core.tag.TagDataType;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;

public class ScoreboardData
{
    // Arguments can be multiline, which is why every argument needs to be saved as a Component array
    public record SidebarTemplate(String title, Map<String, Component[]> arguments, String... lines) {}

    private final DataAccessor newData = BingoReloaded.getDataAccessor("scoreboards");

    public SidebarTemplate loadTemplate(String name, Map<String, Component[]> arguments) {
        String title = newData.getString(name + ".title", "");
        List<String> sidebar = newData.getList(name + ".sidebar", TagDataType.STRING);

        return new SidebarTemplate(title, arguments, sidebar.toArray(new String[]{}));
    }
}
