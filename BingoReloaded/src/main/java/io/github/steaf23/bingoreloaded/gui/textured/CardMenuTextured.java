package io.github.steaf23.bingoreloaded.gui.textured;

import io.github.steaf23.bingoreloaded.gui.base.BasicMenu;
import io.github.steaf23.bingoreloaded.gui.base.MenuManager;

public class CardMenuTextured extends BasicMenu implements Textured {

    public CardMenuTextured(MenuManager manager, String initialTitle, int rows) {
        super(manager, initialTitle, rows);
    }

    @Override
    public String getTexture() {
        return "ㇺ";
    }

    @Override
    public int getHorizontalImageOffset() {
        return 48;
    }
}
