package me.steven.bingoreloaded;

import me.steven.bingoreloaded.cards.BingoCard;
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

    public boolean isCompleted()
    {
        return completed;
    }

    public void complete()
    {
        if (completed) return;

        completed = true;
        String name = stack.getItemMeta().getDisplayName();
        stack.setType(Material.BARRIER);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        stack.setItemMeta(meta);
        BingoReloaded.broadcast(ChatColor.GREEN + "Completed " + item.toString() + "!");
    }

    public static final Map<BingoCard.CardDifficulty, List<Material>> ITEMS = new HashMap<>() {{
        put(BingoCard.CardDifficulty.NORMAL, new ArrayList<>() {{
            add(Material.CYAN_CONCRETE);
            add(Material.COBBLESTONE);
            add(Material.STONE);
            add(Material.GRANITE);
            add(Material.DIORITE);
            add(Material.ANDESITE);
            add(Material.MAGENTA_CONCRETE);
            add(Material.CANDLE);
            add(Material.GRAY_DYE);
            add(Material.DIAMOND);
        }});
    }};
}
