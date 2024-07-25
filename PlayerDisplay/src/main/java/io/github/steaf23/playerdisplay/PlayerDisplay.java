package io.github.steaf23.playerdisplay;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import io.github.steaf23.playerdisplay.util.PlayerDisplayTranslationKey;
import io.github.steaf23.playerdisplay.util.TinyCaps;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Function;

public class PlayerDisplay
{
    public static final MiniMessage MINI_BUILDER = MiniMessage.builder()
            .tags(TagResolver.builder()
                    .resolvers(StandardTags.defaults(), TinyCaps.TAG_RESOLVER)
                    .build())
            .build();

    private static JavaPlugin plugin;
    private static Function<PlayerDisplayTranslationKey, Component> translateFunction;
    private static boolean USE_CUSTOM_TEXTURES;

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

    public static void setUseCustomTextures(boolean useCustomTextures) {
        USE_CUSTOM_TEXTURES = useCustomTextures;
    }

    public static boolean useCustomTextures() {
        return USE_CUSTOM_TEXTURES;
    }
}
