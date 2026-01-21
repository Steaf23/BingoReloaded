package io.github.steaf23.bingoreloaded.gui.inventory.core.action;

import io.github.steaf23.bingoreloaded.gui.inventory.core.Menu;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;


/**
 * Class used to perform an action in a menu by clicking on an item
 */
public abstract class MenuAction
{
    public record ActionArguments(Menu menu, PlayerHandle player, ClickType clickType)
    {
    }

    protected ItemTemplate item;

    public void setItem(@NotNull ItemTemplate item) {
        this.item = item;
    }

    public ItemTemplate item() {
        return item;
    }

    public abstract void use(ActionArguments arguments);
}
