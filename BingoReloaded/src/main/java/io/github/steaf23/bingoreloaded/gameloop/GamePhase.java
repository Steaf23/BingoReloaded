package io.github.steaf23.bingoreloaded.gameloop;

import io.github.steaf23.bingoreloaded.event.BingoParticipantJoinEvent;
import io.github.steaf23.bingoreloaded.event.BingoParticipantLeaveEvent;
import io.github.steaf23.bingoreloaded.event.BingoSettingsUpdatedEvent;

public interface GamePhase
{
    void handleParticipantJoined(final BingoParticipantJoinEvent event);
    void handleParticipantLeave(final BingoParticipantLeaveEvent event);
    void handleSettingsUpdated(final BingoSettingsUpdatedEvent event);
}
