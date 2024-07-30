package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.util.CollectionHelper;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.easymenulib.util.ChatComponentUtils;
import io.github.steaf23.easymenulib.util.SmallCaps;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum BingoTranslation
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

    private static final Pattern HEX_PATTERN = Pattern.compile("\\{#[a-fA-F0-9]{6}\\}");
    private static final Pattern SMALL_CAPS_PATTERN = Pattern.compile("\\{@.+\\}");
    private static final Pattern SUBSTITUTE_PATTERN = Pattern.compile("\\{\\$(?<key>[\\w.]+)(\\((?<args>.+)\\))?\\}");

    BingoTranslation(String key) {
        this.key = key;
        this.translation = ChatColor.GRAY + key;
    }

    public static void setLanguage(FileConfiguration text, FileConfiguration fallbackText) {
        for (BingoTranslation value : BingoTranslation.values()) {
            value.translation = text.getString(value.key, fallbackText.getString(value.key, value.translation));
        }
    }

    public String translate(String... args) {
        String rawTranslation = BingoTranslation.convertConfigString(translation);

        for (int i = 0; i < args.length; i++) {
            rawTranslation = rawTranslation.replace("{" + i + "}", args[i]);
        }
        return rawTranslation;
    }

    public String rawTranslation() {
        return translation;
    }

    /**
     * convert translated string with arguments to ItemText and preserve argument order, like translate() does
     *
     * @param args
     * @return An array of itemText where each element is a line,
     * where each line is split using '\n' in the translated string.
     */
    public BaseComponent[] asComponent(Set<ChatColor> modifiers, boolean useSmallCaps, BaseComponent... args) {
        //TODO: fix issue where raw translations cannot convert the colors defined in lang files properly on items
        String rawTranslation = translation;
        rawTranslation = convertColors(rawTranslation);
        TextComponent.fromLegacyText(rawTranslation);

        List<BaseComponent> result = new ArrayList<>();
        String[] lines = rawTranslation.split("\\n");
        String[] pieces;
        for (int i = 0; i < lines.length; i++) {
            ComponentBuilder lineBuilder = ChatComponentUtils.formattedBuilder(modifiers.toArray(new ChatColor[]{}));
            pieces = lines[i].split("\\{");
            for (String piece : pieces) {
                String pieceToAdd = piece;
                for (int argIdx = 0; argIdx < args.length; argIdx++) {
                    if (pieceToAdd.contains(argIdx + "}")) {
                        lineBuilder.append(args[argIdx]);
                        pieceToAdd = pieceToAdd.replace(i + "}", "");
                        break;
                    }
                }
                if (useSmallCaps)
                    lineBuilder.append(ChatComponentUtils.smallCaps(pieceToAdd));
                else
                    lineBuilder.append(pieceToAdd);
            }
            result.add(lineBuilder.build());
        }
        return result.toArray(new BaseComponent[]{});
    }

    public BaseComponent[] asComponent(Set<ChatColor> modifiers, BaseComponent... args) {
        return asComponent(modifiers, false, args);
    }

    /**
     * @param input The input string, can look something like this: "{#00bb33}Hello, I like to &2&lDance && &rSing!"
     * @return Legacy text string that can be used in TextComponent#fromLegacyText
     */
    public static String convertColors(String input) {
        String part = input;
        part = part.replaceAll("(?<!&)&(?!&)", "§");
        part = part.replaceAll("&&", "&");

        Matcher matcher = HEX_PATTERN.matcher(part);
        while (matcher.find()) {
            String match = matcher.group();
            String color = match.replaceAll("[\\{\\}]", "");
            part = part.replace(match, "" + net.md_5.bungee.api.ChatColor.of(color));
        }

        return part;
    }

    public static String convertSmallCaps(String input) {
        String part = input;
        Matcher matcher = SMALL_CAPS_PATTERN.matcher(part);
        while (matcher.find()) {
            String match = matcher.group();
            String result = match.replace("{@", "").replace("}", "");
            part = part.replace(match, SmallCaps.toSmallCaps(result));
        }

        return part;
    }

    public static String convertSubstitution(String input, String... args) {
        String part = input;
        Matcher matcher = SUBSTITUTE_PATTERN.matcher(part);
        Set<String> matchedKeys = new HashSet<>();

        while (matcher.find()) {
            String match = matcher.group();
            String key = matcher.group("key");
            String path = key.replace("{$", "").replace("}", "");

            if (matchedKeys.contains(path)) {
                Message.warn("recursive translation substitution found on " + path);
                break;
            }

            matchedKeys.add(path);

            String argsGroup = matcher.group("args");
            String[] addedArgs = new String[0];
            if (argsGroup != null) {
                addedArgs = argsGroup.split(",");
            }
            String[] allArgs = CollectionHelper.concatWithArrayCopy(args, addedArgs);

            BingoTranslation actualTranslation = getByKey(path);
            if (actualTranslation == null) {
                //invalid key, remove brackets and continue...
                part = part.replace(key, path);
                continue;
            }
            part = part.replace(match, actualTranslation.translate(allArgs));
        }

        return part;
    }

    public static BingoTranslation getByKey(String key) {
        for (BingoTranslation value : values()) {
            if (value.key.equals(key))
                return value;
        }
        return null;
    }

    /**
     * Convert input string to a presentable string replacing color codes and small caps codes
     *
     * @return
     */
    public static String convertConfigString(String input) {
        String out = input;
        out = BingoTranslation.convertColors(out);
        out = BingoTranslation.convertSmallCaps(out);
        out = BingoTranslation.convertSubstitution(out);
        return out;
    }
}
