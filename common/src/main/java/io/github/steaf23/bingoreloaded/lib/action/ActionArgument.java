package io.github.steaf23.bingoreloaded.lib.action;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public record ActionArgument(String name, boolean required,
							 Function<TabCompletionContext, @Nullable List<String>> possibleValues, UseDisplay useDisplay) {

	public enum UseDisplay {
		NAME,
		OPTIONS,
		AUTOMATIC,
	}

	public record TabCompletionContext(String... arguments) {

	}

	public ActionArgument withUseDisplay(UseDisplay display) {
		return new ActionArgument(this.name, this.required, this.possibleValues, display);
	}


	public static ActionArgument optional(String name, Function<TabCompletionContext, List<String>> possibleValues) {
		return new ActionArgument(name, false, possibleValues, UseDisplay.AUTOMATIC);
	}

	public static ActionArgument optional(String name, List<String> possibleValues) {
		return new ActionArgument(name, false, ctx -> possibleValues, UseDisplay.AUTOMATIC);
	}

	public static ActionArgument optional(String name) {
		return new ActionArgument(name, false, ctx -> List.of(), UseDisplay.AUTOMATIC);
	}

	public static ActionArgument required(String name, Function<TabCompletionContext, @Nullable List<String>> possibleValues) {
		return new ActionArgument(name, true, possibleValues, UseDisplay.AUTOMATIC);
	}

	public static ActionArgument required(String name, @Nullable List<String> possibleValues) {
		return new ActionArgument(name, true, ctx -> possibleValues, UseDisplay.AUTOMATIC);
	}

	public static ActionArgument required(String name) {
		return new ActionArgument(name, true, ctx -> List.of(), UseDisplay.AUTOMATIC);
	}

	public String createUsage(TabCompletionContext context) {

		List<String> values = possibleValues.apply(context);

		boolean showName = switch (useDisplay) {
			case NAME -> true;
			case OPTIONS -> false;
			case AUTOMATIC -> values == null || values.size() > 4;
		};

		String arg;

		if (showName || values == null || values.isEmpty()) {
			arg = name;
		} else {
			arg = values.stream().reduce("", (acc, val) -> acc + " | " + val);
		}

		if (required) {
			return "<" + arg + ">";
		} else {
			return "[" + arg + "]";
		}
	}
}
