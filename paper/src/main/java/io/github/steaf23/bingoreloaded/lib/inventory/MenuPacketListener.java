package io.github.steaf23.bingoreloaded.lib.inventory;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCustomClickAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientNameItem;
import io.github.steaf23.bingoreloaded.lib.api.ServerSoftware;
import io.github.steaf23.bingoreloaded.lib.events.PlayerDisplayAnvilTextChangedEvent;
import io.github.steaf23.bingoreloaded.lib.events.PlayerDisplayCustomClickActionEvent;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuPacketListener extends SimplePacketListenerAbstract
{
    //Packet events listener =========================================
    protected final Map<UUID, Integer> openPlayerInventories;

    private final ServerSoftware server;

    public MenuPacketListener(ServerSoftware server) {
        this.server = server;
        PacketEvents.getAPI().getEventManager().registerListener(this);
        this.openPlayerInventories = new HashMap<>();
    }

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            ConsoleMessenger.bug("Invalid player!", this);
            return;
        }
        if (event.getPacketType() == PacketType.Play.Client.NAME_ITEM) {
            WrapperPlayClientNameItem nameItem = new WrapperPlayClientNameItem(event);

            server.runTask(player.getWorld().getUID(), (t) -> {
                var textChangedEvent = new PlayerDisplayAnvilTextChangedEvent(nameItem.getItemName(), event.getUser().getUUID());
                Bukkit.getPluginManager().callEvent(textChangedEvent);
            });
        }
        else if (event.getPacketType() == PacketType.Play.Client.CUSTOM_CLICK_ACTION) {
            WrapperPlayClientCustomClickAction customClickAction = new WrapperPlayClientCustomClickAction(event);

            server.runTask(player.getWorld().getUID(), (t) -> {
                var customClickActionEvent = new PlayerDisplayCustomClickActionEvent(event.getUser().getUUID(), customClickAction.getId().key(), (NBTCompound)customClickAction.getPayload());
                Bukkit.getPluginManager().callEvent(customClickActionEvent);
            });
        }
    }
}