package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.data.TranslationData;

import java.util.function.Function;

public interface BingoTranslatable
{
    Function<TranslationData, String> translate();
}
