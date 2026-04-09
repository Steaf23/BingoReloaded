package io.github.steaf23.bingoreloaded.gameloop;

import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.data.world.WorldGroup;
import io.github.steaf23.bingoreloaded.lib.api.BingoReloadedRuntime;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.event.EventResult;
import io.github.steaf23.bingoreloaded.lib.util.ComponentUtils;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class SingularGameManager extends GameManager
{
    private boolean sendErrorOnJoin = false;

    public SingularGameManager(@NotNull BingoReloadedRuntime runtime, BingoConfigurationData config) {
        super(runtime, config);

        WorldGroup group = createWorldGroupFromExistingWorlds();
        if (group == null) {
            sendErrorOnJoin = true;
            return;
        }

        BingoSession session = new BingoSession(this, group, config);
        sessions.put(config.getOptionValue(BingoOptions.DEFAULT_WORLD_NAME), session);
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
        return false;
    }

    private WorldGroup createWorldGroupFromExistingWorlds() {
        String defaultWorldName = getGameConfig().getOptionValue(BingoOptions.DEFAULT_WORLD_NAME);
        WorldHandle overworld = getPlatform().getWorld(defaultWorldName);
        WorldHandle nether = getPlatform().getWorld(defaultWorldName + "_nether");
        WorldHandle theEnd = getPlatform().getWorld(defaultWorldName + "_the_end");

        if (overworld == null) {
            ConsoleMessenger.error("Could not create world group from existing world; " + defaultWorldName + " does not exist. Make sure the world exists and reload the plugin.");
            return null;
        }

        boolean createNether = !getGameConfig().getOptionValue(BingoOptions.DISABLE_NETHER);
        boolean createTheEnd = !getGameConfig().getOptionValue(BingoOptions.DISABLE_THE_END);

        UUID netherId = overworld.uniqueId();
        UUID endId = overworld.uniqueId();

        if (createNether) {
            if (nether == null) {
                ConsoleMessenger.error("Could not create world group from existing world; " + defaultWorldName + "_nether does not exist. Make sure the world exists and reload the plugin.");
                return null;
            }

            netherId = nether.uniqueId();
        }

        if (createTheEnd) {
            if (theEnd == null) {
                ConsoleMessenger.error("Could not create world group from existing world; " + defaultWorldName + "_the_end does not exist. Make sure the world exists and reload the plugin.");
                return null;
            }

            endId = theEnd.uniqueId();
        }

        return new WorldGroup(getPlatform(), defaultWorldName, overworld.uniqueId(), netherId, endId);
    }

    @Override
    public EventResult<?> handlePlayerJoinsServer(PlayerHandle player) {
        if (player.hasPermission("bingo.admin") && sendErrorOnJoin) {
            player.sendMessage(ComponentUtils.MINI_BUILDER.deserialize("v(<yellow>" + getPlatform().getExtensionInfo().version() + "</yellow>): <red>Cannot start Bingo Reloaded, something is up with your world setup.</red> 2 common causes: \n" +
                    "<gray>1.</gray> Check if the world name is correctly specified. If your world is named differently from <aqua>" + getGameConfig().getOptionValue(BingoOptions.DEFAULT_WORLD_NAME) + "</aqua> please edit the config by setting <aqua>defaultWorldName</aqua> to the actual world name." +
                    "\n<gray>2.</gray> Make sure If you have disabled the nether or the end, please reflect this change in the config by setting <aqua>disableNether/disableTheEnd</aqua> to true."));

        }

        return super.handlePlayerJoinsServer(player);
    }
}
