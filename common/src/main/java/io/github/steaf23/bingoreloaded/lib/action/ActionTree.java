package io.github.steaf23.bingoreloaded.lib.action;

import io.github.steaf23.bingoreloaded.lib.api.ActionUser;
import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformServer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ActionTree
{
    protected final List<ActionTree> subActions;
    protected final String name;
    private ActionExecutor action;
    private final List<String> permissionWhitelist;

    protected String usage;
    protected ActionTabCompleter tabCompletionForArgs;

    protected ActionUser lastUser = null;

    public ActionTree(String name, List<String> permissionWhitelist, ActionExecutor action) {
        this.subActions = new ArrayList<>();
        this.name = name;
        this.action = action;
        this.usage = "";
        this.permissionWhitelist = permissionWhitelist;
        this.tabCompletionForArgs = (_, _) -> List.of();
    }

    public ActionTree(String name, ActionExecutor action) {
        this(name, List.of(), action);
    }

    public ActionTree(String name, Function<String[], ActionResult> action) {
        this(name, List.of(), (_, args) -> action.apply(args));
    }

    public ActionTree(String name, List<String> permissionWhitelist, Function<String[], ActionResult> action) {
        this(name, permissionWhitelist, (_, args) -> action.apply(args));
    }

    public ActionTree(String name, List<String> permissionWhitelist) {
        this(name, permissionWhitelist, (ActionExecutor) null);
    }

    public ActionTree setAction(ActionExecutor action) {
        this.action = action;
        return this;
    }

    public ActionTree setAction(Function<String[], ActionResult> action) {
        this.action = (_, args) -> action.apply(args);
        return this;
    }

	public ActionTree addSubAction(ActionTree subAction) {
        subActions.add(subAction);
        return this;
    }

    public ActionTree addUsage(String usage) {
        this.usage = usage;
        return this;
    }

    public ActionTree addTabCompletion(Function<String[], List<String>> tabCompletionForArgs) {
        this.tabCompletionForArgs = (_, args) -> tabCompletionForArgs.apply(args);
        return this;
    }

    public ActionTree addTabCompletion(ActionTabCompleter tabCompletionForArgs) {
        this.tabCompletionForArgs = tabCompletionForArgs;
        return this;
    }

    public ActionResult execute(PlatformServer server, ActionUser user, String... arguments) {
        lastUser = user;

        if (!hasPermission(user)) {
            return ActionResult.NO_PERMISSION;
        }

        if (action != null) {
            if (subActions.isEmpty()) {
                return action.execute(server, arguments);
            }

            if (arguments.length == 0) {
                return action.execute(server, arguments);
            }
        }

        if (arguments.length == 0) {
            return ActionResult.INCORRECT_USE;
        }

        ActionTree cmd = getSubCommand(arguments[0]);
        if (cmd != null) {
            return cmd.execute(server, lastUser, Arrays.copyOfRange(arguments, 1, arguments.length));
        }
        return ActionResult.INCORRECT_USE;
    }

    public boolean hasPermission(ActionUser user) {
        return permissionWhitelist.isEmpty() || user.hasAnyPermission(permissionWhitelist);
    }

    public @Nullable List<String> tabComplete(PlatformServer server, ActionUser user, String... arguments) {
        if (subActions.isEmpty()) {
            return tabCompletionForArgs.tabComplete(server, arguments);
        }

        if (arguments.length == 1) {
            return subActions.stream()
                    .filter(cmd -> cmd.hasPermission(user))
                    .map(cmd -> cmd.name).collect(Collectors.toList());
        }

        ActionTree cmd = getSubCommand(arguments[0]);
        if (cmd != null) {
            return cmd.tabComplete(server, user, Arrays.copyOfRange(arguments, 1, arguments.length));
        }

        return List.of();
    }

    public ActionTree getSubCommand(String name) {
        for (ActionTree actionTree : subActions) {
            if (actionTree.name.equals(name)) {
                return actionTree;
            }
        }
        return null;
    }

    public String usage(String... arguments) {
        return "/" + determineUsage(arguments);
    }

    protected String determineUsage(String... arguments) {
        if (subActions.isEmpty() || arguments.length == 0) {
            return name + " " + usage;
        }

        ActionTree cmd = getSubCommand(arguments[0]);
        if (cmd != null) {
            return name + " " + cmd.determineUsage(Arrays.copyOfRange(arguments, 1, arguments.length));
        }

        if (arguments.length == 1) {
            return name + " <" + subActions.stream().map(subCommand -> subCommand.name)
                    .collect(Collectors.joining(" | ")) + ">";
        }

        return "";
    }

    public ActionUser getLastUser() {
        return lastUser;
    }

    public String name() {
        return name;
    }

    @FunctionalInterface
    public interface ActionExecutor {
        ActionResult execute(PlatformServer server, String[] args);
    }

    @FunctionalInterface
    public interface ActionTabCompleter {
        List<String> tabComplete(PlatformServer server, String[] args);
    }
}
