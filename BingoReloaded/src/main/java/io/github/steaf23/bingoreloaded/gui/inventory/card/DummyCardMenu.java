package io.github.steaf23.bingoreloaded.gui.inventory.card;

import io.github.steaf23.bingoreloaded.tasks.GameTask;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.HumanEntity;

import java.util.List;

public class DummyCardMenu implements CardMenu
{
    private List<GameTask> tasks;

    @Override
    public void setInfo(Component title, Component... description) {
    }

    @Override
    public void updateTasks(List<GameTask> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void open(HumanEntity entity) {
    }

    @Override
    public CardMenu copy() {
        DummyCardMenu menu = new DummyCardMenu();
        menu.tasks = tasks;
        return menu;
    }
}
