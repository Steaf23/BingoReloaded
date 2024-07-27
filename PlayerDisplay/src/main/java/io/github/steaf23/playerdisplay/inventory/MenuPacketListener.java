package io.github.steaf23.playerdisplay.inventory;

import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientNameItem;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

public class MenuPacketListener extends SimplePacketListenerAbstract
{
    //Packet events listener =========================================
    protected final Map<UUID, Stack<Menu>> activeMenus;
    protected final Map<UUID, Integer> openPlayerInventories;

    public MenuPacketListener(Map<UUID, Stack<Menu>> activeMenus) {
        this.activeMenus = activeMenus;
        this.openPlayerInventories = new HashMap<>();
    }

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.NAME_ITEM) {
            WrapperPlayClientNameItem nameItem = new WrapperPlayClientNameItem(event);

            Stack<Menu> menus = activeMenus.get(event.getUser().getUUID());
            if (menus == null || menus.size() == 0) {
                return;
            }

            if (menus.peek() instanceof UserInputMenu inputMenu) {
                inputMenu.handleTextChanged(nameItem.getItemName());
            }

            event.setCancelled(true);
        }
    }

    @Override
    public void onPacketPlaySend(PacketPlaySendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.OPEN_WINDOW) {
            WrapperPlayServerOpenWindow openWindow = new WrapperPlayServerOpenWindow(event);
//            openPlayerInventories.put(event.getUser().getUUID(), openWindow.getContainerId());
        }
    }

    //NOTE: currently not used
    public int getOpenInventoryIdForPlayer(UUID playerId) {
        return openPlayerInventories.getOrDefault(playerId, -1);
    }
}