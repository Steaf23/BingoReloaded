package io.github.steaf23.bingoreloaded.gameloop;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.data.world.WorldSet;
import io.github.steaf23.bingoreloaded.lib.api.BingoReloadedRuntime;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformServer;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.protocol.message.MessageParser;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class IntegratedGameManager extends GameManager
{
    private boolean sendErrorOnJoin = false;

    public IntegratedGameManager(PlatformServer server, @NotNull BingoReloadedRuntime runtime, BingoConfigurationData config) {
        super(server, runtime, config);

        WorldSet group = createWorldGroupFromExistingWorlds();
        if (group == null) {
            sendErrorOnJoin = true;
            return;
        }

        BingoSession session = new BingoSession(this, group, config);
        Optional<Key> name = config.getOptionValue(BingoOptions.SETUP).lobbyWorldKey();
        sessions.put(name.orElseThrow().value(), session);
    }

    // Don't create extra worlds...
    @Override
    public void setup(List<String> worldNames) {
    }

    @Override
    public boolean destroySession(String sessionName) {
        ConsoleMessenger.error("Cannot destroy session when using configuration singular!");
        return false;
    }

    @Override
    public boolean createSession(String sessionName) {
        ConsoleMessenger.error("Cannot create session when using configuration singular!");
        return super.createSession(sessionName);
    }

    private WorldSet createWorldGroupFromExistingWorlds() {
        Optional<Key> optKey = getGameConfig().getOptionValue(BingoOptions.SETUP).lobbyWorldKey();
        if (optKey.isEmpty()) {
            return null;
        }

        Key key = optKey.get();

        WorldHandle overworld = getServer().getWorld(key);
        WorldHandle nether;
        WorldHandle theEnd;
        // Special case for default overworld...
        if (key.equals(Key.key("minecraft:overworld"))) {
            nether = getServer().getWorld(Key.key("minecraft:the_nether"));
            theEnd = getServer().getWorld(Key.key("minecraft:the_end"));
        } else {
            nether = getServer().getWorld(WorldSet.netherKey(key));
            theEnd = getServer().getWorld(WorldSet.theEndKey(key));
        }

        if (overworld == null) {
            ConsoleMessenger.error("Could not create world group from existing world; " + key + " does not exist. Make sure the world exists and reload the plugin.");
            return null;
        }

        boolean createNether = !getGameConfig().getOptionValue(BingoOptions.DISABLE_NETHER);
        boolean createTheEnd = !getGameConfig().getOptionValue(BingoOptions.DISABLE_THE_END);

        if (createNether) {
            if (nether == null) {
                ConsoleMessenger.error("Could not create world group from existing world; " + key + "_nether does not exist. Make sure the world exists and reload the plugin.");
                return null;
            }
        }

        if (createTheEnd) {
            if (theEnd == null) {
                ConsoleMessenger.error("Could not create world group from existing world; " + key + "_the_end does not exist. Make sure the world exists and reload the plugin.");
                return null;
            }
        }

        return new WorldSet(getServer(), key, createNether, createTheEnd);
    }

    @Override
    public void handlePlayerJoinsServer(PlayerHandle player) {
        if (BingoReloaded.isAdmin(player) && sendErrorOnJoin) {
            player.sendMessage(MessageParser.MINI_BUILDER.deserialize("v(<yellow>" + BingoReloaded.getMetaInfo().version() + "</yellow>): <red>Cannot start Bingo Reloaded, something is up with your world setup.</red> 2 common causes: \n" +
                    "<gray>1.</gray> Check if the world name is correctly specified. If your world is named differently from <aqua>" + getGameConfig().getOptionValue(BingoOptions.SETUP).lobbyWorldKey() + "</aqua> please edit the config by setting <aqua>defaultWorldName</aqua> to the actual world name." +
                    "\n<gray>2.</gray> Make sure If you have disabled the nether or the end, please reflect this change in the config by setting <aqua>disableNether/disableTheEnd</aqua> to true."));

        }

        super.handlePlayerJoinsServer(player);
    }
}
