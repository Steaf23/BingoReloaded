package io.github.steaf23.bingoreloaded.util;

import io.github.steaf23.bingoreloaded.BingoReloadedCore;

public class TranslatedMessage extends Message
{
    public TranslatedMessage(String translatePath)
    {
        super(BingoReloadedCore.translate(translatePath));
    }
}
