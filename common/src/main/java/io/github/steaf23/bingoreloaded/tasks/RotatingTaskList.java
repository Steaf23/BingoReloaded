package io.github.steaf23.bingoreloaded.tasks;

import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.tasks.data.TaskData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class RotatingTaskList {

	private final List<TaskData> randomTasks = new ArrayList<>();
	private final BingoCardData cardData = new BingoCardData();

	private final TaskGenerator.GeneratorSettings settings;
	private final Random seededRandom;

	public RotatingTaskList(TaskGenerator.GeneratorSettings settings, long seed) {
		this.settings = settings;
		this.seededRandom = new Random(seed);
	}

	public GameTask nextTask(Predicate<TaskData> addPrecondition) {
		if (randomTasks.isEmpty()) {
			randomTasks.addAll(cardData.getAllTasks(settings.cardName(), settings.includedTypes()));
			// Do not add the tasks that are currently on the card.
			// This will result in less duplicates overall when cycling through tasks.
			randomTasks.removeIf(addPrecondition);
			Collections.shuffle(randomTasks, seededRandom);
		}
		if (randomTasks.isEmpty()) {
			return GameTask.simpleItemTask(ItemType.of("dirt"), 1);
		}

		TaskData data = randomTasks.removeLast();
		return TaskGenerator.createTaskFromData(data);
	}
}
