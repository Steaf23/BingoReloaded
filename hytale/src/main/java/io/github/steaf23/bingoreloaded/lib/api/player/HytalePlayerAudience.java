package io.github.steaf23.bingoreloaded.lib.api.player;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.steaf23.bingoreloaded.adapter.MessageHelper;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

public class HytalePlayerAudience implements Audience {

	PlayerRef playerRef;

	HytalePlayerAudience(PlayerRef playerRef) {
		this.playerRef = playerRef;
	}

	@Override
	public void sendMessage(@NotNull ComponentLike message) {
		playerFromRef().sendMessage(MessageHelper.fromComponentLike(message));
	}

	@Override
	public void sendMessage(@NotNull Component message) {
		playerFromRef().sendMessage(MessageHelper.fromComponentLike(message));
	}

	Player playerFromRef() {
		return playerRef.getReference().getStore().getComponent(playerRef.getReference(), Player.getComponentType());
	}
}
