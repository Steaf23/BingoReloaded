package io.github.steaf23.bingoreloaded;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.Argument;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.steaf23.bingoreloaded.lib.action.ActionArgument;
import io.github.steaf23.bingoreloaded.lib.action.ActionTree;
import io.github.steaf23.bingoreloaded.lib.api.ActionUser;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandleHytale;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an example command that will simply print the name of the plugin in chat when used.
 */
public class HytaleCommand extends AbstractPlayerCommand {

    private final ActionTree actionTree;

    private final List<Argument<?, String>> arguments;

    public HytaleCommand(ActionTree actionTree, String description) {
        super(actionTree.name(), description);
        this.setPermissionGroup(GameMode.Adventure); // Allows the command to be used by anyone, not just OP
        this.actionTree = actionTree;
        this.arguments = new ArrayList<>();

        for (ActionArgument arg : actionTree.arguments()) {
            if (arg.required()) {
                arguments.add(withRequiredArg(arg.name(), "{desc}", ArgTypes.STRING));
            } else {
                arguments.add(withOptionalArg(arg.name(), "{desc}", ArgTypes.STRING));
            }
        }

        for (ActionTree sub : actionTree.subCommands()) {
            addSubCommand(new HytaleCommand(sub, ""));
        }
    }

    @Override
    protected void execute(@NotNull CommandContext ctx, @NotNull Store<EntityStore> store, @NotNull Ref<EntityStore> ref, @NotNull PlayerRef playerRef, @NotNull World world) {

        ActionUser user = new PlayerHandleHytale(playerRef);

        actionTree.execute(user, arguments.stream()
                        .filter(arg -> arg.get(ctx) != null)
                        .map(arg -> arg.get(ctx)).toArray(String[]::new));
    }
}