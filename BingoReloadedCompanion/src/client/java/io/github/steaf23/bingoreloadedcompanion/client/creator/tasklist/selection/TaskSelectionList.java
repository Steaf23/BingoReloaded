package io.github.steaf23.bingoreloadedcompanion.client.creator.tasklist.selection;

import net.minecraft.client.gui.layouts.LinearLayout;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TaskSelectionList extends LinearLayout implements Iterable<SelectedTaskComponent> {

	public List<SelectedTaskComponent> selectedTasks = new ArrayList<>();

	public TaskSelectionList() {
		super(0, 0, Orientation.VERTICAL);
	}

	public void addComp(SelectedTaskComponent child) {
		selectedTasks.add(child);
		addChild(child);
	}

	public void clear() {
		removeChildren();
		selectedTasks.clear();
	}

	@Override
	public @NonNull Iterator<SelectedTaskComponent> iterator() {
		return selectedTasks.iterator();
	}
}
