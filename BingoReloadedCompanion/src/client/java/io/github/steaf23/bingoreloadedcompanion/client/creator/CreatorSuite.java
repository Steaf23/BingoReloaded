package io.github.steaf23.bingoreloadedcompanion.client.creator;

import io.github.steaf23.bingoreloaded.protocol.data.CreatorContext;
import io.github.steaf23.bingoreloaded.protocol.data.CreatorTaskSupplier;
import io.github.steaf23.bingoreloaded.protocol.data.card.CustomCard;
import io.github.steaf23.bingoreloaded.protocol.data.card.CustomList;
import io.github.steaf23.bingoreloaded.protocol.data.task.StatisticCategory;
import io.github.steaf23.bingoreloaded.protocol.data.task.TaskDefinition;
import io.github.steaf23.bingoreloaded.protocol.data.task.TaskFormatting;
import io.github.steaf23.bingoreloaded.protocol.data.task.TaskId;
import io.github.steaf23.bingoreloadedcompanion.card.taskdata.TaskWithCount;
import io.github.steaf23.bingoreloadedcompanion.client.BingoReloadedCompanionClient;
import io.github.steaf23.bingoreloadedcompanion.client.util.FabricTypes;
import io.github.steaf23.bingoreloadedcompanion.network.ClientGetCreatorListPayload;
import io.github.steaf23.bingoreloadedcompanion.network.ClientUpsertCreatorCardPayload;
import io.github.steaf23.bingoreloadedcompanion.network.ClientUpsertCreatorListPayload;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.object.ObjectContents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CreatorSuite {

	public record TaskWithName(TaskDefinition def, TaskWithCount.NameSupplier name) {}

	public final Map<TaskId, TaskWithName> allStatistics = new HashMap<>();
	public final Map<TaskId, TaskWithName> allItems = new HashMap<>();
	public final Map<TaskId, TaskWithName> allAdvancements = new HashMap<>();
	public final List<TaskId> itemOrder = new ArrayList<>();
	public final List<TaskId> advancementOrder = new ArrayList<>();
	public final List<TaskId> statisticOrder = new ArrayList<>();
	public final Map<CreativeModeTab, List<TaskId>> itemsPerTab = new HashMap<>();

	private final List<ResourceKey<CreativeModeTab>> ORDERED_TABS = List.of(
			CreativeModeTabs.BUILDING_BLOCKS,
			CreativeModeTabs.COLORED_BLOCKS,
			CreativeModeTabs.NATURAL_BLOCKS,
			CreativeModeTabs.FUNCTIONAL_BLOCKS,
			CreativeModeTabs.REDSTONE_BLOCKS,
			CreativeModeTabs.TOOLS_AND_UTILITIES,
			CreativeModeTabs.COMBAT,
			CreativeModeTabs.FOOD_AND_DRINKS,
			CreativeModeTabs.INGREDIENTS,
			CreativeModeTabs.SPAWN_EGGS,
			CreativeModeTabs.OP_BLOCKS
	);

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

		ORDERED_TABS.stream().map(BuiltInRegistries.CREATIVE_MODE_TAB::getValueOrThrow)
				.filter(t -> t.getType() != CreativeModeTab.Type.SEARCH)
				.forEach(tab -> {

			tab.buildContents(new CreativeModeTab.ItemDisplayParameters(
					Minecraft.getInstance().player.connection.enabledFeatures(),
					Minecraft.getInstance().player.canUseGameMasterBlocks(),
					Minecraft.getInstance().player.level().registryAccess()));
			List<TaskId> items = new ArrayList<>();

			tab.getDisplayItems().stream().sorted(Comparator.comparing(t -> t.getItem().getDescriptionId())).forEach(stack -> {
				Identifier itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
				Key key = Key.key(itemId.getNamespace(), itemId.value());
				TaskId id = new TaskId.Item(key);
				for (TaskId item : items) {
					if (((TaskId.Item)item).id().equals(key)) {
						return;
					}
				}
				items.add(id);
				allItems.put(id, itemTask(key, id, tab.getDisplayName().getString()));
			});

			itemsPerTab.put(tab, items);
		});

		itemOrder.addAll(allItems.keySet().stream().map(i -> (TaskId.Item)i).sorted(Comparator.comparing(TaskId.Item::id)).toList());

		taskSupplier().statistics().stream()
				.flatMap(task -> {
					TaskId.Statistic stat = task.statistic();
					return switch (task.statistic().category().type) {
						case CUSTOM -> Stream.of(new TaskDefinition(
								stat,
								task.name(),
								task.customIcon(),
								stat.category().name(),
								64));
						case ITEM -> taskSupplier().items().stream()
								.map(item -> itemTaskFromStatistic(stat.category(), item, stat.category().name(), stat.statisticType()));
						case BLOCK -> taskSupplier().validBlocks().stream()
								.map(item -> itemTaskFromStatistic(stat.category(), item, stat.category().name(), stat.statisticType()));
						case ENTITY -> taskSupplier().validEntityTypes().stream()
								.map(item -> entityTaskFromStatistic(stat.category(), item, stat.category().name(), stat.statisticType()));
					};
				}).forEach(s -> allStatistics.put(s.id(), new TaskWithName(s, t -> FabricTypes.toNativeComponent(createStatisticName(t)))));

		taskSupplier().advancements().forEach((key, node) -> {
			if (!node.hasDisplay()) {
				return;
			}
			String category = findAdvancementRoot(key);
			TaskDefinition def = new TaskDefinition(new TaskId.Advancement(key), node.displayName(), node.displayIcon(), category,1);
			Component comp = FabricTypes.toNativeComponent(taskFormatting().advancementNameComponent(net.kyori.adventure.text.Component.text(node.displayName())));
			allAdvancements.put(def.id(), new TaskWithName(def, _ -> comp));
		});
	}

	public net.kyori.adventure.text.Component createStatisticName(TaskWithCount task) {
		int count = task.count();
		TaskFormatting formatting = taskFormatting();
		TaskId.Statistic stat = (TaskId.Statistic)task.task().id();
		return switch (stat.category()) {
			case ENTITY_KILLED -> formatting.statisticKillEntityComponent(
					(with) -> statisticComponent(stat, with),
					createEntityName(stat.statisticSpecification()),
					count);
			case KILLED_BY -> formatting.statisticKilledByEntityComponent(
					(with) -> statisticComponent(stat, with),
					createEntityName(stat.statisticSpecification()),
					count);
			case TRAVEL -> formatting.statisticNameComponent(
					statisticComponent(stat),
					count,
					net.kyori.adventure.text.Component.text(" ").append(net.kyori.adventure.text.Component.translatable("soundCategory.block")),
					10);
			case DAMAGE -> formatting.statisticNameComponent(
					statisticComponent(stat),
					count,
					net.kyori.adventure.text.Component.object(ObjectContents.sprite(Key.key("gui"), Key.key("hud/heart/full"))), 1);
			default -> switch (stat.category().type) {
				case CUSTOM -> formatting.statisticNameComponent(
						statisticComponent(stat),
						count,
						net.kyori.adventure.text.Component.empty(),
						1);
				case ITEM, BLOCK -> formatting.statisticItemComponent(
						statisticComponent(stat),
						createItemName(stat.statisticSpecification()),
						count);
				case ENTITY -> throw new IllegalStateException("This code should not be reachable, please contact the developer!");
			};
		};
	}

	public net.kyori.adventure.text.Component statisticComponent(TaskId.Statistic stat, net.kyori.adventure.text.Component... args) {
		String prefix;
		String result;
		if (stat.category().type == StatisticCategory.Type.CUSTOM) {
			prefix = "stat.minecraft.";
			result = stat.statisticSpecification().value();
		} else {
			prefix = "stat_type.minecraft.";
			result = stat.statisticType().value();
		}

		String full = !result.isEmpty() ? prefix + result : stat.statisticType().key().asString();
		return net.kyori.adventure.text.Component.translatable(full, args);
	}

	public net.kyori.adventure.text.Component createEntityName(Key key) {
		return net.kyori.adventure.text.Component.translatable(BuiltInRegistries.ENTITY_TYPE.getValue(FabricTypes.idFromKey(key)).getDescriptionId());
	}

	public TaskWithName getTaskById(TaskId id) {
		return switch (id.type()) {
			case ITEM -> allItems.get(id);
			case ADVANCEMENT -> allAdvancements.get(id);
			case STATISTIC -> allStatistics.get(id);
		};
	}

	public CreatorTaskSupplier taskSupplier() {
		return context.taskSupplier();
	}

	public io.github.steaf23.bingoreloaded.protocol.data.task.TaskFormatting taskFormatting() {
		return context.formatting();
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

	public TaskWithName itemTask(Key key, String category) {
		return itemTask(key, new TaskId.Item(key), category);
	}

	public TaskWithName itemTask(Key key, TaskId id, String category) {
		Item item = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(key.namespace(), key.value()));
		net.kyori.adventure.text.Component name = createItemName(key);
		return new TaskWithName(new TaskDefinition(
				id,
				"x1 " + item.getDescriptionId(),
				key,
				category,
				64), task -> FabricTypes.toNativeComponent(taskFormatting().itemNameComponent(name, task.count())));
	}

	public net.kyori.adventure.text.Component createItemName(Key item) {
		return net.kyori.adventure.text.Component.translatable(BuiltInRegistries.ITEM.getValue(FabricTypes.idFromKey(item)).getDescriptionId());
	}

	public TaskDefinition itemTaskFromStatistic(StatisticCategory statCategory, Key itemKey, String category, Key statType) {
		Item item = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(itemKey.namespace(), itemKey.value()));
		return new TaskDefinition(
				new TaskId.Statistic(statType, itemKey, statCategory),
				"STAT: " + item.getDescriptionId(),
				itemKey,
				category,
				64
		);
	}

	public TaskDefinition entityTaskFromStatistic(StatisticCategory statCategory, Key entityKey, String category, Key statType) {
		EntityType<?> entity = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.fromNamespaceAndPath(entityKey.namespace(), entityKey.value()));
		return new TaskDefinition(
				new TaskId.Statistic(statType, entityKey, statCategory),
				"STAT: " + entity.getDescriptionId(),
				Key.key(entityKey.namespace(), entityKey.value() + "_spawn_egg"),
				category,
				64
		);
	}

	public @Nullable String findAdvancementRoot(Key advancement) {
		return advancement.asString().split("/")[0];
	}
}
