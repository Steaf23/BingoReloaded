package io.github.steaf23.bingoreloaded.lib.api;

public interface InventoryHandle {

	void setItem(int index, StackHandle stack);
	void addItem(StackHandle... stacks);

	StackHandle[] contents();
	void clearContents();
	void setContents(StackHandle[] contents);
}
