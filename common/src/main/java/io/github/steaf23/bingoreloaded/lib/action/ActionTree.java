package io.github.steaf23.bingoreloaded.lib.action;

import io.github.steaf23.bingoreloaded.lib.api.ActionUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ActionTree
{
    protected final List<ActionTree> subActions;
    protected final String name;
    private Function<String[], Boolean> action;
    private final List<String> permissionWhitelist;

    protected String usage;
    protected Function<String[], List<String>> tabCompletionForArgs;

    private ActionUser lastUser = null;

    public ActionTree(String name, List<String> permissionWhitelist, Function<String[], Boolean> action) {
        this.subActions = new ArrayList<>();
        this.name = name;
        this.action = action;
        this.usage = "";
        this.permissionWhitelist = permissionWhitelist;
        this.tabCompletionForArgs = args -> List.of();
    }

    public ActionTree(String name, Function<String[], Boolean> action) {
        this(name, List.of());
    }

    public ActionTree(String name, List<String> permissionWhitelist) {
        this(name, permissionWhitelist, null);
    }

    public ActionTree setAction(Function<String[], Boolean> action) {
        this.action = action;
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
        this.tabCompletionForArgs = tabCompletionForArgs;
        return this;
    }

    public boolean execute(ActionUser user, String... arguments) {
        lastUser = user;

        if (!hasPermission(user)) {
            return false;
        }

        if (subActions.isEmpty()) {
            return action.apply(arguments);
        }

        if (arguments.length == 0) {
            return false;
        }

        ActionTree cmd = getSubCommand(arguments[0]);
        if (cmd != null) {
            return cmd.execute(lastUser, Arrays.copyOfRange(arguments, 1, arguments.length));
        }
        return false;
    }

    public boolean hasPermission(ActionUser user) {
        return permissionWhitelist.isEmpty() || user.hasAnyPermission(permissionWhitelist);
    }

    public List<String> tabComplete(String... arguments) {
        if (subActions.isEmpty()) {
            return tabCompletionForArgs.apply(arguments);
        }

        if (arguments.length == 1) {
            return subActions.stream().map(cmd -> cmd.name).collect(Collectors.toList());
        }

        ActionTree cmd = getSubCommand(arguments[0]);
        if (cmd != null) {
            return cmd.tabComplete(Arrays.copyOfRange(arguments, 1, arguments.length));
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
        if (subActions.isEmpty()) {
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
}
