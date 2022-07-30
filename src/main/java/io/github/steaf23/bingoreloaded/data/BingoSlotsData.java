package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.item.AbstractCardSlot;
import io.github.steaf23.bingoreloaded.item.AdvancementCardSlot;
import io.github.steaf23.bingoreloaded.item.ItemCardSlot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.util.*;

/**
 * This class is used to interface with the lists.yml file.
 * Material and string comparisons are done using the Enum.name() method.
 */
public class BingoSlotsData
{
    private static final YMLDataManager data = new YMLDataManager("lists.yml");

    public static List<ItemCardSlot> getItemSlots(String listName)
    {
        List<ItemCardSlot> result = new ArrayList<>();
        for (Map<?, ?> slot : data.getConfig().getMapList(listName + ".item"))
        {
            if (!slot.containsKey("name"))
            {
                continue;
            }

            ItemCardSlot item = new ItemCardSlot(Material.valueOf((String) slot.get("name")));
            if (slot.containsKey("count"))
                item.count = (int) slot.get("count");

            result.add(item);
        }
        return result;
    }

    public static List<AdvancementCardSlot> getAdvancementSlots(String listName)
    {
        List<AdvancementCardSlot> result = new ArrayList<>();

        for (Map<?, ?> slot : data.getConfig().getMapList(listName + ".advancement"))
        {
            if (!slot.containsKey("name"))
            {
                continue;
            }

            AdvancementCardSlot advancement = new AdvancementCardSlot(Material.PAPER);
            if (slot.containsKey("count"))
                advancement.advancement = Bukkit.getAdvancement(NamespacedKey.fromString((String) slot.get("name")));

            result.add(advancement);
        }
        return result;
    }

    public static List<AbstractCardSlot> getAllSlots(String listName)
    {
        List<AbstractCardSlot> result = new ArrayList<>();
        result.addAll(getItemSlots(listName));
        result.addAll(getAdvancementSlots(listName));
        return result;
    }

    public static void saveItemSlots(String listName, ItemCardSlot... slots)
    {
        List<Map<String, Object>> slotList = new ArrayList<>();
        for (ItemCardSlot slot : slots)
        {
            slotList.add(new HashMap<>(){{
                put("name", slot.getName());
                put("count", slot.count);
            }});
        }
        data.getConfig().set(listName + ".item", slotList);
        data.saveConfig();
    }

    public static void saveAdvancementSlots(String listName, AdvancementCardSlot... slots)
    {
        List<Map<String, Object>> slotList = new ArrayList<>();
        for (AdvancementCardSlot slot : slots)
        {
            slotList.add(new HashMap<>(){{
                put("name", slot.getName());
            }});
        }
        data.getConfig().set(listName + ".advancement", slots);
        data.saveConfig();
    }

    public static int getSlotCount(String listName)
    {
        return data.getConfig().getStringList(listName).size();
    }

    public static boolean removeList(String listName)
    {
        if (data.getConfig().contains(listName))
        {
            data.getConfig().set(listName, null);
            data.saveConfig();
            return true;
        }
        return false;
    }

    /**
     * Add empty lists to the lists.yml file.
     * @param names names of the categories to add
     */
    public static void addEmptyList(String... names)
    {
        for (String n : names)
        {
            if (data.getConfig().contains(n)) return;
            if (n.equals("")) return;

            data.getConfig().set(n, new String[]{});
        }
        data.saveConfig();
    }

    /**
     * @return All the category names present in the lists.yml file.
     */
    public static Set<String> getListNames()
    {
        return data.getConfig().getKeys(false);
    }
}
