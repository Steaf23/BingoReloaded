package io.github.steaf23.brmultimode;

import io.github.steaf23.bingoreloaded.BingoReloadedCore;
import io.github.steaf23.bingoreloaded.BingoSession;
import io.github.steaf23.bingoreloaded.command.BingoCommand;
import io.github.steaf23.bingoreloaded.data.ConfigData;
import org.bukkit.entity.Player;

public class MultiBingoCommand extends BingoCommand
{
    private final BingoGameManager manager;

    public MultiBingoCommand(ConfigData config, BingoGameManager manager)
    {
        super(config);
        this.manager = manager;
    }

    @Override
    public BingoSession getSession(Player player)
    {
        return manager.getSession(BingoReloadedCore.getWorldNameOfDimension(player.getWorld()));
    }
}
