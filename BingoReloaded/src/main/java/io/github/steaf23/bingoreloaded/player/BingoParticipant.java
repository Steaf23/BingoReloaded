package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.BingoSession;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public interface BingoParticipant
{
    public BingoSession getSession();
    @Nullable
    public BingoTeam getTeam();
    public Optional<Player> gamePlayer();
    public UUID getId();
    public String getDisplayName();
    public void showDeathMatchItem(Material item);
    public boolean alwaysActive();
}
