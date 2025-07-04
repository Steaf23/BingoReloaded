package io.github.steaf23.bingoreloaded.lib.api;

public interface InventoryHandle extends Iterable<StackHandle> {

	void setItem(int index, StackHandle stack);
	void addItem(StackHandle stack);

	StackHandle[] contents();
	void clearContents();
	void setContents(StackHandle[] contents);
}
