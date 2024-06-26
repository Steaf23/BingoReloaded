package io.github.steaf23.bingoreloaded.gui.hud;

import io.github.steaf23.bingoreloaded.data.ScoreboardData;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.easymenulib.scoreboard.PlayerHUD;
import io.github.steaf23.easymenulib.scoreboard.SidebarHUD;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TemplatedPlayerHUD extends PlayerHUD
{
    private final ScoreboardData.SidebarTemplate template;

    public TemplatedPlayerHUD(Player player, String initialTitle, ScoreboardData.SidebarTemplate template) {
        super(player.getUniqueId(), new SidebarHUD(initialTitle));

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
        boolean full = false;
        int templateIndex = 0;
        for (String line : template.lines()) {
            // convert colors, placeholders, etc..
            for (String arg : template.arguments().keySet()) {
                line = line.replace("{" + arg + "}", template.arguments().get(arg));
            }
            for (String linePart : line.split("\\n")) {
                // for each part we need to check if we have enough space left after adding every extra line, expanding downwards
                int spaceForLine = 15 - lineNumber - (template.lines().length - templateIndex);
                if (spaceForLine < 0) {
                    break;
                }
                linePart = new Message(linePart).toLegacyString(player);
                sidebar.setText(lineNumber, linePart);
                lineNumber++;
                if (lineNumber == 15) {
                    full = true;
                    break;
                }
            }
            if (full) {
                break;
            }
            templateIndex++;
        }

        super.update();
    }
}
