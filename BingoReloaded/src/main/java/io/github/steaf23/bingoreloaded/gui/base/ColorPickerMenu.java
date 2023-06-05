package io.github.steaf23.bingoreloaded.gui.base;

import io.github.steaf23.bingoreloaded.util.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class ColorPickerMenu extends MenuInventory
{
    protected static final MenuItem NEXT = new MenuItem(53, Material.STRUCTURE_VOID, "" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "Scroll Left", "");
    protected static final MenuItem PREVIOUS = new MenuItem(45, Material.BARRIER, "" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "Scroll Right", "");

    private static final int HUE_AMOUNT = 25;

    private final Consumer<ChatColor> result;

    private final List<MenuItem> hueItems;

    private int scrollIndex = 0;

    private ColorPickerMenu(String title, Consumer<ChatColor> result, MenuInventory parent)
    {
        super(54, title + ChatColor.BLACK + " (" + ChatColor.BOLD + ChatColor.WHITE + "████" + ChatColor.RESET + ChatColor.BLACK + ")", parent);
        this.result = result;
        this.hueItems = new ArrayList<>();

        for (int i = 0; i < HUE_AMOUNT; i++)
        {
            Color col = Color.getHSBColor(i * (1.0f / (HUE_AMOUNT - 1)), 1.0f, 1.0f);
            hueItems.add(createColoredItem(col));
        }

        addItem(PREVIOUS);
        addItem(NEXT);

        setHueBar(0);

        for (int i = 0; i < 45; i++)
        {
            addItem(createColoredItem(new Color(0)));
        }

        updateDisplay(new Color(Integer.parseInt(hueItems.get(0).getKey().substring(1), 16)));
    }

    public static void open(String title, Consumer<ChatColor> result, Player player, MenuInventory parent)
    {
        new ColorPickerMenu(title, result, parent).open(player);
    }

    @Override
    public void onItemClicked(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        MenuItem clickedItem = new MenuItem(getOption(slotClicked));

        if (slotClicked == 53)
        {
            scrollIndex -= 3;
            setHueBar(scrollIndex);
        }
        else if (slotClicked == 45)
        {
            scrollIndex += 3;
            setHueBar(scrollIndex);
        }
        else if (slotClicked > 45 && slotClicked < 53)
        {
            String hex = clickedItem.getKey();
            updateDisplay(new Color(Integer.parseInt(hex.substring(1), 16)));
        }
        else
        {
            String key = clickedItem.getKey();
            result.accept(ChatColor.of(key));
            close(player);
        }
    }

    private static MenuItem createColoredItem(Color color)
    {
        String hex = String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
        MenuItem item = new MenuItem(Material.LEATHER_CHESTPLATE, ChatColor.of(hex) + hex, "").setKey(hex);
        if (item.getItemMeta() instanceof LeatherArmorMeta armorMeta)
        {
            armorMeta.setColor(org.bukkit.Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()));
            item.setItemMeta(armorMeta);
        }
        return item;
    }

    public void setHueBar(int startingFrom)
    {
        if (hueItems.size() < 7)
        {
            Message.log("Add atleast 7 hue items!");
            return;
        }

        for (int i = 0; i < 7; i++)
        {
            addItem(hueItems.get(Math.floorMod(i + startingFrom, hueItems.size())).copyToSlot(1 + i, 5));
        }
    }

    public void updateDisplay(Color color)
    {
        float hue = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null)[0];
        for (int y = 0; y < 5; y++)
        {
            for (int x = 0; x < 9; x++)
            {
                float saturation = 1.0f - (y * (1.0f / 4));
                float brightness = 1.0f - (x * (1.0f / 8));

                Color targetColor = Color.getHSBColor(hue, saturation, brightness);

                MenuItem item = createColoredItem(targetColor).setSlot(MenuItem.slotFromXY(x, y));
                addItem(item);
            }
        }
    }
}
