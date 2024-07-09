package io.github.steaf23.easymenulib.inventory;

import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientNameItem;
import io.github.steaf23.easymenulib.inventory.UserInputMenu;

import java.util.Map;
import java.util.Stack;
import java.util.UUID;

public class MenuPacketListener extends SimplePacketListenerAbstract
{
    protected final Map<UUID, Stack<Menu>> activeMenus;

    public MenuPacketListener(Map<UUID, Stack<Menu>> activeMenus) {
        this.activeMenus = activeMenus;
    }
}