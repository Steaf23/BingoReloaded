package me.steven.bingoreloaded.data;

import org.bukkit.Material;

import java.util.*;

/**
 * This class is used to interface with the items.yml file to allow the user to set individual difficulty for Bingo items.
 * Material and string comparisons are done using the Enum.name() method.
 */
public class BingoItemData
{
    private static final YMLDataManager data = new YMLDataManager("items.yml");

    /**
     * @return A Map of all categories and the items associated with it.
     */
    public static Map<String, List<Material>> getAllItems()
    {
        Map<String, List<Material>> result = new HashMap<>();
        for (String category : getCategories())
        {
            result.put(category, getItems(category));
        }

        return result;
    }

    /**
     * @param category Name of the category list to get items from.
     *                 Make sure that this parameter is equal to the actual list path in the items.yml file.
     * @return The List of Materials that have been saved in the given category
     */
    public static List<Material> getItems(String category)
    {
        List<Material> result = new ArrayList<>();
        if (!data.getConfig().contains(category)) return result;
        if (data.getConfig().get(category) == null) return result;

        data.getConfig().getStringList(category).forEach(name ->
                result.add(Material.getMaterial(name)));

        return result;
    }

    public static int getItemCount(String category)
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
    public static void saveItems(String category, Material... items)
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

    public static boolean removeItemList(String listName)
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
     * Add empty lists to the items.yml file.
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
     * @return All the category names present in the items.yml file.
     */
    public static Set<String> getCategories()
    {
        return data.getConfig().getKeys(false);
    }
}
