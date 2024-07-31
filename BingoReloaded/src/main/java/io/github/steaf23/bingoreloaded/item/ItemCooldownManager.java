package io.github.steaf23.bingoreloaded.item;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class ItemCooldownManager
{
    private final Map<Material, Long> cooldownMap;

    public ItemCooldownManager()
    {
        cooldownMap = new HashMap<>();
    }

    public void addCooldown(Material material, long cooldownMs)
    {
        cooldownMap.put(material, System.currentTimeMillis() + cooldownMs);
    }

    public long getTimeLeft(Material material)
    {
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

    public boolean isCooldownOver(Material material)
    {
        return getTimeLeft(material) == 0;
    }

    public void cancelCooldown(Material material)
    {
        if (!cooldownMap.containsKey(material))
        {
            return;
        }

        // remove cooldowns from the map
        cooldownMap.remove(material);
    }
}
