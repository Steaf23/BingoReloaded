package io.github.steaf23.bingoreloaded.util;

import io.github.steaf23.bingoreloaded.BingoReloaded;

public class TranslatedMessage extends Message
{
    public TranslatedMessage(String translatePath)
    {
        super(BingoReloaded.data().translationData.translate(translatePath));
    }
}
