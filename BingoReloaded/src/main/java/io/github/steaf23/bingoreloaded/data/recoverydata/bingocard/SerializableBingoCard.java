package io.github.steaf23.bingoreloaded.data.recoverydata.bingocard;

import io.github.steaf23.bingoreloaded.cards.BingoCard;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;

public interface SerializableBingoCard {
    BingoCard toBingoCard(BingoSession session);
}
