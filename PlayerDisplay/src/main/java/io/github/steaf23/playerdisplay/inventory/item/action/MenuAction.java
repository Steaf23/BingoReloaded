package io.github.steaf23.playerdisplay.inventory.item.action;

import io.github.steaf23.playerdisplay.inventory.item.ItemTemplate;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;


/**
 * Class used to perform an action in a menu by clicking on an item
 */
public abstract class MenuAction
{
    public record ActionArguments(HumanEntity player, ClickType clickType)
    {
    }

    protected ItemTemplate item;

    public void setItem(@NotNull ItemTemplate item) {
        this.item = item;
    }

    public abstract void use(ActionArguments arguments);
}
