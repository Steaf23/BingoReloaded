package io.github.steaf23.bingoreloaded.lib.inventory;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientNameItem;
import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformTaskScheduler;
import io.github.steaf23.bingoreloaded.lib.events.PlayerDisplayAnvilTextChangedEvent;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuPacketListener extends SimplePacketListenerAbstract
{
    //Packet events listener =========================================
    protected final Map<UUID, Integer> openPlayerInventories;

    private final PlatformTaskScheduler tasks;

    public MenuPacketListener(PlatformTaskScheduler tasks) {
        this.tasks = tasks;
        PacketEvents.getAPI().getEventManager().registerListener(this);
        this.openPlayerInventories = new HashMap<>();
    }

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.NAME_ITEM) {
            WrapperPlayClientNameItem nameItem = new WrapperPlayClientNameItem(event);

            tasks.runTask((t) -> {
                var textChangedEvent = new PlayerDisplayAnvilTextChangedEvent(nameItem.getItemName(), event.getUser().getUUID());
                Bukkit.getPluginManager().callEvent(textChangedEvent);
            });
        }
    }
}