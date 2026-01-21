package io.github.steaf23.bingoreloaded.lib.action;

import io.github.steaf23.bingoreloaded.lib.api.ActionUser;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ActionTree {

	protected final List<ActionTree> subActions;
	protected final List<ActionArgument> arguments;
	protected final String name;
	protected final String description;
	private CommandFunction action;
	private final List<String> permissionWhitelist;
	//FIXME REFACTOR before release. remove staticArg because it's a bandaid fix for deferred actions in hytale. Come up with a proper fix for it.
	String staticArg;

	public ActionTree(String name, List<String> permissionWhitelist, CommandFunction action) {
		this.subActions = new ArrayList<>();
		this.arguments = new ArrayList<>();
		this.name = name;
		this.description = "{description}";
		this.action = action;
		this.permissionWhitelist = permissionWhitelist;
		this.staticArg = null;
	}

	public ActionTree(String name, CommandFunction action) {
		this(name, List.of(), action);
	}

	public ActionTree(String name, List<String> permissionWhitelist) {
		this(name, permissionWhitelist, (CommandFunction) null);
	}

	public ActionTree(String name, List<String> permissionWhitelist, String staticArg) {
		this(name, permissionWhitelist, (CommandFunction) null);
		this.staticArg = staticArg;
	}

	public ActionTree setAction(CommandFunction action) {
		this.action = action;
		return this;
	}

	public ActionTree addSubAction(ActionTree subAction) {
		subAction.staticArg = staticArg;
		subActions.add(subAction);
		return this;
	}

	public void removeSubAction(ActionTree subAction) {
		subActions.remove(subAction);
	}

	public @Nullable ActionTree getSubAction(String actionName) {
		for (ActionTree subAction: subActions) {
			if (subAction.name().equals(actionName)) {
				return subAction;
			}
		}
		return null;
	}

	public ActionTree addArgument(ActionArgument argument) {
		arguments.add(argument);
		return this;
	}

	public ActionResult execute(ActionUser user, String... arguments) {

		if (staticArg != null) {
			String[] result = new String[arguments.length + 1];
			result[0] = staticArg;
			System.arraycopy(arguments, 0, result, 1, arguments.length);
			arguments = result;
		}

		if (!hasPermission(user)) {
			return ActionResult.NO_PERMISSION;
		}

		if (action != null) {
			if (subActions.isEmpty()) {
				return action.perform(user, arguments);
			}

			if (arguments.length == 0) {
				return action.perform(user, arguments);
			}
		}

		if (arguments.length == 0) {
			return ActionResult.INCORRECT_USE;
		}

		ActionTree cmd = getSubCommand(arguments[0]);
		if (cmd != null) {
			return cmd.execute(user, Arrays.copyOfRange(arguments, 1, arguments.length));
		}
		return ActionResult.INCORRECT_USE;
	}

	public boolean hasPermission(ActionUser user) {
		return permissionWhitelist.isEmpty() || user.hasAnyPermission(permissionWhitelist);
	}

	public @Nullable List<String> tabComplete(ActionUser user, String... args) {
		if (subActions.isEmpty()) {
			return tabCompleteArgument(args);
		}

		if (args.length == 1) {
			return subActions.stream()
					.filter(cmd -> cmd.hasPermission(user))
					.map(cmd -> cmd.name).collect(Collectors.toList());
		}

		ActionTree cmd = getSubCommand(args[0]);
		if (cmd != null) {
			return cmd.tabComplete(user, Arrays.copyOfRange(args, 1, args.length));
		}

		return List.of();
	}

	public List<String> tabCompleteArgument(String... args) {
		int argumentIndex = args.length;
		if (argumentIndex >= arguments.size()) {
			return null;
		}
		ActionArgument argument = arguments.get(argumentIndex);
		return argument.possibleValues().apply(new ActionArgument.TabCompletionContext(args));
	}

	public ActionTree getSubCommand(String name) {
		for (ActionTree actionTree : subActions) {
			if (actionTree.name.equals(name)) {
				return actionTree;
			}
		}
		return null;
	}

	public List<ActionTree> subCommands() {
		return subActions;
	}

	public String usage(ActionArgument.TabCompletionContext context) {
		return "/" + determineUsage(context);
	}

	protected String determineUsage(ActionArgument.TabCompletionContext context) {
		if (subActions.isEmpty() || context.arguments().length == 0) {
			return name + arguments.stream()
					.map(arg -> arg.createUsage(context))
					.reduce(" ", (acc, val) -> acc + " " + val);
		}

		String[] arguments = context.arguments();

		ActionTree cmd = getSubCommand(arguments[0]);
		if (cmd != null) {
			return name + " " + cmd.determineUsage(new ActionArgument.TabCompletionContext(Arrays.copyOfRange(arguments, 1, arguments.length)));
		}

		if (arguments.length == 1) {
			return name + " <" + subActions.stream().map(subCommand -> subCommand.name)
					.collect(Collectors.joining(" | ")) + ">";
		}

		return "";
	}

	public String name() {
		return name;
	}

	public List<ActionArgument> arguments() {
		return arguments;
	}

	@FunctionalInterface
	public interface CommandFunction {

		ActionResult perform(ActionUser user, String... arguments);
	}
}
