package io.github.steaf23.bingoreloaded.api;

import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface CardMenu
{
    void setInfo(Component title, Component... description);
    void updateTasks(List<GameTask> tasks);
    void open(PlayerHandle entity);

    /**
     * FIXME: when menus can have changeable titles (i.e. when menu builders get added)
     */
    CardMenu copy(@Nullable Component alternateTitle);
    CardDisplayInfo displayInfo();
}
