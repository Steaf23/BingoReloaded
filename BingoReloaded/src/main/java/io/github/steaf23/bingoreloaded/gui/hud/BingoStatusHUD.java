package io.github.steaf23.bingoreloaded.gui.hud;

import io.github.steaf23.bingoreloaded.data.ScoreboardData;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.easymenulib.scoreboard.PlayerHUD;
import io.github.steaf23.easymenulib.scoreboard.SidebarHUD;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class BingoStatusHUD extends PlayerHUD
{
    private final ScoreboardData.SidebarTemplate template;

    public BingoStatusHUD(Player player, boolean enableSidebar, String initialTitle, ScoreboardData.SidebarTemplate template) {
        super(player.getUniqueId(), enableSidebar, new SidebarHUD(initialTitle));

        this.template = template;
        update();
    }

    @Override
    public void update() {
        Player player = Bukkit.getPlayer(getPlayerId());
        if (player == null) {
            return;
        }

        sidebar.setTitle(new Message(template.title()).toLegacyString(player));

        sidebar.clear();
        int lineNumber = 0;
        for (String line : template.lines()) {
            // convert colors, placeholders, etc..
            for (String arg : template.arguments().keySet()) {
                line = line.replace("{" + arg + "}", template.arguments().get(arg));
            }
            for (String linePart : line.split("\\n")) {
                linePart = new Message(linePart).toLegacyString(player);
                sidebar.setText(lineNumber, linePart);
                lineNumber++;
            }
        }

        super.update();
    }
}
