package io.github.steaf23.bingoreloaded.data.teleportgrid;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.lib.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagDataType;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class TeleportGridData {

	private final DataAccessor data = BingoReloaded.getDataAccessor("data/grid");
	private final Random random;

	private final TeleportationGrid gridOptions;

	public TeleportGridData(TeleportationGrid gridOptions, Random random) {
		this.gridOptions = gridOptions;
		this.random = random;
	}

	public boolean isDone() {
		int cells = getUsedCells().size();
		return cells == gridOptions.size().x() * gridOptions.size().z();
	}

	public TeleportationGrid.Point createNextStart() throws IllegalStateException {
		Set<Integer> usedCells = getUsedCells();
		int cell = randomUnpickedValue(usedCells, gridOptions.size().x() * gridOptions.size().z());

		if (cell == -1) {
			throw new IllegalStateException("I Should not be called when out of cells to pick!");
		}

		int cellX = cell % gridOptions.size().x();
		int cellZ = cell / gridOptions.size().z();
		int xStart = cellX * gridOptions.cellSize().x() + gridOptions.cellSize().x() / 2 + gridOptions.center().x();
		int zStart = cellZ * gridOptions.cellSize().z() + gridOptions.cellSize().z() / 2 + gridOptions.center().z();

		usedCells.add(cell);
		data.setList("used_cells", TagDataType.INT, usedCells.stream().toList());

		data.saveChanges();

		return new TeleportationGrid.Point(xStart, zStart);
	}

	public void reset() {
		data.setList("used_cells", TagDataType.INT, List.of());
		data.saveChanges();
	}

	public int getGamesLeft() {
		return gridOptions.size().x() * gridOptions.size().z() - getUsedCells().size();
	}

	public TeleportationGrid getGridOptions() {
		return gridOptions;
	}

	private Set<Integer> getUsedCells() {
		return new HashSet<>(data.getList("used_cells", TagDataType.INT));
	}

	private int randomUnpickedValue(Set<Integer> pickedValues, int totalValues) throws IllegalArgumentException {
		if (pickedValues.size() >= totalValues) {
			throw new IllegalArgumentException("The amount of picked values must be smaller than totalValues");
		}

		int offset = random.nextInt(totalValues);
		int i = 0;
		int value = (i + offset) % totalValues;
		while (pickedValues.contains(value)) {
			value = (i + offset) % totalValues;
			i++;
		}

		return value;
	}

}
