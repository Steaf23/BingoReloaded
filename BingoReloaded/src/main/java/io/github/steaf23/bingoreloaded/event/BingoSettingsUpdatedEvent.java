package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;

public class BingoSettingsUpdatedEvent extends BingoEvent
{
    private final BingoSettings newSettings;

    public BingoSettingsUpdatedEvent(BingoSettings newSettings, BingoSession session)
    {
        super(session);
        this.newSettings = newSettings;
    }

    public BingoSettings getNewSettings()
    {
        return newSettings;
    }
}
