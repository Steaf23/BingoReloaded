package io.github.steaf23.bingoreloaded.lib.api;


import io.github.steaf23.bingoreloaded.gui.inventory.core.Menu;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import org.bukkit.plugin.java.JavaPlugin;

public interface MenuBoard {

	JavaPlugin plugin();

	void open(Menu menu, PlayerHandle player);

	void close(Menu menu, PlayerHandle player);

	void closeAll(PlayerHandle player);
}
