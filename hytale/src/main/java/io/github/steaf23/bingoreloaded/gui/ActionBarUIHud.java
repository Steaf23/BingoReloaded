package io.github.steaf23.bingoreloaded.gui;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.jetbrains.annotations.NotNull;

public class ActionBarUIHud extends CustomUIHud {

	Message text;

	public ActionBarUIHud(@NotNull PlayerRef playerRef, Message text) {
		super(playerRef);

		this.text = text;
	}

	@Override
	protected void build(@NotNull UICommandBuilder uiCommandBuilder) {
		uiCommandBuilder.append("action_bar.ui");
		uiCommandBuilder.set("#ActionBarLbl.TextSpans", text);
	}

	public static void sendMessage(PlayerRef toPlayer, Message msg) {
		Player player = toPlayer.getReference().getStore().getComponent(toPlayer.getReference(), Player.getComponentType());
		player.getHudManager().setCustomHud(toPlayer, new ActionBarUIHud(toPlayer, msg));
	}
}
