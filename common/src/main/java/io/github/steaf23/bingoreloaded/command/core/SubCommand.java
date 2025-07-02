package io.github.steaf23.bingoreloaded.command.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SubCommand
{
    protected final List<SubCommand> subCommands;
    protected final String name;
    private final Function<String[], Boolean> action;
    protected String usage;
    protected Function<String[], List<String>> tabCompletionForArgs;

    public SubCommand(String name, Function<String[], Boolean> action) {
        this.subCommands = new ArrayList<>();
        this.name = name;
        this.action = action;
        this.usage = "";
        this.tabCompletionForArgs = args -> List.of();
    }

    public SubCommand(String name) {
        this(name, null);
    }


    public SubCommand addSubCommand(SubCommand subCommand) {
        subCommands.add(subCommand);
        return this;
    }

    public SubCommand addUsage(String usage) {
        this.usage = usage;
        return this;
    }

    public SubCommand addTabCompletion(Function<String[], List<String>> tabCompletionForArgs) {
        this.tabCompletionForArgs = tabCompletionForArgs;
        return this;
    }

    public boolean execute(String... arguments) {
        if (subCommands.isEmpty()) {
            return action.apply(arguments);
        }

        if (arguments.length == 0) {
            return false;
        }

        SubCommand cmd = getSubCommand(arguments[0]);
        if (cmd != null) {
            return cmd.execute(Arrays.copyOfRange(arguments, 1, arguments.length));
        }
        return false;
    }

    public List<String> tabComplete(String... arguments) {
        if (subCommands.isEmpty()) {
            return tabCompletionForArgs.apply(arguments);
        }

        if (arguments.length == 1) {
            return subCommands.stream().map(cmd -> cmd.name).collect(Collectors.toList());
        }

        SubCommand cmd = getSubCommand(arguments[0]);
        if (cmd != null) {
            return cmd.tabComplete(Arrays.copyOfRange(arguments, 1, arguments.length));
        }

        return List.of();
    }

    public SubCommand getSubCommand(String name) {
        for (SubCommand subCommand : subCommands) {
            if (subCommand.name.equals(name)) {
                return subCommand;
            }
        }
        return null;
    }

    public String usage(String... arguments) {
        return "/" + determineUsage(arguments);
    }

    protected String determineUsage(String... arguments) {
        if (subCommands.isEmpty()) {
            return name + " " + usage;
        }

        SubCommand cmd = getSubCommand(arguments[0]);
        if (cmd != null) {
            return name + " " + cmd.determineUsage(Arrays.copyOfRange(arguments, 1, arguments.length));
        }

        if (arguments.length == 1) {
            return name + " <" + subCommands.stream().map(subCommand -> subCommand.name)
                    .collect(Collectors.joining(" | ")) + ">";
        }

        return "";
    }
}
