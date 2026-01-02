package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.cards.TaskCard;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.lib.api.PotionEffectInstance;
import io.github.steaf23.bingoreloaded.lib.api.ServerSoftware;
import io.github.steaf23.bingoreloaded.lib.api.StatusEffectType;
import io.github.steaf23.bingoreloaded.lib.api.item.InventoryHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagDataStorage;
import io.github.steaf23.bingoreloaded.lib.util.ComponentUtils;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.bingoreloaded.tasks.data.ItemTask;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * This class describes a player in a single bingo session.
 * This class will still exist if the player leaves the game/world.
 * This instance will be removed when the session gets destroyed.
 */
public class BingoPlayer implements BingoParticipant
{
	public final String playerName;

	private BingoTeam team;
    private final BingoSession session;
    private final UUID playerId;
    private final Component displayName;

    private final ServerSoftware server;

    public BingoPlayer(PlayerHandle player, BingoSession session) {
        this.playerId = player.uniqueId();
        this.session = session;
        this.server = session.getGameManager().getPlatform();
        this.playerName = player.playerName();
        this.displayName = player.displayName();
        this.team = null;
    }

    /**
     * @return the player that belongs to this BingoPlayer, if this player is in a session world, otherwise returns null
     */
    public Optional<PlayerHandle> sessionPlayer() {
        PlayerHandle player = server.getPlayerFromUniqueId(playerId);
        if (player == null || !session.hasPlayer(player)) {
            return Optional.empty();
        }
        return Optional.of(player);
    }

    @Override
    public String getName() {
        return playerName;
    }

    @Override
    public UUID getId() {
        return playerId;
    }

    @Override
    public Component getDisplayName() {
        return displayName;
    }

    @Override
    public void giveKit(PlayerKit kit) {
        if (sessionPlayer().isEmpty())
            return;

        PlayerHandle player = sessionPlayer().get();

        var items = kit.getItems(getTeam().getColor(), server);
        player.closeInventory();
        InventoryHandle inv = player.inventory();
        inv.clearContents();
        items.forEach(i ->
        {
            TagDataStorage store = i.stack().getStorage();

            store.setBoolean("kit_item", true);
            i.stack().setStorage(store);
            inv.setItem(i.slot(), i.stack());
        });
    }

    @Override
    public void giveBingoCard(int cardSlot, @NotNull StackHandle cardItem) {
        if (sessionPlayer().isEmpty())
            return;

        PlayerHandle player = sessionPlayer().get();

        server.runTask(task -> {
            for (StackHandle itemStack : player.inventory().contents()) {
                if (PlayerKit.CARD_ITEM.isCompareKeyEqual(itemStack)) {
                    player.inventory().removeItem(itemStack);
                    break;
                }
            }
            StackHandle existingItem = player.inventory().getItem(cardSlot);

            player.inventory().setItem(cardSlot, cardItem);
            if (!existingItem.type().isAir()) {
                Map<Integer, StackHandle> leftOver = player.inventory().addItem(existingItem);
                for (StackHandle stack : leftOver.values()) {
                    player.world().dropItem(stack, player.position());
                }
            }
        });
    }

    @Override
    public void giveEffects(EnumSet<EffectOptionFlags> effects, int gracePeriod) {
        if (sessionPlayer().isEmpty())
            return;

        takeEffects(false);
        PlayerHandle player = sessionPlayer().get();

        server.runTask(task -> {
            if (effects.contains(EffectOptionFlags.NIGHT_VISION))
                player.addEffect(new PotionEffectInstance(StatusEffectType.of("minecraft:night_vision"), PotionEffectInstance.INFINITE_DURATION).setParticles(false));
            if (effects.contains(EffectOptionFlags.WATER_BREATHING))
                player.addEffect(new PotionEffectInstance(StatusEffectType.of("minecraft:water_breathing"), PotionEffectInstance.INFINITE_DURATION).setParticles(false));
            if (effects.contains(EffectOptionFlags.FIRE_RESISTANCE))
                player.addEffect(new PotionEffectInstance(StatusEffectType.of("minecraft:fire_resistance"), PotionEffectInstance.INFINITE_DURATION).setParticles(false));
            if (effects.contains(EffectOptionFlags.SPEED))
                player.addEffect(new PotionEffectInstance(StatusEffectType.of("minecraft:speed"), PotionEffectInstance.INFINITE_DURATION).setParticles(false));
            player.addEffect(new PotionEffectInstance(StatusEffectType.of("minecraft:saturation"), 2)
                    .setAmplifier(100)
                    .setParticles(false));
            player.addEffect(new PotionEffectInstance(StatusEffectType.of("minecraft:regeneration"), 2)
                    .setAmplifier(100)
                    .setParticles(false));
            player.addEffect(new PotionEffectInstance(StatusEffectType.of("minecraft:resistance"), BingoReloaded.ONE_SECOND * gracePeriod)
                    .setAmplifier(100)
                    .setParticles(false));
        });
    }

    /**
     * @param force ignore if the player is actually in the world playing the game at this moment.
     */
    @Override
    public void takeEffects(boolean force) {
        if (force) {
            PlayerHandle p = server.getPlayerFromUniqueId(playerId);
            if (p != null) {
                p.clearAllEffects();
            }
        } else {
            if (sessionPlayer().isEmpty())
                return;

            sessionPlayer().get().clearAllEffects();
        }
    }

    @Override
    public void showDeathMatchTask(ItemTask task) {
        if (sessionPlayer().isEmpty())
            return;

        sessionPlayer().get()
                .sendMessage(BingoMessage.DEATHMATCH_ITEM.asPhrase(ComponentUtils.itemName(task.itemType()))
                        .color(NamedTextColor.GOLD));
    }

    @Override
    public void showCard(ItemTask deathMatchTask) {
        BingoTeam playerTeam = getTeam();
        if (playerTeam == null) {
            ConsoleMessenger.bug("Invalid team for player " + playerName + "!", this);
            return;
        }
        Optional<TaskCard> card = playerTeam.getCard();

        sessionPlayer().ifPresent(player -> {
            if (deathMatchTask != null) {
                showDeathMatchTask(deathMatchTask);
                return;
            }

            // if the player is actually participating, show it
            card.ifPresentOrElse(c -> c.showInventory(player), () -> BingoMessage.NO_PLAYER_CARD.sendToAudience(player));
        });
    }

    @Override
    public boolean alwaysActive() {
        return false;
    }

    @Override
    public BingoSession getSession() {
        return session;
    }

    @Override
    public @Nullable BingoTeam getTeam() {
        return team;
    }

    @Override
    public void setTeam(@Nullable BingoTeam team) {
        this.team = team;

		sessionPlayer().ifPresent(p -> p.setWaypointColor(team == null ? null : team.getColor()));
    }

    @Override
    public String toString() {
        return playerName;
    }

    @Override
    public @NotNull Audience audience() {
        return sessionPlayer().isPresent() ? sessionPlayer().get() : Audience.empty();
    }

    public ServerSoftware server() {
        return server;
    }
}
