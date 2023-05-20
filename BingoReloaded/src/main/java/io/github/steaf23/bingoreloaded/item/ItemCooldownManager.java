package io.github.steaf23.bingoreloaded.item;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemCooldownManager
{
    private Map<Material, Long> cooldownMap;

    public ItemCooldownManager()
    {
        cooldownMap = new HashMap<>();
    }

    public void addCooldown(ItemStack item, long cooldownMs)
    {
        cooldownMap.put(item.getType(), System.currentTimeMillis() + cooldownMs);
    }

    public long getTimeLeft(ItemStack item)
    {
        Material material = item.getType();
        if (!cooldownMap.containsKey(material))
        {
            return 0;
        }

        // remove cooldowns from the map if they ran out
        long timeLeft = cooldownMap.get(material) - System.currentTimeMillis();
        if (timeLeft == 0)
        {
            cooldownMap.remove(material);
        }

        return timeLeft > 0 ? timeLeft : 0;
    }

    public boolean isCooldownOver(ItemStack item)
    {
        return getTimeLeft(item) == 0;
    }

    public void cancelCooldown(ItemStack item)
    {
        Material material = item.getType();
        if (!cooldownMap.containsKey(material))
        {
            return;
        }

        // remove cooldowns from the map
        cooldownMap.remove(material);
    }
}
