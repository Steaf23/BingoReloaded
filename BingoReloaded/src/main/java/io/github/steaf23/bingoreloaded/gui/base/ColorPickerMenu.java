package io.github.steaf23.bingoreloaded.gui.base;


import io.github.steaf23.bingoreloaded.util.FlexColor;
import io.github.steaf23.bingoreloaded.util.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class ColorPickerMenu extends BasicMenu
{
    private static final MenuItem NEXT = new MenuItem(53, Material.STRUCTURE_VOID, "" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "Scroll Left", "");
    private static final MenuItem PREVIOUS = new MenuItem(45, Material.BARRIER, "" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "Scroll Right", "");

    private static final int HUE_AMOUNT = 25;

    private final Consumer<ChatColor> result;

    private final List<MenuItem> hueItems;

    private int scrollIndex = 0;

    public ColorPickerMenu(MenuManager manager, String title, Consumer<ChatColor> result) {
        super(manager, title, 6);
        this.result = result;
        this.hueItems = new ArrayList<>();

        for (int i = 0; i < HUE_AMOUNT; i++) {
            Color col = Color.getHSBColor(i * (1.0f / (HUE_AMOUNT - 1)), 1.0f, 1.0f);
            ChatColor chatColor = ChatColor.of(col);
            hueItems.add(io.github.steaf23.bingoreloaded.gui.base.MenuItem.createColoredLeather(chatColor, Material.LEATHER_CHESTPLATE)
                    .setCompareKey(FlexColor.asHex(chatColor)));
        }

        addItem(PREVIOUS);
        addItem(NEXT);

        setHueBar(0);

        for (int i = 0; i < 45; i++) {
            ChatColor color = ChatColor.of(new Color(0));
            addItem(io.github.steaf23.bingoreloaded.gui.base.MenuItem.createColoredLeather(color, Material.LEATHER_CHESTPLATE)
                    .setCompareKey(FlexColor.asHex(color)));
        }

        updateDisplay(new Color(Integer.parseInt(hueItems.get(0).getCompareKey().substring(1), 16)));
    }

    @Override
    public boolean onClick(InventoryClickEvent event, HumanEntity player, MenuItem clickedItem, ClickType clickType) {
        int slotClicked = clickedItem.getSlot();

        if (slotClicked == 53) {
            scrollIndex -= 3;
            setHueBar(scrollIndex);
        } else if (slotClicked == 45) {
            scrollIndex += 3;
            setHueBar(scrollIndex);
        } else if (slotClicked > 45 && slotClicked < 53) {
            String hex = clickedItem.getCompareKey();
            updateDisplay(new Color(Integer.parseInt(hex.substring(1), 16)));
        } else {
            String key = clickedItem.getCompareKey();
            close(player);
            result.accept(ChatColor.of(key));
        }
        return super.onClick(event, player, clickedItem, clickType);
    }

    private void setHueBar(int startingFrom) {
        if (hueItems.size() < 7) {
            Message.log("Add at least 7 hue items!");
            return;
        }

        for (int i = 0; i < 7; i++) {
            addItem(hueItems.get(Math.floorMod(i + startingFrom, hueItems.size())).copyToSlot(1 + i, 5));
        }
    }

    private void updateDisplay(Color color) {
        float hue = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null)[0];
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 9; x++) {
                float saturation = 1.0f - (y * (1.0f / 4));
                float brightness = 1.0f - (x * (1.0f / 8));

                Color targetColor = Color.getHSBColor(hue, saturation, brightness);

                ChatColor chatColor = ChatColor.of(targetColor);

                io.github.steaf23.bingoreloaded.gui.base.MenuItem item = io.github.steaf23.bingoreloaded.gui.base.MenuItem.createColoredLeather(chatColor, Material.LEATHER_CHESTPLATE)
                        .setCompareKey(FlexColor.asHex(chatColor))
                        .setSlot(io.github.steaf23.bingoreloaded.gui.base.MenuItem.slotFromXY(x, y));
                addItem(item);
            }
        }
    }
}
