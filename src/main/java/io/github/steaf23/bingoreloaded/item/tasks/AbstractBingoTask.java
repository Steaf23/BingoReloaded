package io.github.steaf23.bingoreloaded.item.tasks;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.GameTimer;
import io.github.steaf23.bingoreloaded.gui.cards.CardBuilder;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public abstract class AbstractBingoTask
{
    public final InventoryItem item;
    public final ChatColor nameColor;

    private BingoTeam completedBy = null;

    public abstract AbstractBingoTask copy();
    public abstract String getKey();
    public abstract String getDisplayName();
    public abstract List<String> getDescription();

    public AbstractBingoTask(Material material, ChatColor nameColor)
    {
        this.item = new InventoryItem(material, null);
        this.nameColor = nameColor;
    }

    protected void updateItem()
    {
        ItemMeta meta = item.getItemMeta();
        if (meta != null)
        {
            meta.setDisplayName(getDisplayName());
            meta.setLore(getDescription());
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }
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

        BingoReloaded.broadcast(ChatColor.GREEN + "Completed " + getDisplayName() + " by team " + completedBy.team.getColor() + completedBy.getName() + ChatColor.GREEN + "! At " + timeString);

        String crossedName = "" + ChatColor.GRAY + ChatColor.STRIKETHROUGH + ChatColor.stripColor(getDisplayName());
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

    public void voidTask()
    {
        item.setType(Material.BEDROCK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null)
        {
            meta.setDisplayName("" + ChatColor.BLACK + ChatColor.STRIKETHROUGH + getDisplayName());
            meta.setLore(List.of(ChatColor.BLACK + "This team is out of the game!"));
            item.setItemMeta(meta);
        }
    }
}
