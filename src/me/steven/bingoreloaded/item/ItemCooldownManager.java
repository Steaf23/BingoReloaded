package me.steven.bingoreloaded.item;

import me.steven.bingoreloaded.item.InventoryItem;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ItemCooldownManager
{
    public final InventoryItem item;
    private final long maxCooldownTime;
    private final Map<String, Long> playerTimes = new HashMap<>();

    public ItemCooldownManager(InventoryItem stack, long maxCooldownTime)
    {
        this.item = stack;
        this.maxCooldownTime = maxCooldownTime;
    }

    public boolean use(Player player)
    {
        String name = player.getName();

        if (!playerTimes.containsKey(name))
        {
            playerTimes.put(name, System.currentTimeMillis());
            return true;
        }

        if (playerTimes.get(name) + maxCooldownTime < System.currentTimeMillis())
        {
            playerTimes.put(name, System.currentTimeMillis());
            return true;
        }

        return false;
    }

    public double getTimeLeft(Player player)
    {
        return ((playerTimes.get(player.getName()) + maxCooldownTime) - System.currentTimeMillis()) / 1000.0;
    }
}
