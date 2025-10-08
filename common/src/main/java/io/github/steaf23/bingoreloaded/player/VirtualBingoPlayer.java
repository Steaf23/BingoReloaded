package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.util.ComponentUtils;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.bingoreloaded.tasks.data.ItemTask;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

public class VirtualBingoPlayer implements BingoParticipant
{
    private BingoTeam team;
    private final UUID id;
    private final String name;
    private final BingoSession session;

    public VirtualBingoPlayer(UUID id, String name, BingoSession session) {
        this.id = id;
        this.name = name;
        this.session = session;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public BingoSession getSession() {
        return session;
    }

    @Override
    public @NotNull BingoTeam getTeam() {
        return team;
    }

    @Override
    public void setTeam(@Nullable BingoTeam team) {
        this.team = team;
    }

    @Override
    public Optional<PlayerHandle> sessionPlayer() {
        return Optional.empty();
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public Component getDisplayName() {
        return ComponentUtils.MINI_BUILDER.deserialize("<white>[<light_purple><tiny:'DUMMY'><white>] <gray>" + name + " <reset>");
    }

    @Override
    public void showDeathMatchTask(ItemTask task) {
    }

    @Override
    public void showCard(ItemTask deathMatchTask) {
    }

    @Override
    public boolean alwaysActive() {
        return true;
    }

    @Override
    public void giveBingoCard(int cardSlot, @NotNull StackHandle cardItem) {
    }

    @Override
    public void giveEffects(EnumSet<EffectOptionFlags> effects, int gracePeriod) {
    }

    @Override
    public void takeEffects(boolean force) {
    }

    @Override
    public void giveKit(PlayerKit kit) {
    }

    @Override
    public @NotNull Audience audience() {
        return Audience.empty();
    }
}
