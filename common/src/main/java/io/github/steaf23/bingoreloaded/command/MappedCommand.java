package io.github.steaf23.bingoreloaded.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.steaf23.bingoreloaded.api.BingoCommandSource;
import net.kyori.adventure.key.Key;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class MappedCommand<Source> {
	public record Settings<Source>(Function<Source, BingoCommandSource> mapper, ArgumentType<Key> keyType) {
	}

	private final LiteralArgumentBuilder<Source> rootBuilder;
	private final Settings<Source> settings;

	public MappedCommand(String commandName, List<String> permissions, Settings<Source> settings) {
		this.settings = settings;
		rootBuilder = literal(commandName)
				.requires(permissionRequirement(permissions));
	}

	public BingoCommandSource mapSource(Source source) {
		return settings.mapper.apply(source);
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

	public void then(ArgumentBuilder<Source, ?> node) {
		rootBuilder.then(node);
	}

	public void then(List<String> permissions, ArgumentBuilder<Source, ?> node) {
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

	public ArgumentType<Key> keyType() {
		return settings.keyType;
	}

	public Key getKey(CommandContext<Source> context, String name) {
		return context.getArgument(name, Key.class);
	}

	public static <E> CompletableFuture<Suggestions> suggestAll(SuggestionsBuilder builder, Collection<E> collection, Function<E, String> map) {
		for (E e : collection) {
			builder.suggest(map.apply(e));
		}
		return builder.buildFuture();
	}

	public static CompletableFuture<Suggestions> suggestAll(SuggestionsBuilder builder, Collection<String> collection) {
		for (String e : collection) {
			builder.suggest(e);
		}
		return builder.buildFuture();
	}

}
