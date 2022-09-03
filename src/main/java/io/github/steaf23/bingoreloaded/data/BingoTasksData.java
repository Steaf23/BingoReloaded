package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.Message;
import io.github.steaf23.bingoreloaded.item.tasks.AbstractBingoTask;
import io.github.steaf23.bingoreloaded.item.tasks.AdvancementTask;
import io.github.steaf23.bingoreloaded.item.tasks.ItemTask;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;

import java.util.*;

/**
 * This class is used to interface with the lists.yml file.
 * Material and string comparisons are done using the Enum.name() method.
 */
public class BingoTasksData
{
    private static final YmlDataManager data = new YmlDataManager("lists.yml");

    public static List<ItemTask> getItemTasks(String listName)
    {
        List<ItemTask> result = new ArrayList<>();
        for (Map<?, ?> task : data.getConfig().getMapList(listName + ".item"))
        {
            if (!task.containsKey("key"))
            {
                continue;
            }

            boolean duplicate = false;
            for (var entry : result)
            {
                if (entry.getKey().equals(task.get("key")))
                {
                    duplicate = true;
                    break;
                }
            }

            if (!duplicate)
            {
                try
                {
                    ItemTask item;
                    if (task.containsKey("count"))
                        item = new ItemTask(Material.valueOf((String) task.get("key")), (int)task.get("count"));
                    else
                        item = new ItemTask(Material.valueOf((String) task.get("key")));
                    result.add(item);
                }
                catch (IllegalArgumentException exc)
                {
                    Message.log("ignoring item '" + task.get("key") + "' since it cannot be found!");
                }
            }
        }
        return result;
    }

    public static List<AdvancementTask> getAdvancementTasks(String listName)
    {
        List<AdvancementTask> result = new ArrayList<>();

        for (Map<?, ?> task : data.getConfig().getMapList(listName + ".advancement"))
        {
            if (!task.containsKey("key"))
            {
                continue;
            }

            boolean duplicate = false;
            for (var entry : result)
            {
                if (entry.advancement.getKey().toString().equals(task.get("key")))
                {
                    duplicate = true;
                    break;
                }
            }

            if (!duplicate)
            {
                Advancement adv = Bukkit.getAdvancement(NamespacedKey.fromString((String) task.get("key")));
                if (adv != null)
                {
                    AdvancementTask advancement = new AdvancementTask(adv);
                    result.add(advancement);
                }
                else
                {
                    Message.log("ignoring advancement '" + task.get("key") + "' since it cannot be found!");
                }
            }
        }
        return result;
    }

    public static List<AbstractBingoTask> getAllTasks(String listName)
    {
        List<AbstractBingoTask> result = new ArrayList<>();
        result.addAll(getItemTasks(listName));
        result.addAll(getAdvancementTasks(listName));

        return result;
    }

    public static void saveItemTasks(String listName, ItemTask... tasks)
    {
        data.getConfig().set(listName + ".item", null);
        data.saveConfig();
        List<Map<?, ?>> taskList = new ArrayList<>();
        for (var task : tasks)
        {
            boolean duplicate = false;
            for (var entry : taskList)
            {
                if (task.getKey().equals(entry.get("key")))
                {
                    duplicate = true;
                    break;
                }
            }

            if (!duplicate)
            {
                taskList.add(new HashMap<>()
                {{
                    put("key", task.getKey());
                    put("count", task.getCount());
                }});
            }
        }

        data.getConfig().set(listName + ".item", taskList);
        data.saveConfig();
    }

    public static void saveAdvancementTasks(String listName, AdvancementTask... tasks)
    {
        data.getConfig().set(listName + ".advancement", null);
        List<Map<String, Object>> taskList = new ArrayList<>();
        for (var task : tasks)
        {
            boolean duplicate = false;
            for (var entry : taskList)
            {
                if (task.advancement.getKey().toString().equals(entry.get("key")))
                {
                    duplicate = true;
                    break;
                }
            }

            if (!duplicate)
            {
                taskList.add(new HashMap<>(){{
                    put("key", task.getKey());
                }});
            }
        }
        data.getConfig().set(listName + ".advancement", taskList);
        data.saveConfig();
    }

    public static boolean listContainsKey(AbstractBingoTask task, List<AbstractBingoTask> list)
    {
        boolean result = false;
        for (var entry : list)
        {
            result = entry.getKey().equals(task.getKey());
        }
        return result;
    }

    public static int getTaskCount(String listName)
    {
        int count = 0;
        count += data.getConfig().getMapList(listName + ".item").size();
        count += data.getConfig().getMapList(listName + ".advancement").size();
        return count;
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

    public static AbstractBingoTask getRandomTask(String listName)
    {
        List<AbstractBingoTask> tasks = getAllTasks(listName);
        int idx = new Random().nextInt(tasks.size());
        return tasks.get(idx);
    }

    public static ItemTask getRandomItemTask(String listName)
    {
        List<ItemTask> tasks = getItemTasks(listName);
        int idx = new Random().nextInt(tasks.size());
        return tasks.get(idx);
    }
}
