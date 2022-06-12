package me.steven.bingoreloaded.criteria;

import me.steven.bingoreloaded.BingoReloaded;
import me.steven.bingoreloaded.player.BingoTeam;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class ItemCriteria implements IBingoCriteria, Listener
{
    private final Material material;
    private boolean completed = false;

    public ItemCriteria(Material material)
    {
        this.material = material;
    }

    @Override
    public boolean isComplete()
    {
        return completed;
    }

    @Override
    public boolean tryComplete(BingoTeam team, int time)
    {
        return false;
    }

    @Override
    public Material getMaterial()
    {
        return material;
    }

    @EventHandler
    public void onPlayerDropItem(final PlayerDropItemEvent dropEvent)
    {
        if (dropEvent.getItemDrop().getItemStack().getType().equals(material) && !isComplete())
        {
            // TODO: add item stack stuff
            completed = true;
            var event = new BingoCriteriaCompleteEvent(this, dropEvent.getPlayer());
            Bukkit.getPluginManager().callEvent(event);
        }
    }
}
