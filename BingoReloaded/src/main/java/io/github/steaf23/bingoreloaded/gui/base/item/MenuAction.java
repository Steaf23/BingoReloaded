package io.github.steaf23.bingoreloaded.gui.base.item;

import io.github.steaf23.bingoreloaded.gui.base.BasicMenu;

public abstract class MenuAction
{
    protected MenuItem item;

    public void setItem(MenuItem item) {
        this.item = item;
    }

    public abstract void use(BasicMenu.ActionArguments arguments);
}
