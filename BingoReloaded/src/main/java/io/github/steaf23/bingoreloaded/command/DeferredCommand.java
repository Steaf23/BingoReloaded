package io.github.steaf23.bingoreloaded.command;

import io.github.steaf23.bingoreloaded.util.Message;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DeferredCommand extends SubCommand
{
    private final String deferredArgument;

    public DeferredCommand(String name, String deferredArgumentName) {
        super(name);
        this.deferredArgument = deferredArgumentName;
    }

    @Override
    public List<String> tabComplete(String... arguments) {
        String deferred = "<" + deferredArgument + ">";

        if (arguments.length <= 1) {
            if (tabCompletionForArgs != null) {
                List<String> completions = tabCompletionForArgs.apply(arguments);
                if (!completions.isEmpty())
                    return tabCompletionForArgs.apply(arguments);
                else
                    return List.of(deferred);
            } else {
                return List.of(deferred);
            }
        }

        if (arguments.length == 2) {
            return subCommands.stream().map(cmd -> cmd.name).collect(Collectors.toList());
        }

        SubCommand cmd = getSubCommand(arguments[1]);
        String userArgument = arguments[0];
        if (cmd != null) {
            String[] finalArguments = Arrays.copyOfRange(arguments, 1, arguments.length);
            finalArguments[0] = userArgument;
            return cmd.tabComplete(finalArguments);
        }

        return List.of();
    }

    @Override
    public boolean execute(String... arguments) {
        // A substitute can't exist when there is nothing to defer it to (this is a developer mistake)
        if (subCommands.size() == 0) {
            Message.error("Wrongly formatted command by plugin (Please report, this is a developer mistake!)");
            return false;
        }

        // there needs to be an extra argument past the substituted argument, else this command will fail.
        if (arguments.length <= 1) {
            return false;
        }

        // Skip one element, as that is what we defer the 0th argument to.
        // Cut out the second argument and consolidate the array to only contain the deferred argument for command execution
        SubCommand cmd = getSubCommand(arguments[1]);
        String userArgument = arguments[0];
        if (cmd != null) {
            String[] finalArguments = Arrays.copyOfRange(arguments, 1, arguments.length);
            finalArguments[0] = userArgument;
            return cmd.execute(finalArguments);
        }
        return false;
    }

    @Override
    protected String determineUsage(String... arguments) {
        if (subCommands.size() == 0) {
            return name + " " + usage;
        }

        if (arguments.length <= 2) {
            return name + " <" + deferredArgument + "> <" + subCommands.stream().map(subCommand -> subCommand.name)
                    .collect(Collectors.joining(" | ")) + ">";
        }

        SubCommand cmd = getSubCommand(arguments[1]);
        String userArgument = arguments[0];
        if (cmd != null) {
            String[] finalArguments = Arrays.copyOfRange(arguments, 1, arguments.length);
            finalArguments[0] = userArgument;
            return name + " <" + deferredArgument + "> " + cmd.determineUsage(finalArguments);
        }

        return name;
    }
}
