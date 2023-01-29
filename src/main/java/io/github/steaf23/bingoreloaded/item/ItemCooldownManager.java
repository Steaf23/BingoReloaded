package io.github.steaf23.bingoreloaded.item;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemCooldownManager
{
    private static Map<UUID, Map<Material, Long>> cooldownMap;

    private static ItemCooldownManager INSTANCE;

    public static void addCooldown(UUID playerID, ItemStack item, long cooldownMs)
    {
        if (!cooldownMap.containsKey(playerID))
        {
            cooldownMap.put(playerID, new HashMap<>());
        }
        cooldownMap.get(playerID).put(item.getType(), System.currentTimeMillis() + cooldownMs);
    }

    public static long getTimeLeft(UUID playerID, ItemStack item)
    {
        Material material = item.getType();
        if (!cooldownMap.containsKey(playerID))
        {
            return 0;
        }
        if (!cooldownMap.get(playerID).containsKey(material))
        {
            return 0;
        }

        // remove cooldowns from the map if they ran out
        long timeLeft = cooldownMap.get(playerID).get(material) - System.currentTimeMillis();
        if (timeLeft == 0)
        {
            cooldownMap.get(playerID).remove(material);
            if (cooldownMap.get(playerID).size() == 0)
            {
                cooldownMap.remove(playerID);
            }
        }

        return timeLeft > 0 ? timeLeft : 0;
    }

    public static boolean isCooldownOver(UUID player, ItemStack item)
    {
        return getTimeLeft(player, item) == 0;
    }

    public static void cancelCooldown(Player player, ItemStack item)
    {
        UUID playerID = player.getUniqueId();
        Material material = item.getType();
        if (!cooldownMap.containsKey(playerID))
        {
            return;
        }
        if (!cooldownMap.get(playerID).containsKey(material))
        {
            return;
        }

        // remove cooldowns from the map
        cooldownMap.get(playerID).remove(material);
        if (cooldownMap.get(playerID).size() == 0)
        {
            cooldownMap.remove(playerID);
        }
    }

    public static void create()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new ItemCooldownManager();
        }
    }

    private ItemCooldownManager()
    {
        cooldownMap = new HashMap<>();
    }
}
