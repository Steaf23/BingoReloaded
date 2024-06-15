package io.github.steaf23.easymenulib;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import io.github.steaf23.easymenulib.util.EasyMenuTranslationKey;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Function;

public class EasyMenuLibrary
{
    private static JavaPlugin plugin;
    private static Function<EasyMenuTranslationKey, String> translateFunction;

    /**
     * Should be called on plugin load (i.e. as fast as possible after the server has started up)
     * @param plugin
     */
    public static void setPlugin(final JavaPlugin plugin) {
        EasyMenuLibrary.plugin = plugin;

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(plugin));
        PacketEvents.getAPI().getSettings().reEncodeByDefault(false)
                .checkForUpdates(true)
                .bStats(true);
        PacketEvents.getAPI().load();
        PacketEvents.getAPI().init();
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public static void setItemTranslation(Function<EasyMenuTranslationKey, String> translateFunction) {
        EasyMenuLibrary.translateFunction = translateFunction;
    }

    public static String translateKey(EasyMenuTranslationKey key) {
        return translateFunction.apply(key);
    }

    public static void sendPlayerPacket(Player player, PacketWrapper packet) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
    }
}
