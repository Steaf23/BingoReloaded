package io.github.steaf23.bingoreloaded.gui.inventory.item;

import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.easymenulib.inventory.BasicMenu;
import io.github.steaf23.easymenulib.inventory.item.action.MenuAction;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

//TODO: test
public class TaskItemAction extends MenuAction
{
    private final BingoTask task;

    public TaskItemAction(@NotNull BingoTask task) {
        this.task = task;
    }

    @Override
    public void use(BasicMenu.ActionArguments arguments) {
        Component base = Component.text("\n");
        Component name = task.data.getName();
        name.decorate(TextDecoration.BOLD);

        base.append(name);
        base.append(Component.text("\n - "));
        base.append(task.data.getChatDescription());

        Message.sendDebugNoPrefix(base, (Player) arguments.player());
    }
}
