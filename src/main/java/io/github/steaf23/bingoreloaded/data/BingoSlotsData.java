package io.github.steaf23.bingoreloaded.data;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This class is used to interface with the lists.yml file to allow the user to set individual difficulty for Bingo items.
 * Material and string comparisons are done using the Enum.name() method.
 */
public class BingoSlotsData
{
    private static final YMLDataManager data = new YMLDataManager("lists.yml");

    /**
     * @param category Name of the category list to get items from.
     *                 Make sure that this parameter is equal to the actual list path in the lists.yml file.
     * @return The List of Materials that have been saved in the given category
     */
    public static List<Material> getSlots(String category)
    {
        List<Material> result = new ArrayList<>();
        if (!data.getConfig().contains(category)) return result;
        if (data.getConfig().get(category) == null) return result;

        data.getConfig().getStringList(category).forEach(name ->
                 result.add(Material.getMaterial(name)));

        return result;
    }

    public static int getSlotCount(String category)
    {
        return data.getConfig().getStringList(category).size();
    }

    /**
     * Used to add a new item(s) to a category
     * @param category The category to save the item as.
     *                 This can for example be used to denote difficulty for an item added.
     *                 A new Category will be created if the given name does not exist yet.
     * @param items The actual Materials that will be added to the category list.
     */
    public static void saveSlots(String category, Material... items)
    {
        if (!getCategories().contains(category) && !category.equals(""))
        {
            addCategories(category);
        }

        for (String c : getCategories())
        {
            List<String> names = data.getConfig().getStringList(c);

            for (Material item : items)
            {
                if (category.equals(c)) // Add the item to the given category.
                {
                    if (!names.contains(item.name()))
                        names.add(item.name());
                }
                else // Remove the item from the other categories.
                {
                    names.remove(item.name());
                }
            }
            data.getConfig().set(c, names);
        }
        data.saveConfig();
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
    public static void addCategories(String... names)
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
    public static Set<String> getCategories()
    {
        return data.getConfig().getKeys(false);
    }
}
