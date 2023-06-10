package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class VirtualBingoPlayer implements BingoParticipant
{
    private final BingoTeam team;
    private final UUID id;
    private final String name;
    private final BingoSession session;

    VirtualBingoPlayer(UUID id, String name, BingoTeam team, BingoSession session)
    {
        this.team = team;
        this.id = id;
        this.name = name;
        this.session = session;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public BingoSession getSession()
    {
        return session;
    }

    @Nullable
    @Override
    public BingoTeam getTeam()
    {
        return team;
    }

    @Override
    public Optional<Player> gamePlayer()
    {
        return Optional.empty();
    }

    @Override
    public UUID getId()
    {
        return id;
    }

    @Override
    public String getDisplayName()
    {
        return ChatColor.WHITE + "[" + ChatColor.LIGHT_PURPLE + ChatColor.ITALIC + "BOT" + ChatColor.RESET + ChatColor.WHITE + "] " + ChatColor.GRAY + name + ChatColor.RESET + "";
    }

    @Override
    public void showDeathMatchTask(BingoTask task)
    {
    }

    @Override
    public boolean alwaysActive()
    {
        return true;
    }
}
