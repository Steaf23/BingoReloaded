package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.util.CollectionHelper;
import io.github.steaf23.playerdisplay.PlayerDisplay;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import io.github.steaf23.playerdisplay.util.TinyCaps;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum BingoMessage
{
    SETTINGS_SCOREBOARD_TITLE("branding.scoreboard_title"),
    MESSAGE_PREFIX("branding.message_prefix"),
    MENU_PREFIX("branding.menu_prefix"),
    LANGUAGE_NAME("name"),
    CHANGED_LANGUAGE("changed"),
    COMMAND_USAGE("command.use"),
    NO_DEATHMATCH("command.bingo.no_deathmatch"),
    DURATION("game.timer.duration"),
    TIME_LEFT("game.timer.time_left"),
    SETTINGS_UPDATED("game.lobby.settings_updated"),
    WAIT_STATUS("game.lobby.wait_status"),
    PLAYER_STATUS("game.lobby.player_status"),
    STARTING_STATUS("game.lobby.starting_status"),
    VOTE_GAMEMODE("game.lobby.vote_gamemode"),
    VOTE_KIT("game.lobby.vote_kit"),
    VOTE_CARD("game.lobby.vote_card"),
    VOTE_COUNT("game.lobby.vote_count"),
    VOTE_WON("game.lobby.vote_won"),
    NO_PLAYERS("game.start.no_players"),
    ALREADY_STARTED("game.start.already_started"),
    GIVE_CARDS("game.start.give_cards"),
    NO_CARD("game.start.no_card"),
    SETTINGS_HOVER("game.start.chosen_settings"),
    DEATHMATCH_START("game.deathmatch.start"),
    DEATHMATCH_SEARCH("game.deathmatch.search"),
    DEATHMATCH_ITEM("game.deathmatch.item"),
    BINGO("game.end.bingo"),
    RESTART("game.end.restart"),
    LEAVE("game.player.leave"),
    NOT_STARTED("game.player.no_start"),
    NO_PLAYER_CARD("game.player.no_card"),
    COOLDOWN("game.player.cooldown"),
    RESPAWN("game.player.respawn"),
    RESPAWN_EXPIRED("game.player.respawn_expired"),
    NO_JOIN("game.team.no_join"),
    JOIN("game.team.join"),
    JOIN_AUTO("game.team.join_auto"),
    DROPPED("game.team.dropped"),
    POST_GAME_START("game.postgame.start"),
    NO_CHAT("game.team.no_chat"),
    CHAT_OFF("game.team.chat_off"),
    CHAT_ON("game.team.chat_on"),
    FULL_TEAM_DESC("game.team.full_team_desc"),
    JOIN_TEAM_DESC("game.team.join_team_desc"),
    TEAM_SIZE_CHANGED("game.team.size_changed"),
    VOIDED("game.item.voided"),
    HOTSWAP_EXPIRE("game.item.expire"),
    HOTSWAP_RECOVER("game.item.recover"),
    HOTSWAP_SINGLE_EXPIRED("game.item.single_expired"),
    HOTSWAP_SINGLE_ADDED("game.item.single_added"),
    HOTSWAP_MULTIPLE_EXPIRED("game.item.multiple_expired"),
    HOTSWAP_MULTIPLE_ADDED("game.item.multiple_added"),
    LORE_ITEM("game.item.lore"),
    LORE_ADVANCEMENT("game.item.lore_advancement"),
    LORE_STATISTIC("game.item.lore_statistic"),
    COMPLETED("game.item.completed"),
    COMPLETED_LORE("game.item.complete_lore"),
    MENU_SAVE("menu.save"),
    MENU_EXIT("menu.exit"),
    MENU_SAVE_EXIT("menu.save_exit"),
    MENU_ACCEPT("menu.accept"),
    MENU_NEXT("menu.next"),
    MENU_PREV("menu.prev"),
    MENU_FILTER("menu.filter"),
    MENU_CLEAR_FILTER("menu.clear"),
    GAME_SCOREBOARD_TITLE("menu.completed"),
    CARD_TITLE("menu.card.title"),
    INFO_REGULAR_NAME("menu.card.info_regular.name"),
    INFO_REGULAR_DESC("menu.card.info_regular.desc"),
    INFO_LOCKOUT_NAME("menu.card.info_lockout.name"),
    INFO_LOCKOUT_DESC("menu.card.info_lockout.desc"),
    INFO_COMPLETE_NAME("menu.card.info_complete.name"),
    INFO_COMPLETE_DESC("menu.card.info_complete.desc"),
    INFO_HOTSWAP_NAME("menu.card.info_hotswap.name"),
    INFO_HOTSWAP_DESC("menu.card.info_hotswap.desc"),
    INFO_HOTSWAP_COUNTDOWN("menu.card.info_hotswap.desc_alt"),
    OPTIONS_TITLE("menu.options.title"),
    OPTIONS_START("menu.options.start"),
    OPTIONS_END("menu.options.end"),
    OPTIONS_TEAM("menu.options.team"),
    OPTIONS_LEAVE("menu.options.leave"),
    OPTIONS_KIT("menu.options.kit"),
    OPTIONS_CARD("menu.options.card"),
    OPTIONS_GAMEMODE("menu.options.mode"),
    OPTIONS_EFFECTS("menu.options.effects"),
    OPTIONS_VOTE("menu.options.vote"),
    MODE_REGULAR("menu.options.modes.regular"),
    MODE_LOCKOUT("menu.options.modes.lockout"),
    MODE_COMPLETE("menu.options.modes.complete"),
    MODE_HOTSWAP("menu.options.modes.hotswap"),
    EFFECTS_ENABLE("menu.effects.enable"),
    EFFECTS_DISABLE("menu.effects.disable"),
    EFFECTS_ENABLED("menu.effects.enabled"),
    EFFECTS_DISABLED("menu.effects.disabled"),
    EFFECTS_NIGHT_VISION("menu.effects.night_vision"),
    EFFECTS_WATER_BREATH("menu.effects.water_breath"),
    EFFECTS_FIRE_RES("menu.effects.fire_res"),
    EFFECTS_NO_FALL_DMG("menu.effects.no_fall_dmg"),
    EFFECTS_SPEED("menu.effects.speed"),
    EFFECTS_NO_DURABILITY("menu.effects.no_durability"),
    EFFECTS_KEEP_INVENTORY("menu.effects.keep_inventory"),
    KIT_HARDCORE_NAME("menu.kits.hardcore.name"),
    KIT_HARDCORE_DESC("menu.kits.hardcore.desc"),
    KIT_NORMAL_NAME("menu.kits.normal.name"),
    KIT_NORMAL_DESC("menu.kits.normal.desc"),
    KIT_OVERPOWERED_NAME("menu.kits.overpowered.name"),
    KIT_OVERPOWERED_DESC("menu.kits.overpowered.desc"),
    KIT_RELOADED_NAME("menu.kits.reloaded.name"),
    KIT_RELOADED_DESC("menu.kits.reloaded.desc"),
    KIT_CUSTOM_NAME("menu.kits.custom"),
    CARD_ITEM_NAME("items.card.name"),
    CARD_ITEM_DESC("items.card.desc"),
    WAND_ITEM_NAME("items.wand.name"),
    WAND_ITEM_DESC("items.wand.desc"),
    VOTE_ITEM_NAME("items.vote.name"),
    VOTE_ITEM_DESC("items.vote.desc"),
    TEAM_ITEM_NAME("items.team.name"),
    TEAM_ITEM_DESC("items.team.desc"),
    TEAM_AUTO("teams.auto"),
    LIST_COUNT("creator.card_item.desc"),
    COUNT_MORE("menu.count_more"),
    ;

    private final String key;
    private String translation;
    private boolean dynamic;

    private static final Pattern HEX_PATTERN = Pattern.compile("\\{#[a-fA-F0-9]{6}}");
    private static final Pattern SMALL_CAPS_PATTERN = Pattern.compile("\\{@.+}");
    private static final Pattern SUBSTITUTE_PATTERN = Pattern.compile("\\{\\$(?<key>[\\w.]+)(\\((?<args>.+)\\))?}");

    private static final TagResolver SUBSTITUTE_RESOLVER = substituteResolver();

    BingoMessage(String key) {
        this.key = key;
        this.translation = key;
    }

    public static void setLanguage(FileConfiguration text, FileConfiguration fallbackText) {
        for (BingoMessage value : BingoMessage.values()) {
            value.translation = text.getString(value.key, fallbackText.getString(value.key, value.translation));
        }
    }

    public String rawTranslation() {
        return translation;
    }

    /**
     * @param input The input string, can look something like this: "{#00bb33}Hello, I like to &2&lDance && &rSing!"
     * @return Legacy text string that can be used in TextComponent#fromLegacyText
     */
    public static String convertColors(String input) {
        String part = input;
        return replaceColors(part, color -> "" + TextColor.fromHexString(color));
    }

    public static String convertSmallCaps(String input) {
        String part = input;
        Matcher matcher = SMALL_CAPS_PATTERN.matcher(part);
        while (matcher.find()) {
            String match = matcher.group();
            String result = match.replace("{@", "").replace("}", "");
            part = part.replace(match, TinyCaps.toTinyCaps(result));
        }

        return part;
    }

    //FIXME: test or refactor
    public static String convertSubstitution(String input, String... args) {
        String part = input;
        Matcher matcher = SUBSTITUTE_PATTERN.matcher(part);
        Set<String> matchedKeys = new HashSet<>();

        while (matcher.find()) {
            String match = matcher.group();
            String key = matcher.group("key");
            String path = key.replace("{$", "").replace("}", "");

            if (matchedKeys.contains(path)) {
                ConsoleMessenger.warn("Recursive translation substitution found on " + path + ". Please check your translation file. If this was not you, please report it.");
                break;
            }

            matchedKeys.add(path);

            String argsGroup = matcher.group("args");
            String[] addedArgs = new String[0];
            if (argsGroup != null) {
                addedArgs = argsGroup.split(",");
            }
            String[] allArgs = CollectionHelper.concatWithArrayCopy(args, addedArgs);

            BingoMessage actualTranslation = getByKey(path);
            if (actualTranslation == null) {
                //invalid key, remove brackets and continue...
                part = part.replace(key, path);
                continue;
            }
//            part = part.replace(match, actualTranslation.translate(allArgs));
        }

        return part;
    }

    public static BingoMessage getByKey(String key) {
        for (BingoMessage value : values()) {
            if (value.key.equals(key))
                return value;
        }
        return null;
    }

    /**
     * Convert input string to a minimessage string replacing color codes and small caps codes
     *
     * @return
     */
    public static String convertConfigString(String input) {
        String out = input;
        out = BingoMessage.convertColors(out);
        out = BingoMessage.convertSmallCaps(out);
        out = BingoMessage.convertSubstitution(out);
        return out;
    }

    /**
     * Translate and parse this config string and then send it to the player.
     * This method supports minimessage, placeholders and bingo reloaded formatting.
     *
     * @param audience
     */
    public void sendToAudience(Audience audience, TextColor color, List<TextDecoration> decorations, Component... withArguments) {
        //Untangle the mess before sending it.
        String translated = rawTranslation();


        audience.forEachAudience(innerAudience -> {
            //Translate and send in steps
            //1. Solve placeholders first (so they can be nested into arguments in the following formats).
            String playerMessage = translated;
            if (innerAudience instanceof Player player && BingoReloaded.PLACEHOLDER_API_ENABLED) {
                playerMessage = PlaceholderAPI.setPlaceholders(player, playerMessage);
            }

            Component[] components = configStringAsMultiline(playerMessage, color, withArguments);

            for (Component c : components) {
                innerAudience.sendMessage(BingoMessage.MESSAGE_PREFIX.asPhrase().append(c.decorate(decorations.toArray(TextDecoration[]::new))));
            }
        });
    }

    public void sendToAudience(Audience audience, Component... withArguments) {
        sendToAudience(audience, null, List.of(), withArguments);
    }

    public void sendToAudience(Audience audience, TextColor color, Component... withArguments) {
        sendToAudience(audience, color, List.of(), withArguments);
    }

    public void sendToAudience(Audience audience, List<TextDecoration> decorations, Component... withArguments) {
        sendToAudience(audience, null, decorations, withArguments);
    }

    public static Component createHoverCommandMessage(Component prefix, Component hoverable, HoverEvent hover, Component postfix, @NotNull String command) {
        return prefix.append(hoverable.clickEvent(ClickEvent.runCommand(command)).hoverEvent(hover)).append(postfix);
    }

    /**
     * Returns a multiline component, translated
     *
     * @param player
     * @param withArguments
     * @return
     */
    public Component[] convertForPlayer(Player player, Component... withArguments) {
        //1. Solve placeholders first (so they can be nested into arguments in the following formats).
        String playerMessage = rawTranslation();
        return convertForPlayer(playerMessage, player, withArguments);
    }

    public static Component[] convertForPlayer(String input, Player player, Component... withArguments) {
        if (BingoReloaded.PLACEHOLDER_API_ENABLED)
            input = PlaceholderAPI.setPlaceholders(player, input);

        return configStringAsMultiline(input, null, withArguments);
    }

    //TODO: find way to optimize phrases by only creating them on plugin load/ language change? (maybe save phrase w/o args in a map to return those instead?)
    /**
     * Phrases are interpreted without context (player) so placeholders and tags relying on targets cannot be used. Their result is stored in a single line, stripped of \n
     *
     * @return the phrased version of the translation as a component.
     */
    public Component asPhrase(Component... arguments) {
        String converted = String.join("", convertConfigStringToMini(rawTranslation()));
        // create tag resolvers for each argument, which will appear as <0>, <1> etc... in the mini message string and be replaced by the correct components.
        List<TagResolver> resolvers = new ArrayList<>();
        for (int i = 0; i < arguments.length; i++) {
            resolvers.add(Placeholder.component(Integer.toString(i), arguments[i]));
        }
        return PlayerDisplay.MINI_BUILDER.deserialize(converted, resolvers.toArray(TagResolver[]::new));
    }

    public Component[] asMultiline(TextColor color, Component... arguments) {
        return configStringAsMultiline(rawTranslation(), color, arguments);
    }

    public static Component[] configStringAsMultiline(String input, TextColor color, Component... arguments) {
        List<Component> result = new ArrayList<>();

        for (String converted : convertConfigStringToMini(input)) {
            // create tag resolvers for each argument, which will appear as <0>, <1> etc... in the mini message string and be replaced by the correct components.
            List<TagResolver> resolvers = new ArrayList<>();
            for (int i = 0; i < arguments.length; i++) {
                resolvers.add(Placeholder.component(Integer.toString(i), arguments[i]));
            }
            Component c = PlayerDisplay.MINI_BUILDER.deserialize(converted, resolvers.toArray(TagResolver[]::new));
            if (color != null) {
                result.add(c.color(color));
            } else {
                result.add(c);
            }
        }
        return result.toArray(Component[]::new);
    }

    public Component[] asMultiline(Component... arguments) {
        return asMultiline(null, arguments);
    }

    /**
     * Splits strings on \n and converts them to mini message format.
     *
     * @param message
     * @return
     */
    private static List<String> convertConfigStringToMini(String message) {
        String[] messages = message.split("\\n");
        return Arrays.stream(messages).map(line -> {
           return convertConfigStringToSingleMini(line);
        }).toList();
    }

    public static String convertConfigStringToSingleMini(String message) {
        // replace colors
        message = replaceColors(message, color -> "<" + color + ">");

        //NOTE: small caps and substitution can also be done by replacing it into minimessage tags, but doing it directly is probably faster.
        message = convertSmallCaps(message);
        message = convertSubstitution(message);

        message = message.replace("{", "<").replace("}", ">");
        return message;
    }

    /**
     * Replaces all instances of color formatted blocks by their converted values.
     * The way the colors are replaced is determined by the converter function.
     *
     * @param input     input string containing possibly multiple instances of colors to be converted
     * @param converter function to covert each found color string to the target formatted string.
     *                  Where the input is the color in hex string format "#FFFFFF" configured by the user.
     * @return the converted string (or the input string if no colors were found).
     */
    private static String replaceColors(String input, Function<String, String> converter) {
        Matcher matcher = HEX_PATTERN.matcher(input);

        String result = input;
        while (matcher.find()) {
            String match = matcher.group();
            String hexColor = match.substring(1, match.length() - 1);
            result = result.replace(match, converter.apply(hexColor));
        }

        return result;
    }

    //TODO: finish sub resolver

    /**
     * @return
     */
    private static TagResolver substituteResolver() {
        return TagResolver.resolver("sub", (args, ctx) -> {
//            ctx.deserialize()
            return null;
        });
    }
}
