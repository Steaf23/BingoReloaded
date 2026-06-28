package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.tasks.data.TaskData;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BingoCardData {

	private static final ItemType DEFAULT_ITEM = ItemType.of("minecraft:dirt");

	private final TaskListData listsData = new TaskListData();
	private final TaskTagData tagData = new TaskTagData();
	public static final byte MAX_ITEMS = 36;
	public static final byte MIN_ITEMS = 1;

	private final DataAccessor defaultData = BingoReloaded.getDataAccessor("data/default_cards");
	private final DataAccessor data = BingoReloaded.getDataAccessor("data/cards");

	public boolean removeCard(String cardName) {
		if (!data.contains(cardName))
			return false;

		data.erase(cardName);
		data.saveChanges();
		return true;
	}

	public boolean duplicateCard(String cardName) {
		DataStorage card = getCard(cardName);
		if (card.isEmpty()) {
			return false;
		}

		data.setStorage(cardName + "_copy", card.duplicate());
		data.saveChanges();
		return true;
	}

	public boolean renameCard(String cardName, String newName) {
		if (isDefaultCard(cardName) || isDefaultCard(newName))
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
		Set<String> names = new HashSet<>(defaultData.getKeys());
		names.addAll(data.getKeys());
		return names;
	}

	public byte getListMax(String cardName, String listName) {
		return getCardLists(cardName).getByte(listName + ".max", MAX_ITEMS);
	}

	public byte getListMin(String cardName, String listName) {
		return getCardLists(cardName).getByte(listName + ".min", MIN_ITEMS);
	}

	public void setList(String cardName, String listName, int max, int min) {
		if (isDefaultCard(cardName)) {
			return;
		}

		data.setByte(cardName + ".lists." + listName + ".max", (byte) Math.min(max, MAX_ITEMS));
		data.setByte(cardName + ".lists." + listName + ".min", (byte) Math.max(min, MIN_ITEMS));
		data.saveChanges();
	}

	public void removeList(String cardName, String listName) {
		if (isDefaultCard(cardName)) {
			return;
		}

		data.erase(cardName + ".lists." + listName);
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
		return getCardLists(cardName).getKeys();
	}

	public List<String> getListsSortedByMin(String cardName) {
		List<String> result = new ArrayList<>(getListNames(cardName));
		result.sort((a, b) -> Integer.compare(getListMin(cardName, a), getListMin(cardName, b)));
		return result;
	}

	public List<String> getListsSortedByMax(String cardName) {
		List<String> result = new ArrayList<>(getListNames(cardName));
		result.sort((a, b) -> Integer.compare(getListMax(cardName, a), getListMax(cardName, b)));
		return result;
	}

	public String getDescription(String cardName) {
		return getCard(cardName).getString("description", "");
	}

	public void setDescription(String cardName, String description) {
		if (isDefaultCard(cardName)) {
			return;
		}
		data.setString(cardName + ".description", description);
		data.saveChanges();
	}

	public boolean isDefaultCard(String cardName) {
		return defaultData.getKeys().contains(cardName);
	}

	public TaskListData lists() {
		return listsData;
	}

	public TaskTagData tags() {
		return tagData;
	}

	public DataStorage getCard(String cardName) {
		if (defaultData.contains(cardName)) {
			return defaultData.getStorageOrEmpty(cardName);
		}
		else {
			return data.getStorageOrEmpty(cardName);
		}
	}

	public DataStorage getCardLists(String cardName) {
		return getCard(cardName).getStorageOrEmpty("lists");
	}
}
