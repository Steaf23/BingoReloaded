package io.github.steaf23.bingoreloaded.gui.item;

import io.github.steaf23.bingoreloaded.gui.base.BasicMenu;
import io.github.steaf23.bingoreloaded.gui.base.item.MenuAction;
import io.github.steaf23.bingoreloaded.gui.base.item.MenuItem;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.util.Message;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TaskItemAction implements MenuAction
{
    private final BingoTask task;

    public TaskItemAction(@NotNull BingoTask task) {
        this.task = task;
    }

    @Override
    public void use(MenuItem item, BasicMenu.ActionArguments arguments) {
        BaseComponent base = new TextComponent("\n");
        BaseComponent name = task.data.getItemDisplayName().asComponent();
        name.setBold(true);
        name.setColor(task.nameColor);

        base.addExtra(name);
        base.addExtra("\n - ");
        base.addExtra(task.data.getDescription());

        Message.sendDebugNoPrefix(base, (Player) arguments.player());
    }
}
