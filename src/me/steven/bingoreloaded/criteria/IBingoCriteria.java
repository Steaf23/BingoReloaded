package me.steven.bingoreloaded.criteria;

import me.steven.bingoreloaded.player.BingoTeam;
import org.bukkit.Material;

public interface IBingoCriteria
{
    boolean isComplete();
    boolean tryComplete(BingoTeam team, int time);
    Material getMaterial();
}
