package io.github.steaf23.bingoreloaded.core.data;

public class DataStorage
{
    public final BingoCardsData cardsData;
    public final BingoStatsData statsData;
    public final TranslationData translationData;

    public DataStorage()
    {
        this.cardsData = new BingoCardsData();
        this.statsData = new BingoStatsData();
        this.translationData = new TranslationData();
    }
}
