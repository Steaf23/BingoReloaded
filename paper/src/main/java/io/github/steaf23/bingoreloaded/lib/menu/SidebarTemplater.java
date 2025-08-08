package io.github.steaf23.bingoreloaded.lib.menu;

import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.ScoreboardData;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.util.ComponentUtils;
import net.kyori.adventure.text.Component;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SidebarTemplater
{
    private static final Pattern ARG_PATTERN = Pattern.compile("\\{[a-zA-Z0-9_]+}");

	public static Component title(ScoreboardData.SidebarTemplate template, PlayerHandle player) {
		Component title = Component.empty();
		Component[] titleComponents = BingoMessage.convertForPlayer(template.title(), player);
		if (titleComponents.length > 0) {
			title = titleComponents[0];
		}
		return title;
	}

    public static List<Component> sidebarComponents(ScoreboardData.SidebarTemplate template, PlayerHandle forPlayer) {
        // Newlines on the scoreboard lines is not supported, so we can ignore it.
        // Also assume that every template line is a config string.

        // Step 1. collect all components, including ones from template arguments, into a single list of components.
        int lineIndex = 0;
        List<Component> components = new ArrayList<>();
        for (String line : template.lines()) {
            // convert placeholders
            //FIXME: REFACTOR: placeholder API (Maybe make a ComponentPreParser that can convert strings into other strings for example placeholderAPI.
//            if (BingoReloaded.PLACEHOLDER_API_ENABLED) {
//                line = PlaceholderAPI.setPlaceholders(forPlayer, line);
//            }

            // split out lines that contain arguments into separate components, that then join if they are single line or append separately if multiline.
            Matcher matcher = ARG_PATTERN.matcher(line);
            // If we enter the while condition, we have to add the last part of the line to the end of the last component added by this line, be it an argument or a piece of text.
            boolean appendToLastComponent = false;
            while (matcher.find()) {
                String match = matcher.group();
                String key = match.replace("{", "").replace("}", "");
                String[] lineParts = line.split(Pattern.quote(match), 2);
                // cut out the left side of the line, up to the match from the original line, and continue matching...
                String beforeMatch = lineParts[0];
                String before = BingoMessage.convertConfigStringToSingleMini(beforeMatch);
                Component componentToAdd;
                if (appendToLastComponent) {
                    // Here a single line has multiple arguments, we want to try to keep them on the same line when the argument permits.
                    componentToAdd = components.removeLast();
                } else {
                    componentToAdd = Component.empty();
                }
                componentToAdd = componentToAdd.append(ComponentUtils.MINI_BUILDER.deserialize(before));

                appendToLastComponent = true;
                Component[] argument = template.arguments().getOrDefault(key, new Component[]{});
                if (argument.length != 0) {
                    // Append the first argument to the same component to stay in one line as much as possible.
                    // Any subsequent component of the argument will be placed on a next line
                    for (Component arg : argument) {
                        // We cannot afford to add more argument lines, we have to crop it in order to fit the remaining lines
                        if (15 - components.size() - (template.lines().length - lineIndex) <= 0) {
                            break;
                        }

                        components.add(componentToAdd.append(arg));
                        componentToAdd = Component.empty();
                    }
                } else {
                    components.add(componentToAdd);
                }

                //reduce line to everything on right side of the match.
                line = line.substring(beforeMatch.length() + match.length());
            }
            // add what's left of the line as another component
            if (line.isEmpty()) {
                lineIndex++;
                continue;
            }

            // finish the end of the line
            Component rightSide = ComponentUtils.MINI_BUILDER.deserialize(line);
            if (appendToLastComponent) {
                components.set(components.size() - 1, components.getLast().append(rightSide));
            } else {
                components.add(rightSide);
            }
            lineIndex++;
        }

        return components;
    }
}
