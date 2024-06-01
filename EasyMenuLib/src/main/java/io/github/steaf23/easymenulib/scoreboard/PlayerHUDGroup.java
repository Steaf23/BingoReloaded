package io.github.steaf23.easymenulib.scoreboard;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PlayerHUDGroup
{
    protected final Map<String, String> registeredFields;
    private final List<PlayerHUD> huds;
    private final HUDRegistry registry;

    public PlayerHUDGroup(HUDRegistry registry) {
        this.huds = new ArrayList<>();
        this.registry = registry;
        this.registeredFields = new HashMap<>();
    }

    protected abstract PlayerHUD createHUDForPlayer(Player player);

    public void addPlayer(Player player) {
        PlayerHUD hud = createHUDForPlayer(player);
        // Don't re-add players if they are already added
        if (huds.stream().anyMatch(other -> hud.getPlayerId().equals(other.getPlayerId()))) {
            return;
        }
        registry.addPlayerHUD(hud);
        huds.add(hud);
    }

    public void removePlayer(Player player) {
        registry.removePlayerHUD(player.getUniqueId());
        huds.removeIf(h -> h.getPlayerId().equals(player.getUniqueId()));
    }

    public void removeAllPlayers() {
        for (PlayerHUD hud : huds) {
            registry.removePlayerHUD(hud.getPlayerId());
        }

        huds.clear();
    }

    public void updateVisible() {
        huds.forEach(PlayerHUD::update);
    }
}
