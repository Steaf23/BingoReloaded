package io.github.steaf23.bingoreloaded.lib.item.action;

import io.github.steaf23.bingoreloaded.lib.api.PlayerClickType;
import io.github.steaf23.bingoreloaded.lib.api.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.inventory.Menu;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import org.jetbrains.annotations.NotNull;


/**
 * Class used to perform an action in a menu by clicking on an item
 */
public abstract class MenuAction
{
    public record ActionArguments(Menu menu, PlayerHandle player, PlayerClickType clickType)
    {
    }

    protected ItemTemplate item;

    public void setItem(@NotNull ItemTemplate item) {
        this.item = item;
    }

    public abstract void use(ActionArguments arguments);
}
