package io.github.steaf23.playerdisplay;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import io.github.steaf23.playerdisplay.util.PlayerDisplayTranslationKey;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Function;

public class PlayerDisplay
{
    private static JavaPlugin plugin;
    private static Function<PlayerDisplayTranslationKey, Component> translateFunction;

    /**
     * Should be called on plugin load (i.e. as fast as possible after the server has started up)
     * @param plugin
     */
    public static void setPlugin(final JavaPlugin plugin) {
        PlayerDisplay.plugin = plugin;

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(plugin));
        PacketEvents.getAPI().getSettings().reEncodeByDefault(false)
                .checkForUpdates(true);
        PacketEvents.getAPI().load();
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public static void setItemTranslation(Function<PlayerDisplayTranslationKey, Component> translateFunction) {
        PlayerDisplay.translateFunction = translateFunction;
    }

    public static Component translateKey(PlayerDisplayTranslationKey key) {
        return translateFunction.apply(key);
    }

    public static void sendPlayerPacket(Player player, PacketWrapper packet) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
    }

    public static void onPluginEnable() {
        PacketEvents.getAPI().init();
    }
}
