package io.github.steaf23.brpartymode;

import io.github.steaf23.bingoreloaded.BingoSession;
import io.github.steaf23.bingoreloaded.command.BingoCommand;
import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.entity.Player;

public class PartyBingoCommand extends BingoCommand
{
    private final BingoSession session;

    public PartyBingoCommand(ConfigData config, BingoSession session)
    {
        super(config);
        this.session = session;
    }

    @Override
    public BingoSession getSession(Player player)
    {

        return session;
    }
}
