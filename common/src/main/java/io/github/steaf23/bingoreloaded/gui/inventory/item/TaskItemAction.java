package io.github.steaf23.bingoreloaded.gui.inventory.item;

import io.github.steaf23.bingoreloaded.data.helper.TaskFormatting;
import io.github.steaf23.bingoreloaded.lib.inventory.action.MenuAction;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

public class TaskItemAction extends MenuAction
{
    private final GameTask task;
    private final TaskFormatting formatting;

    public TaskItemAction(TaskFormatting formatting, @NotNull GameTask task) {
        this.task = task;
        this.formatting = formatting;
    }

    @Override
    public void use(ActionArguments arguments) {
        arguments.player().sendMessage(Component.empty());
        arguments.player().sendMessage(task.data().getName(formatting).decorate(TextDecoration.BOLD));
        arguments.player().sendMessage(Component.text(" - ").append(task.data().getChatDescription(formatting)));
    }
}
