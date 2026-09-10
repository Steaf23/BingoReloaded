package io.github.steaf23.bingoreloadedcompanion.client.creator;

import io.github.steaf23.bingoreloaded.protocol.data.CreatorContext;
import io.github.steaf23.bingoreloaded.protocol.data.CreatorTaskSupplier;
import io.github.steaf23.bingoreloaded.protocol.data.card.CustomCard;
import io.github.steaf23.bingoreloaded.protocol.data.card.CustomList;
import io.github.steaf23.bingoreloaded.protocol.data.task.StatisticCategory;
import io.github.steaf23.bingoreloaded.protocol.data.task.TaskDefinition;
import io.github.steaf23.bingoreloaded.protocol.data.task.TaskId;
import io.github.steaf23.bingoreloadedcompanion.client.BingoReloadedCompanionClient;
import io.github.steaf23.bingoreloadedcompanion.network.ClientGetCreatorListPayload;
import io.github.steaf23.bingoreloadedcompanion.network.ClientUpsertCreatorCardPayload;
import io.github.steaf23.bingoreloadedcompanion.network.ClientUpsertCreatorListPayload;
import net.kyori.adventure.key.Key;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CreatorSuite {

	public final Map<TaskId, TaskDefinition> allStatistics = new HashMap<>();
	public final Map<TaskId, TaskDefinition> allItems = new HashMap<>();
	public final Map<TaskId, TaskDefinition> allAdvancements = new HashMap<>();
	public final Map<CreativeModeTab, Set<TaskId>> itemsPerTab = new HashMap<>();

	private CreatorContext context = null;
	private CreatorCardScreen cardScreen = null;
	private CreatorTaskScreen taskScreen = null;

	private Screen openScreen;

	public void openListEditor(@Nullable CreatorContext context) {
		if (context != null) {
			this.context = context;
			extractContext();
		}

		this.taskScreen = new CreatorTaskScreen(this);
		openScreen = Minecraft.getInstance().gui.screen();
		Minecraft.getInstance().setScreenAndShow(taskScreen);
	}

	public void openCardEditor(@NotNull CreatorContext context) {
		this.context = context;

		extractContext();

		this.cardScreen = new CreatorCardScreen(this);
		openScreen = Minecraft.getInstance().gui.screen();
		Minecraft.getInstance().setScreenAndShow(cardScreen);
	}

	public void extractContext() {
		allItems.clear();
		allStatistics.clear();
		allAdvancements.clear();
		itemsPerTab.clear();

		BuiltInRegistries.CREATIVE_MODE_TAB.stream().forEach(tab -> {
			if (tab.getType() == CreativeModeTab.Type.SEARCH) {
				return;
			}
			tab.buildContents(new CreativeModeTab.ItemDisplayParameters(
					Minecraft.getInstance().player.connection.enabledFeatures(),
					Minecraft.getInstance().player.canUseGameMasterBlocks(),
					Minecraft.getInstance().player.level().registryAccess()));
			Set<TaskId> items = tab.getDisplayItems().stream()
					.map(stack -> {
						Identifier itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
						TaskDefinition def = itemTask(Key.key(itemId.getNamespace(), itemId.value()), tab.getDisplayName().getString());
						allItems.put(def.id(), def);
						return def;
					})
					.map(TaskDefinition::id)
					.collect(Collectors.toSet());

			itemsPerTab.put(tab, items);
		});

		taskSupplier().statistics().stream()
				.flatMap(task -> {
					TaskId.Statistic stat = task.statistic();
					return switch (task.statistic().category().type) {
						case CUSTOM -> Stream.of(new TaskDefinition(
								stat,
								task.name(),
								"",
								task.customIcon(),
								stat.category().name(),
								64));
						case ITEM -> taskSupplier().items().stream()
								.map(item -> itemTaskFromStatistic(stat.category(), item, stat.category().name()));
						case BLOCK -> taskSupplier().validBlocks().stream()
								.map(item -> itemTaskFromStatistic(stat.category(), item, stat.category().name()));
						case ENTITY -> taskSupplier().validEntityTypes().stream()
								.map(item -> entityTaskFromStatistic(stat.category(), item, stat.category().name()));
					};
				}).forEach(s -> allStatistics.put(s.id(), s));

		taskSupplier().advancements().forEach((key, node) -> {
			if (!node.hasDisplay()) {
				return;
			}
			String category = findAdvancementRoot(key);
			TaskDefinition def = new TaskDefinition(new TaskId.Advancement(key), node.displayName(), node.displayDescription(), node.displayIcon(), category,1);
			allAdvancements.put(def.id(), def);
		});
	}

	public TaskDefinition getTaskById(TaskId id) {
		return switch (id.type()) {
			case ITEM -> allItems.get(id);
			case ADVANCEMENT -> allAdvancements.get(id);
			case STATISTIC -> allStatistics.get(id);
		};
	}

	public CreatorTaskSupplier taskSupplier() {
		return context.taskSupplier();
	}

	public List<CustomCard> cards() {
		return context.cards();
	}

	public void closeScreen(Screen screen) {
		if (openScreen == null) {
			Minecraft.getInstance().gui.setScreen(null);
		}
		Minecraft.getInstance().setScreenAndShow(openScreen);
		openScreen = null;
	}

	public void openList(String listName) {
		BingoReloadedCompanionClient.sendPayloadToServer(new ClientGetCreatorListPayload(listName));
	}

	public void saveCard(CustomCard card) {
		BingoReloadedCompanionClient.sendPayloadToServer(new ClientUpsertCreatorCardPayload(card));
	}

	public void saveList(CustomList list) {
		BingoReloadedCompanionClient.sendPayloadToServer(new ClientUpsertCreatorListPayload(list));
	}

	public void listReceived(CustomList list) {
		if (taskScreen != null) {
			taskScreen.loadList(list);
		}
	}

	public TaskDefinition itemTask(Key key, String category) {
		Item item = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(key.namespace(), key.value()));
		return new TaskDefinition(
				new TaskId.Item(key),
				"x1 " + item.getDescriptionId(),
				"item description",
				key,
				category,
				64);
	}


	public TaskDefinition itemTaskFromStatistic(StatisticCategory statCategory, Key itemKey, String category) {
		Item item = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(itemKey.namespace(), itemKey.value()));
		return new TaskDefinition(
				new TaskId.Statistic(Key.key("custom"), itemKey, statCategory),
				"STAT: " + item.getDescriptionId(),
				"stat description",
				itemKey,
				category,
				64
		);
	}

	public TaskDefinition entityTaskFromStatistic(StatisticCategory statCategory, Key entityKey, String category) {
		EntityType<?> entity = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.fromNamespaceAndPath(entityKey.namespace(), entityKey.value()));
		return new TaskDefinition(
				new TaskId.Statistic(Key.key("custom"), entityKey, statCategory),
				"STAT: " + entity.getDescriptionId(),
				"stat description",
				Key.key(entityKey.namespace(), entityKey.value() + "_spawn_egg"),
				category,
				64
		);
	}

	public @Nullable String findAdvancementRoot(Key advancement) {
		return advancement.asString().split("/")[0];
	}
}
