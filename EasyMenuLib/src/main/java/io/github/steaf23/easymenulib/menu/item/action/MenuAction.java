package io.github.steaf23.easymenulib.menu.item.action;

import io.github.steaf23.easymenulib.menu.BasicMenu;
import io.github.steaf23.easymenulib.menu.item.ItemTemplate;
import org.jetbrains.annotations.NotNull;


/**
 * Class used to perform an action in a menu by clicking on an item
 */
public abstract class MenuAction
{
    protected ItemTemplate item;

    public void setItem(@NotNull ItemTemplate item) {
        this.item = item;
    }

    public abstract void use(BasicMenu.ActionArguments arguments);
}
