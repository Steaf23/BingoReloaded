package me.steven.bingoreloaded;

import me.steven.bingoreloaded.GUIInventories.cards.CardBuilder;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class BingoItem
{
    public final Material item;
    public final CustomItem stack;

    public BingoItem(Material item)
    {
        this.item = item;
        this.stack = new CustomItem(item, null, "I am a bingo Item :D");
    }

    public boolean isComplete(Team team)
    {
        if (completedBy == null) return false;

        return completedBy.getDisplayName().equals(team.getDisplayName());
    }

    public void complete(Team team)
    {
        if (completedBy != null) return;

        Material completeMaterial = CardBuilder.completeColor(team);

        completedBy = team;

        String name = stack.getType().name().replace("_", " ");
        name = WordUtils.capitalizeFully(name);
        BingoReloaded.broadcast(ChatColor.GREEN + "Completed " + name + " by team " + completedBy.getDisplayName() + "!");

        name = "" + ChatColor.GRAY + ChatColor.STRIKETHROUGH + name;
        stack.setType(completeMaterial);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null)
        {
            meta.setDisplayName(name);
            meta.setLore(List.of("Completed by team " + completedBy.getDisplayName()));
        }
        stack.setItemMeta(meta);
    }

    private Team completedBy = null;
}
