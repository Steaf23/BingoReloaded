package io.github.steaf23.bingoreloaded.gui.inventory.item;

import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.easymenulib.inventory.BasicMenu;
import io.github.steaf23.easymenulib.inventory.item.action.MenuAction;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TaskItemAction extends MenuAction
{
    private final BingoTask task;

    public TaskItemAction(@NotNull BingoTask task) {
        this.task = task;
    }

    @Override
    public void use(BasicMenu.ActionArguments arguments) {
        BaseComponent base = new TextComponent("\n");
        BaseComponent name = task.data.getName();
        name.setBold(true);

        base.addExtra(name);
        base.addExtra("\n - ");
        base.addExtra(task.data.getChatDescription());

        Message.sendDebugNoPrefix(base, (Player) arguments.player());
    }
}
