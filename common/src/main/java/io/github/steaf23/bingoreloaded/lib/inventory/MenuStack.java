package io.github.steaf23.bingoreloaded.lib.inventory;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;

public class MenuStack {

	public record PushResult(@Nullable Menu menuToClose, boolean openMenu) {}

	private final Deque<Menu> menus = new ArrayDeque<>();

	public PushResult push(Menu menu) {
		Menu menuToClose = null;
		// If we add another menu on top of a menu that should be removed, remove this menu first.
		if (!menus.isEmpty() && menus.peek().openOnce()) {
			menuToClose = menus.pop();
		}
		// If the new menu is not already in the stack, push it to the top.
		if (!menus.contains(menu)) {
			menus.push(menu);
		}

		// This menu is somewhere in the middle of the menu stack, don't open it.
		boolean openNewMenu = menus.peek() == menu;

		return new PushResult(menuToClose, openNewMenu);
	}

	public Menu pop(Menu expectedTop) {
		if (menus.peek() != expectedTop) {
			throw new IllegalStateException("Menu to close is not on top of the menu stack.");
		}

		menus.pop();
		if (menus.isEmpty()) {
			return null;
		}

		return menus.peek();
	}

	public void popAll(Consumer<Menu> whenPopped) {
		Menu menu = menus.pop();
		while (menu != null) {
			whenPopped.accept(menu);
			menu = menus.pop();
		}
	}

	public @Nullable Menu peek() {
		return menus.peek();
	}
}
