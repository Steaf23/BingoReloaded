package me.steven.bingoreloaded.criteria;

import me.steven.bingoreloaded.player.BingoTeam;
import org.bukkit.Material;

public class StatCriteria implements IBingoCriteria
{
    private final Material material;

    public StatCriteria(Material material)
    {
        this.material = material;
    }

    @Override
    public boolean isComplete()
    {
        return false;
    }

    @Override
    public boolean tryComplete(BingoTeam team, int time)
    {
        return false;
    }

    @Override
    public Material getMaterial()
    {
        return material;
    }
}
