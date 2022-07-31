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

            boolean duplicate = false;
            for (var entry : result)
            {
                if (entry.getName().equals(slot.get("name")))
                {
                    duplicate = true;
                    break;
                }
            }

            if (!duplicate)
            {
                ItemCardSlot item = new ItemCardSlot(Material.valueOf((String) slot.get("name")));
                if (slot.containsKey("count"))
                    item.setCount((int) slot.get("count"));

                result.add(item);
            }
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
        data.getConfig().set(listName + ".item", null);
        List<Map<?, ?>> slotList = new ArrayList<>();
        for (var slot : slots)
        {
            boolean duplicate = false;
            for (var entry : slotList)
            {
                if (slot.getName().equals(entry.get("name")))
                {
                    duplicate = true;
                    break;
                }
            }

            if (!duplicate)
            {
                slotList.add(new HashMap<>()
                {{
                    put("name", slot.getName());
                    put("count", slot.getCount());
                }});
            }
        }

        data.getConfig().set(listName + ".item", slotList);
        data.saveConfig();
    }

    public static void saveAdvancementSlots(String listName, AdvancementCardSlot... slots)
    {
        data.getConfig().set(listName + ".advancement", null);
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

    public static boolean isAlreadyAddedToList(AbstractCardSlot slot, List<AbstractCardSlot> list)
    {
        boolean result = false;
        for (var entry : list)
        {
            result = entry.getName().equals(slot.getName());
        }
        return result;
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

    public static AbstractCardSlot getRandomSlot(String listName)
    {
        List<AbstractCardSlot> slots = getAllSlots(listName);
        int idx = new Random().nextInt(slots.size());
        return slots.get(idx);
    }

    public static ItemCardSlot getRandomItemSlot(String listName)
    {
        List<ItemCardSlot> slots = getItemSlots(listName);
        int idx = new Random().nextInt(slots.size());
        return slots.get(idx);
    }
}
