package io.github.steaf23.bingoreloaded.gui.inventory.item;

import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import io.github.steaf23.playerdisplay.inventory.BasicMenu;
import io.github.steaf23.playerdisplay.inventory.item.action.MenuAction;

import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
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
        arguments.player().sendMessage(Component.empty());
        arguments.player().sendMessage(task.data.getName().decorate(TextDecoration.BOLD));
        arguments.player().sendMessage(Component.text(" - ").append(task.data.getChatDescription()));
    }
}
