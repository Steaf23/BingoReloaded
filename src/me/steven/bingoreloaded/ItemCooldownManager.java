package me.steven.bingoreloaded;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ItemCooldownManager
{
    public final ItemStack stack;
    // cooldown time in milliseconds
    private long maxCooldownTime = 0;

    public Map<String, Long> playerTimes = new HashMap<>();

    public ItemCooldownManager(ItemStack stack, long maxCooldownTime)
    {
        this.stack = stack;
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
