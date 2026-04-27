package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.tasks.data.TaskData;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BingoCardData {

	private static final ItemType DEFAULT_ITEM = ItemType.of("minecraft:dirt");

	public static final Set<String> DEFAULT_CARD_NAMES = Set.of(
			"default_card",
			"default_card_hardcore"
	);
	private final TaskListData listsData = new TaskListData();
	private final TaskTagData tagData = new TaskTagData();
	public static final byte MAX_ITEMS = 36;
	public static final byte MIN_ITEMS = 1;

	private final DataAccessor data = BingoReloaded.getDataAccessor("data/cards");

	public boolean removeCard(String cardName) {
		if (!data.contains(cardName))
			return false;

		if (DEFAULT_CARD_NAMES.contains(cardName)) {
			ConsoleMessenger.error("Cannot remove default card!");
			return false;
		}

		data.erase(cardName);
		data.saveChanges();
		return true;
	}

	public boolean duplicateCard(String cardName) {
		if (!data.contains(cardName))
			return false;

		DataStorage card = data.getStorage(cardName);
		data.setStorage(cardName + "_copy", card);
		data.saveChanges();
		return true;
	}

	public boolean renameCard(String cardName, String newName) {
		if (DEFAULT_CARD_NAMES.contains(cardName) || DEFAULT_CARD_NAMES.contains(newName))
			return false;
		if (!data.contains(cardName))
			return false;
		if (data.contains(newName)) // Card with newName already exists
			return false;

		DataStorage card = data.getStorage(cardName);
		data.setStorage(newName, card);
		data.erase(cardName);
		data.saveChanges();
		return true;
	}

	public Set<String> getCardNames() {
		return data.getKeys();
	}

	public byte getListMax(String cardName, String listName) {
		return data.getByte(cardName + "." + listName + ".max", MAX_ITEMS);
	}

	public byte getListMin(String cardName, String listName) {
		return data.getByte(cardName + "." + listName + ".min", MIN_ITEMS);
	}

	public void setList(String cardName, String listName, int max, int min) {
		data.setByte(cardName + "." + listName + ".max", (byte) Math.min(max, MAX_ITEMS));
		data.setByte(cardName + "." + listName + ".min", (byte) Math.max(min, MIN_ITEMS));
		data.saveChanges();
	}

	public void removeList(String cardName, String listName) {
		data.erase(cardName + "." + listName);
	}

	public List<TaskData> getAllTasks(String cardName) {
		return getAllTasks(cardName, EnumSet.allOf(TaskData.TaskType.class));
	}

	public List<TaskData> getAllTasks(String cardName, EnumSet<TaskData.TaskType> types) {
		List<TaskData> tasks = new ArrayList<>();
		getListNames(cardName).forEach((l) -> tasks.addAll(listsData.getTasks(l, types)));
		return tasks;
	}

	public Set<String> getListNames(String cardName) {
		if (!data.contains(cardName))
			return new HashSet<>();
		else {
			return data.getStorageOrEmpty(cardName).getKeys();
		}
	}

	public List<String> getListsSortedByMin(String cardName) {
		List<String> result = new ArrayList<>(data.getStorageOrEmpty(cardName).getKeys());
		result.sort((a, b) -> Integer.compare(getListMin(cardName, a), getListMin(cardName, b)));
		return result;
	}

	public List<String> getListsSortedByMax(String cardName) {
		List<String> result = new ArrayList<>(data.getStorageOrEmpty(cardName).getKeys());
		result.sort((a, b) -> Integer.compare(getListMax(cardName, a), getListMax(cardName, b)));
		return result;
	}

	public TaskListData lists() {
		return listsData;
	}

	public TaskTagData tags() {
		return tagData;
	}
}
