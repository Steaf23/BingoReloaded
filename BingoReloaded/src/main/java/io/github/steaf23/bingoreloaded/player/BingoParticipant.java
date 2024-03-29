package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gui.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

public interface BingoParticipant
{
    BingoSession getSession();
    @Nullable
    BingoTeam getTeam();
    void setTeam(BingoTeam team);
    UUID getId();
    Optional<Player> sessionPlayer();
    String getDisplayName();
    void showDeathMatchTask(BingoTask task);
    boolean alwaysActive();

    void giveBingoCard();
    void giveEffects(EnumSet<EffectOptionFlags> effects, int gracePeriod);
    void takeEffects(boolean force);
    void giveKit(PlayerKit kit);
}
