package io.github.steaf23.bingoreloaded.gui.base.item;

import io.github.steaf23.bingoreloaded.gui.base.BasicMenu;

public interface MenuAction
{
    void use(MenuItem item, BasicMenu.ActionArguments arguments);
}
