package io.github.steaf23.bingoreloaded.protocol.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;

import java.util.ArrayList;
import java.util.List;

public class MessageParser {

	public static final MiniMessage MINI_BUILDER = MiniMessage.builder()
			.tags(TagResolver.builder()
					.resolvers(StandardTags.defaults(), TinyCaps.TAG_RESOLVER)
					.build())
			.build();

	public static Component createPhrase(String input, Component... arguments) {
		return createPhrase(input, List.of(), arguments);
	}

	public static Component createPhrase(String input, List<TagResolver> additionalResolvers, Component... arguments) {
		// phrases cannot contain newlines, which is why this is filtered explicitly using convertConfigStringToMini
		// create tag resolvers for each argument, which will appear as <0>, <1> etc... in the mini message string and be replaced by the correct components.
		List<TagResolver> resolvers = new ArrayList<>();
		for (int i = 0; i < arguments.length; i++) {
			resolvers.add(Placeholder.component(Integer.toString(i), arguments[i]));
		}

		resolvers.addAll(additionalResolvers);

		return MessageParser.MINI_BUILDER.deserialize(input, resolvers.toArray(TagResolver[]::new));
	}
}
