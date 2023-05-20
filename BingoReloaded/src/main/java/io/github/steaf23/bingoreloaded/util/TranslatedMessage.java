package io.github.steaf23.bingoreloaded.util;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;

public class TranslatedMessage extends Message
{
    public TranslatedMessage(BingoTranslation translation)
    {
        super(translation.rawTranslation());
    }
}
