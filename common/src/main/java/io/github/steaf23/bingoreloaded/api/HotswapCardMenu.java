package io.github.steaf23.bingoreloaded.api;

import io.github.steaf23.bingoreloaded.cards.slot.TickingTaskSlot;

import java.util.List;

public interface HotswapCardMenu extends CardMenu
{
    void updateTaskHolders(List<TickingTaskSlot> taskHolders);
}
