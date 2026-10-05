package io.github.steaf23.bingoreloaded.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.steaf23.bingoreloaded.api.BingoCommandSource;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class MappedCommand<Source> {
	private final Function<Source, BingoCommandSource> mapper;
	private final LiteralArgumentBuilder<Source> rootBuilder;

	public MappedCommand(String commandName, List<String> permissions, Function<Source, BingoCommandSource> mapper) {
		this.mapper = mapper;
		rootBuilder = literal(commandName)
				.requires(permissionRequirement(permissions));
	}

	public BingoCommandSource mapSource(Source source) {
		return mapper.apply(source);
	}

	public LiteralArgumentBuilder<Source> literal(String name) {
		return LiteralArgumentBuilder.literal(name);
	}

	public <T> RequiredArgumentBuilder<Source, T> argument(String name, ArgumentType<T> type) {
		return RequiredArgumentBuilder.argument(name, type);
	}

	public Command<Source> executor(Function<BingoCommandSource, Integer> command) {
		return (ctx) -> command.apply(mapSource(ctx.getSource()));
	}

	public Command<Source> executor(BiFunction<BingoCommandSource, CommandContext<Source>, Integer> command) {
		return (ctx) -> command.apply(mapSource(ctx.getSource()), ctx);
	}

	public void execute(Function<BingoCommandSource, Integer> rootCommand) {
		rootBuilder.executes(executor(rootCommand));
	}

	public void then(LiteralArgumentBuilder<Source> node) {
		rootBuilder.then(node);
	}

	public void then(List<String> permissions, LiteralArgumentBuilder<Source> node) {
		rootBuilder.then(node.requires(permissionRequirement(permissions)));
	}

	public Predicate<Source> permissionRequirement(List<String> permissions) {
		return source -> {
			BingoCommandSource mapped = mapSource(source);
			if (mapped.user() == null) {
				return false;
			}
			return mapped.user().hasAnyPermission(permissions);
		};
	}

	public LiteralCommandNode<Source> buildCommand() {
		return rootBuilder.build();
	}
}
