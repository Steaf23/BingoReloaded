package io.github.steaf23.bingoreloaded.lib.api;


import io.github.steaf23.bingoreloaded.lib.inventory.Menu;
import org.bukkit.plugin.java.JavaPlugin;

public interface MenuBoard {

	JavaPlugin plugin();

	void open(Menu menu, PlayerHandle player);

	void close(Menu menu, PlayerHandle player);

	void closeAll(PlayerHandle player);
}
