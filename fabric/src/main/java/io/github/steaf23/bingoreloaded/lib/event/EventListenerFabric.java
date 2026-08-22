package io.github.steaf23.bingoreloaded.lib.event;

import io.github.steaf23.bingoreloaded.lib.api.AdvancementHandleFabric;
import io.github.steaf23.bingoreloaded.lib.api.EntityTypeFabric;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypeFabric;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandleFabric;
import io.github.steaf23.bingoreloaded.lib.api.platform.FabricServer;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandleFabric;
import io.github.steaf23.bingoreloaded.lib.api.statistics.StatisticHandle;
import io.github.steaf23.bingoreloaded.lib.api.statistics.VanillaStatistic;
import io.github.steaf23.bingoreloaded.lib.api.statistics.VanillaStatistics;
import io.github.steaf23.bingoreloaded.util.FabricTypes;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class EventListenerFabric {

	private final FabricServer server;
	private final PlatformEventDispatcher dispatcher;

	public EventListenerFabric(FabricServer server, PlatformEventDispatcher dispatcher) {
		this.server = server;
		this.dispatcher = dispatcher;

		// Existing fabric api events
		ServerPlayerEvents.JOIN.register(this::playerJoin);
		ServerPlayerEvents.LEAVE.register(this::playerLeave);
		UseItemCallback.EVENT.register(this::playerUseItem);
		ServerPlayerEvents.AFTER_RESPAWN.register(this::playerRespawn);
		ServerLivingEntityEvents.ALLOW_DAMAGE.register(this::allowEntityDamage);
		PlayerBlockBreakEvents.BEFORE.register(this::playerBreaksBlock);

		// Custom added events
		PlayerDroppedItem.EVENT.register(this::playerDroppedItem);
		PlayerStatIncrement.EVENT.register(this::playerIncrementStat);
		PlayerAdvancementCompleted.EVENT.register(this::playerAdvancement);
		//TODO: mixin ItemStack.hurtAndBreak for DamageItemEvent
	}

	private void playerJoin(ServerPlayer player) {
		dispatcher.sendPlayerJoinsServer(new PlayerHandleFabric(server, player));
	}

	private void playerLeave(ServerPlayer player) {
		dispatcher.sendPlayerQuitsServer(new PlayerHandleFabric(server, player));
	}

	private void playerRespawn(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
		dispatcher.sendPlayerRespawn(new PlayerHandleFabric(server, newPlayer), false, false);
	}

	private InteractionResult playerDroppedItem(ServerPlayer player, ItemStack stack) {
		StackHandleFabric handle = new StackHandleFabric(stack);
		EventResult<?> result = dispatcher.sendPlayerDroppedStack(new PlayerHandleFabric(server, player), handle);

		if (stack.isEmpty()) {
			return InteractionResult.CONSUME;
		}

		if (result.consume()) {
			return InteractionResult.FAIL;
		}

		return InteractionResult.SUCCESS;
	}

	private InteractionResult playerUseItem(Player player, Level level, InteractionHand hand) {
		EventResult<?> result = dispatcher.sendPlayerUseItem(
				new PlayerHandleFabric(server, (ServerPlayer)player),
				new StackHandleFabric(player.getItemInHand(hand)));
		return result.consume() ? InteractionResult.FAIL : InteractionResult.PASS;
	}

	private boolean allowEntityDamage(LivingEntity entity, DamageSource source, float damage) {
		if (!entity.is(EntityTypes.PLAYER)) {
			return true;
		}

		ServerPlayer player = (ServerPlayer) entity;
		if (source.is(DamageTypes.FALL)) {
			EventResult<?> result = dispatcher.sendPlayerFallDamage(new PlayerHandleFabric(server, player));
			return !result.consume();
		}
		return true;
	}

	private boolean playerBreaksBlock(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
		EventResult<?> result = dispatcher.sendPlayerPlacesBlock(
				new PlayerHandleFabric(server, (ServerPlayer) player),
				FabricTypes.fromBlockPos(FabricTypes.keyFromId(level.dimension().identifier()), pos),
				new ItemTypeFabric(state.getBlock().asItem()));

		return !result.consume();
	}

	private InteractionResult playerIncrementStat(ServerPlayer player, Stat<?> stat, int increment, int oldValue, int newValue) {
		Identifier statId = BuiltInRegistries.STAT_TYPE.getKey(stat.getType());

		if (stat.getType() == Stats.CUSTOM) {
			statId = (Identifier) stat.getValue();
		}
		VanillaStatistic statistic = VanillaStatistics.fromKey(FabricTypes.keyFromId(statId));

		if (statistic == null || statistic.getsUpdatedOften()) {
			return InteractionResult.PASS;
		}

   		StatisticHandle handle = switch (statistic.specification()) {
			case NONE -> new StatisticHandle(statistic);
			case ITEM -> {
				Item item;
				if (stat.getValue() instanceof Block block) {
					item = block.asItem();
				} else {
					item = (Item) stat.getValue();
				}
				yield new StatisticHandle(statistic, new ItemTypeFabric(item));
			}
			case ENTITY -> new StatisticHandle(statistic, new EntityTypeFabric((EntityType<?>) stat.getValue()));
		};

		EventResult<?> result = dispatcher.sendPlayerStatisticIncrement(new PlayerHandleFabric(server, player),
				handle, newValue);

		if (result.consume()) {
			return InteractionResult.FAIL;
		}

		return InteractionResult.PASS;
	}

	private void playerAdvancement(ServerPlayer player, AdvancementHolder advancement, String lastCriterion) {
		dispatcher.sendPlayerAdvancementDone(new PlayerHandleFabric(server, player), new AdvancementHandleFabric(advancement));
	}
}
