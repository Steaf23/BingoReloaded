package io.github.steaf23.bingoreloaded.util;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;

/**
 * @deprecated will be removed as adventure components took over
 */
@Deprecated
public class TranslatedMessage extends Message
{
    public TranslatedMessage(BingoTranslation translation)
    {
        super(translation.rawTranslation());
    }
}
