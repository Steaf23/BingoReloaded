package io.github.steaf23.bingoreloaded.item;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.GameTimer;
import io.github.steaf23.bingoreloaded.gui.cards.CardBuilder;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public abstract class AbstractCardSlot
{
    public final InventoryItem item;
    public final ChatColor nameColor;

    private BingoTeam completedBy = null;

    public abstract AbstractCardSlot copy();
    public abstract String getName();
    public abstract String getDisplayName();

    public AbstractCardSlot(Material material, ChatColor nameColor)
    {
        this.item = new InventoryItem(material, null, "I am a bingo Item :D");
        this.nameColor = nameColor;
    }

    public boolean isComplete()
    {
        return completedBy != null;
    }

    public boolean isCompletedByTeam(BingoTeam team)
    {
        if (!isComplete())
            return false;

        return completedBy.getName().equals(team.getName());
    }

    public void complete(BingoTeam team, int time)
    {
        if (completedBy != null)
            return;

        Material completeMaterial = CardBuilder.completeColor(team);

        completedBy = team;

        String timeString = GameTimer.getTimeAsString(time);

        BingoReloaded.broadcast(ChatColor.GREEN + "Completed " + getName() + " by team " + completedBy.team.getColor() + completedBy.getName() + ChatColor.GREEN + "! At " + timeString);

        String crossedName = "" + ChatColor.GRAY + ChatColor.STRIKETHROUGH + getName();
        item.setType(completeMaterial);
        ItemMeta meta = item.getItemMeta();
        if (meta != null)
        {
            meta.setDisplayName(crossedName);
            meta.setLore(List.of("Completed by team " + completedBy.team.getColor() + completedBy.getName(),
                    "At " + ChatColor.GOLD + timeString + ChatColor.RESET + ""));
            item.setItemMeta(meta);
        }
    }

    public BingoTeam getWhoCompleted()
    {
        return completedBy;
    }

    public void voidItem()
    {
        item.setType(Material.BEDROCK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null)
        {
            meta.setDisplayName("" + ChatColor.BLACK + ChatColor.STRIKETHROUGH + getName());
            meta.setLore(List.of(ChatColor.BLACK + "This team is out of the game!"));
            item.setItemMeta(meta);
        }
    }
}
