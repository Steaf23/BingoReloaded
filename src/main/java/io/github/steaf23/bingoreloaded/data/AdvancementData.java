package io.github.steaf23.bingoreloaded.data;

public class AdvancementData
{
    private static final YMLDataManager data = new YMLDataManager("advancements.yml");

    public static String getAdvancementTitle(String key)
    {
        return data.getConfig().getString(key);
    }
}
