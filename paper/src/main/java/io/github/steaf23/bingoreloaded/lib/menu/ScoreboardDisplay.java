package io.github.steaf23.bingoreloaded.lib.menu;

import io.github.steaf23.bingoreloaded.data.ScoreboardData;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.player.SharedDisplay;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Adapt a scoreboard (mainly sidebar) to a shared display for displaying plugin information in paper.
 */
public class ScoreboardDisplay implements SharedDisplay {

	private final String templateKey;
	protected InfoMenu data;
	protected SidebarHUD sidebar = new SidebarHUD(Component.text("Test Title"));

	Set<UUID> players = new HashSet<>();

	public ScoreboardDisplay(String templateKey) {
		this.templateKey = templateKey;
	}

	@Override
	public void update(InfoMenu info) {
		this.data = info;

		ScoreboardData.SidebarTemplate template = new ScoreboardData().loadTemplate(templateKey, data.registeredFields);

		for (UUID id: players) {
			Player bukkitPlayer = Bukkit.getPlayer(id);
			if (bukkitPlayer == null) {
				continue;
			}
			this.sidebar.clear();

			PlayerHandle player = new PlayerHandlePaper(bukkitPlayer);
			this.sidebar.setTitle(SidebarTemplater.title(template, player));

			List<Component> lines = SidebarTemplater.sidebarComponents(template, player);
			for (int i = 0; i < Math.min(15, lines.size()); i++) {
				this.sidebar.setText(i, lines.get(i));
			}
			this.sidebar.applyToPlayer(bukkitPlayer);
		}

	}

	@Override
	public void addPlayer(PlayerHandle player) {
		players.add(player.uniqueId());

		update(data);
	}

	@Override
	public void removePlayer(PlayerHandle player) {
		this.sidebar.removeFromPlayer(((PlayerHandlePaper)player).handle());
		players.remove(player.uniqueId());
	}

	@Override
	public void clearPlayers() {
		players.clear();
		this.sidebar.removeAll();
	}
}
