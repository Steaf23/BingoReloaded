package io.github.steaf23.bingoreloaded.item.tasks;

import org.bukkit.Material;

import java.util.UUID;

public class BingoTaskBuilder
{
    public static BingoTask createTask(String type, String key, int count)
    {
        return new BingoTask(new ItemTask(Material.DIAMOND_HOE, 1));
//        Message.log("TASK: " + type + " " + key + " " + count);
//        return switch (type)
//            {
//                case "item" -> new ItemTask(Material.valueOf(key), count);
//                case "advancement" -> new AdvancementTask(Bukkit.getAdvancement(NamespacedKey.fromString(key)));
//                case "statistic" ->
//                {
//                    BingoStatistic statistic = null;
//                    String[] statString = key.split("/");
//                    Statistic stat = Statistic.valueOf(statString[0]);
//                    if (stat == null)
//                        yield null;
//
//                    if (statString.length == 2 && stat.isSubstatistic())
//                    {
//                        switch (stat.getType())
//                        {
//                            case ITEM, BLOCK -> {
//                                Material mat = Material.valueOf(statString[1]);
//                                if (mat != null)
//                                    statistic = new BingoStatistic(stat, mat);
//                            }
//                            case ENTITY -> {
//                                EntityType entity = EntityType.valueOf(statString[1]);
//                                if (entity != null)
//                                    statistic = new BingoStatistic(stat, entity);
//                            }
//                        }
//                    }
//
//                    if (statistic == null)
//                    {
//                        statistic = new BingoStatistic(stat);
//                    }
//
//                    yield new StatisticTask(statistic, count);
//                }
//                default -> null;
//            };
    }

    public static BingoTask createTask(String type, String key, int count, boolean voided, UUID completedBy)
    {
        return createTask(type, key, count);
    }
}
