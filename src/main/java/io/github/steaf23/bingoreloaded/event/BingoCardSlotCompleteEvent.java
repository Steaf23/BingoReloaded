package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.BingoEventManager;
import io.github.steaf23.bingoreloaded.item.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerEvent;

public class BingoCardSlotCompleteEvent extends BingoEvent
{
    private final BingoTask slot;
    private final boolean bingo;
    private final BingoPlayer player;

    public BingoCardSlotCompleteEvent(BingoTask slot, BingoPlayer player, boolean bingo, String worldName)
    {
        super(worldName);
        this.player = player;
        this.slot = slot;
        this.bingo = bingo;
    }

    public BingoTask getCardSlot()
    {
        return slot;
    }

    public boolean hasBingo()
    {
        return bingo;
    }

    public BingoPlayer getPlayer()
    {
        return player;
    }
}
