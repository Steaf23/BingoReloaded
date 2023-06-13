package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.gui.base.MenuItem;
import org.bukkit.Material;

public class EffectOptionsMenuItem {
    private MenuItem menuItem;

    private EffectOptionFlags flag;

    public EffectOptionsMenuItem(EffectOptionFlags flag, MenuItem menuItem) {
        this.menuItem = menuItem;
        this.flag = flag;
    }

    public EffectOptionsMenuItem(EffectOptionFlags flag, int x, int y, Material material) {
        this.menuItem = new MenuItem(x, y, material, "");
        this.flag = flag;
    }

    public EffectOptionsMenuItem(EffectOptionFlags flag, int slot, Material material) {
        this.menuItem = new MenuItem(slot, material, "");
        this.flag = flag;
    }

    public EffectOptionsMenuItem(EffectOptionFlags flag, int x, int y, Material material, String name) {
        this.menuItem = new MenuItem(x, y, material, name);
        this.flag = flag;
    }

    public EffectOptionsMenuItem(EffectOptionFlags flag, int slot, Material material, String name) {
        this.menuItem = new MenuItem(slot, material, name);
        this.flag = flag;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public EffectOptionFlags getFlag() {
        return flag;
    }
}
