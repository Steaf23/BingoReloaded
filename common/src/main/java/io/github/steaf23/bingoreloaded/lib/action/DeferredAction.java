package io.github.steaf23.bingoreloaded.lib.action;

import io.github.steaf23.bingoreloaded.lib.api.ActionUser;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DeferredAction extends ActionTree
{
    private final String deferredArgument;

    public DeferredAction(String name, String deferredArgumentName, List<String> requiredPermissions) {
        super(name, requiredPermissions);
        this.deferredArgument = deferredArgumentName;
    }

    @Override
    public List<String> tabComplete(ActionUser user, String... arguments) {
        String deferred = "<" + deferredArgument + ">";

        if (arguments.length <= 1) {
            List<String> completions = tabCompleteArgument(arguments);
            if (!completions.isEmpty())
                return completions;
            else
                return List.of(deferred);
        }

        if (arguments.length == 2) {
            return subActions.stream().map(cmd -> cmd.name).collect(Collectors.toList());
        }

        ActionTree cmd = getSubCommand(arguments[1]);
        String userArgument = arguments[0];
        if (cmd != null) {
            String[] finalArguments = Arrays.copyOfRange(arguments, 1, arguments.length);
            finalArguments[0] = userArgument;
            return cmd.tabComplete(user, finalArguments);
        }

        return List.of();
    }

    @Override
    public ActionResult execute(ActionUser user, String... arguments) {
        // A substitute can't exist when there is nothing to defer it to (this is a developer mistake)
        if (subActions.isEmpty()) {
            ConsoleMessenger.bug("Wrongly formatted action by plugin {this is a developer mistake!}", this);
            return ActionResult.IGNORED;
        }

        // there needs to be an extra argument past the substituted argument, else this command will fail.
        if (arguments.length <= 1) {
            return ActionResult.INCORRECT_USE;
        }

        // Skip one element, as that is what we defer the 0th argument to.
        // Cut out the second argument and consolidate the array to only contain the deferred argument for command execution
        ActionTree cmd = getSubCommand(arguments[1]);
        String userArgument = arguments[0];
        if (cmd != null) {
            String[] finalArguments = Arrays.copyOfRange(arguments, 1, arguments.length);
            finalArguments[0] = userArgument;
            return cmd.execute(user, finalArguments);
        }
        return ActionResult.INCORRECT_USE;
    }

    @Override
    protected String determineUsage(ActionArgument.TabCompletionContext context) {
        if (subActions.isEmpty()) {
            return name + arguments.stream()
                    .map(arg -> arg.createUsage(context))
                    .reduce(" ", (acc, val) -> acc + " " + val);
        }

        String[] arguments = context.arguments();

        if (arguments.length <= 2) {
            return name + " <" + deferredArgument + "> <" + subActions.stream().map(subCommand -> subCommand.name)
                    .collect(Collectors.joining(" | ")) + ">";
        }

        ActionTree cmd = getSubCommand(arguments[1]);
        String userArgument = arguments[0];
        if (cmd != null) {
            String[] finalArguments = Arrays.copyOfRange(arguments, 1, arguments.length);
            finalArguments[0] = userArgument;
            return name + " <" + deferredArgument + "> " + cmd.determineUsage(new ActionArgument.TabCompletionContext(finalArguments));
        }

        return name;
    }
}
