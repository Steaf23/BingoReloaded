package io.github.steaf23.bingoreloaded.lib.api.statistics;

public class StatisticsKeyConverter
{
    /**
     * @return translation key usable when translating statistics in translation components
     */
    public static String getMinecraftTranslationKey(VanillaStatistic statistic) {
        return statistic.keyStr();
    }
}
