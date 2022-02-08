package me.steven.bingoreloaded;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class BingoItem
{
    public final Material item;
    public final ItemStack stack;

    private boolean completed = false;

    public BingoItem(Material item)
    {
        this.item = item;
        this.stack = new ItemStack(item);

        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return;

        meta.setLore(List.of("I am a bingo Item :D"));
        stack.setItemMeta(meta);
    }

    public static final Map<String, List<Material>> ITEMS = new HashMap<>() {{
        put("normal", new ArrayList<>() {{
            add(Material.CYAN_CONCRETE);
            add(Material.COBBLESTONE);
            add(Material.STONE);
            add(Material.GRANITE);
            add(Material.DIORITE);
            add(Material.ANDESITE);
        }});
    }};

    public boolean isCompleted()
    {
        return completed;
    }

    public void complete()
    {
        if (completed) return;

        completed = true;
        stack.setType(Material.BARRIER);
        BingoReloaded.broadcast(ChatColor.GREEN + "Completed " + item.toString() + "!");
    }
}
