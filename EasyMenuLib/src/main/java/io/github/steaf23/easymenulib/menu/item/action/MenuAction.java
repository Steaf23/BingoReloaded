package io.github.steaf23.easymenulib.menu.item.action;

import io.github.steaf23.easymenulib.menu.BasicMenu;
import io.github.steaf23.easymenulib.menu.item.MenuItem;
import org.jetbrains.annotations.NotNull;

/**
 * Class used to perform an action in a menu by clicking on an item
 */
public abstract class MenuAction
{
    protected MenuItem item;

    public void setItem(@NotNull MenuItem item) {
        this.item = item;
    }

    public abstract void use(BasicMenu.ActionArguments arguments);
}
