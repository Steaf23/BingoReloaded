package io.github.steaf23.bingoreloadedcompanion.client.creator.tasklist.selection;

import io.github.steaf23.bingoreloaded.protocol.data.task.TaskId;
import io.github.steaf23.bingoreloadedcompanion.card.taskdata.TaskWithCount;
import io.github.steaf23.bingoreloadedcompanion.client.creator.CreatorSuite;

import java.util.HashMap;
import java.util.Set;
import java.util.function.Consumer;

public class TaskSelection extends HashMap<TaskId, TaskWithCount> {

	public final Consumer<TaskId> selectedTaskUpdated;

	public TaskSelection(Consumer<TaskId> selectedTaskUpdated) {
		this.selectedTaskUpdated = selectedTaskUpdated;
	}

	/**
	 * @return True when this task is contained by the selection after incrementing/ decrementing.
	 */
	public boolean updateForTask(CreatorSuite suite, TaskId taskId, boolean increment) {
		if (increment) {
			CreatorSuite.NamedTask namedTask = suite.getTaskById(taskId);
			if (!containsKey(taskId)) {
				putNewTask(namedTask, 1);
				selectedTaskUpdated.accept(taskId);
			} else {
				TaskWithCount task = get(taskId);
				if (task.count() < namedTask.def().maxCount()) {
					put(taskId, task.copy(task.count() + 1));
					selectedTaskUpdated.accept(taskId);
				}
			}
		} else {
			if (!containsKey(taskId)) {
				return false;
			}

			TaskWithCount task = get(taskId);
			if (task.count() <= 1) {
				remove(taskId);
				selectedTaskUpdated.accept(taskId);
			} else {
				put(taskId, task.copy(task.count() - 1));
				selectedTaskUpdated.accept(taskId);
			}
		}

		return containsKey(taskId);
	}

	public void putNewTask(CreatorSuite.NamedTask namedTask, int count) {
		put(namedTask.def().id(), new TaskWithCount(namedTask.def(), Math.min(count, namedTask.def().maxCount()), namedTask.name(), Set.of()));
	}

	public void updateSelected(TaskWithCount task) {
		put(task.task().id(), task);
	}
}
