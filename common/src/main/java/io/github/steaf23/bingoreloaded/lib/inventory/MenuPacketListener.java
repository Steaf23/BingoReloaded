package io.github.steaf23.bingoreloaded.lib.inventory;

import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCustomClickAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientNameItem;
import io.github.steaf23.bingoreloaded.lib.PlayerDisplay;
import io.github.steaf23.bingoreloaded.lib.event.events.PlayerDisplayAnvilTextChangedEvent;
import io.github.steaf23.bingoreloaded.lib.event.events.PlayerDisplayCustomClickActionEvent;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuPacketListener extends SimplePacketListenerAbstract
{
    //Packet events listener =========================================
    protected final Map<UUID, Integer> openPlayerInventories;

    public MenuPacketListener() {
        PacketEvents.getAPI().getEventManager().registerListener(this);
        this.openPlayerInventories = new HashMap<>();
    }

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.NAME_ITEM) {
            WrapperPlayClientNameItem nameItem = new WrapperPlayClientNameItem(event);

            Bukkit.getScheduler().callSyncMethod(PlayerDisplay.getPlugin(), () -> {
                var textChangedEvent = new PlayerDisplayAnvilTextChangedEvent(nameItem.getItemName(), event.getUser().getUUID());
                Bukkit.getPluginManager().callEvent(textChangedEvent);
                return null;
            });
        }
        else if (event.getPacketType() == PacketType.Play.Client.CUSTOM_CLICK_ACTION) {
            WrapperPlayClientCustomClickAction customClickAction = new WrapperPlayClientCustomClickAction(event);

            Bukkit.getScheduler().callSyncMethod(PlayerDisplay.getPlugin(), () -> {
                var customClickActionEvent = new PlayerDisplayCustomClickActionEvent(event.getUser().getUUID(), customClickAction.getId().key(), (NBTCompound)customClickAction.getPayload());
                Bukkit.getPluginManager().callEvent(customClickActionEvent);
                return null;
            });
        }
    }
}