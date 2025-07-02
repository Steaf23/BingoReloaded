package io.github.steaf23.bingoreloaded.gui.inventory.item;

import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.bingoreloaded.lib.inventory.item.action.MenuAction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

public class TaskItemAction extends MenuAction
{
    private final GameTask task;

    public TaskItemAction(@NotNull GameTask task) {
        this.task = task;
    }

    @Override
    public void use(ActionArguments arguments) {
        arguments.player().sendMessage(Component.empty());
        arguments.player().sendMessage(task.data.getName().decorate(TextDecoration.BOLD));
        arguments.player().sendMessage(Component.text(" - ").append(task.data.getChatDescription()));
    }
}
