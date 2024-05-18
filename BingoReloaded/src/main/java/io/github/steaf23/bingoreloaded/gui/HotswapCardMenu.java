package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;
import io.github.steaf23.easymenulib.menu.MenuBoard;
import io.github.steaf23.easymenulib.util.ChatColorGradient;
import io.github.steaf23.easymenulib.util.ExtraMath;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.HumanEntity;

import java.awt.*;
import java.util.Map;

public class HotswapCardMenu extends CardMenu
{
    private Map<Integer, Integer> taskExpiration;
    private Map<Integer, Integer> taskExpirationStart;
    private final ChatColorGradient taskTimeGradient;

    public HotswapCardMenu(MenuBoard menuBoard, CardSize cardSize, String title) {
        super(menuBoard, cardSize, title);
        this.taskTimeGradient = new ChatColorGradient();
        taskTimeGradient.addColor(ChatColor.of("#ffd200"), 0.0f);
        taskTimeGradient.addColor(ChatColor.of("#e85e21"), 0.5f);
        taskTimeGradient.addColor(ChatColor.of("#750e0e"), 0.8f);
        taskTimeGradient.addColor(ChatColor.of("#221719"), 1.0f);
    }

    @Override
    public void beforeOpening(HumanEntity player) {
        updateContents();
    }

    public void updateTaskExpiration(Map<Integer, Integer> newExpiration) {
        this.taskExpiration = newExpiration;
        taskExpirationStart = taskExpirationStart;
        updateContents();
    }

    private void updateContents() {
        for (int i = 0; i < getTasks().size(); i++)
        {
            BingoTask task = getTasks().get(i);
            addItem(task.toItem()
                    .setSlot(getSize().getCardInventorySlot(i))
                    .addDescription("time", -1, getColorForTaskTime(i) + GameTimer.getTimeAsString(taskExpiration.get(i))));
        }
    }

    private ChatColor getColorForTaskTime(int taskIndex) {
        return taskTimeGradient.sample(ExtraMath.map(taskExpiration.get(taskIndex), 0.0f, 120.0f, 1.0f, 0.0f));
    }
}
