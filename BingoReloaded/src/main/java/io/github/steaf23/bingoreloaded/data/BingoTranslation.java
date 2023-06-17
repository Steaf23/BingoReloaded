package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.item.ItemText;
import io.github.steaf23.bingoreloaded.util.SmallCaps;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum BingoTranslation
{
    UNIMPLEMENTED("UNIMPLEMENTED MESSAGE"),
    LANGUAGE_NAME("name"),
    CHANGED_LANGUAGE("changed"),
    COMMAND_USAGE("command.use"),
    TP_NO_TEAM("command.tp.no_team"),
    TP_USAGE("command.tp.usage"),
    TP_NOT_PLAYER("command.tp.not_player"),
    TP_NOT_TEAMMATE("command.tp.not_teammate"),
    NO_DEATHMATCH("command.bingo.no_deathmatch"),
    DURATION("game.timer.duration"),
    TIME_LEFT("game.timer.time_left"),
    KIT_SELECTED("game.settings.kit_selected"),
    EFFECTS_SELECTED("game.settings.effects_selected"),
    REGULAR_SELECTED("game.settings.regular_selected"),
    LOCKOUT_SELECTED("game.settings.lockout_selected"),
    COMPLETE_SELECTED("game.settings.complete_selected"),
    CARD_SELECTED("game.settings.card_selected"),
    CARDSIZE_SELECTED("game.settings.cardsize"),
    NO_PLAYERS("game.start.no_players"),
    ALREADY_STARTED("game.start.already_started"),
    GIVE_CARDS("game.start.give_cards"),
    NO_CARD("game.start.no_card"),
    BINGO("game.end.bingo"),
    RESTART("game.end.restart"),
    LEAVE("game.player.leave"),
    NOT_STARTED("game.player.no_start"),
    NO_PLAYER_CARD("game.player.no_card"),
    COOLDOWN("game.player.cooldown"),
    RESPAWN("game.player.respawn"),
    NO_JOIN("game.team.no_join"),
    JOIN("game.team.join"),
    JOIN_AUTO("game.team.join_auto"),
    DROPPED("game.team.dropped"),
    VOIDED("game.team.voided"),
    NO_CHAT("game.team.no_chat"),
    CHAT_OFF("game.team.chat_off"),
    CHAT_ON("game.team.chat_on"),
    FULL_TEAM_DESC("game.team.full_team_desc"),
    JOIN_TEAM_DESC("game.team.join_team_desc"),
    TEAM_SIZE_CHANGED("game.team.size_changed"),
    LORE_ITEM("game.item.lore"),
    LORE_ADVANCEMENT("game.item.lore_advancement"),
    LORE_STATISTIC("game.item.lore_statistic"),
    COMPLETED("game.item.completed"),
    COMPLETED_LORE("game.item.complete_lore"),
    DEATHMATCH("game.item.deathmatch"),
    MENU_SAVE("menu.save"),
    MENU_EXIT("menu.exit"),
    MENU_SAVE_EXIT("menu.save_exit"),
    MENU_ACCEPT("menu.accept"),
    MENU_NEXT("menu.next"),
    MENU_PREV("menu.prev"),
    MENU_FILTER("menu.filter"),
    MENU_CLEAR_FILTER("menu.clear"),
    SCOREBOARD_TITLE("menu.completed"),
    CARD_TITLE("menu.card.title"),
    INFO_REGULAR_NAME("menu.card.info_regular.name"),
    INFO_REGULAR_DESC("menu.card.info_regular.desc"),
    INFO_LOCKOUT_NAME("menu.card.info_lockout.name"),
    INFO_LOCKOUT_DESC("menu.card.info_lockout.desc"),
    INFO_COMPLETE_NAME("menu.card.info_complete.name"),
    INFO_COMPLETE_DESC("menu.card.info_complete.desc"),
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
    TEAM_BROWN("teams.brown"),
    TEAM_ORANGE("teams.orange"),
    TEAM_PINK("teams.pink"),
    TEAM_RED("teams.red"),
    TEAM_WHITE("teams.white"),
    TEAM_LIME("teams.lime"),
    TEAM_GREEN("teams.green"),
    TEAM_GRAY("teams.gray"),
    TEAM_LIGHT_GRAY("teams.light_gray"),
    TEAM_BLACK("teams.black"),
    TEAM_YELLOW("teams.yellow"),
    TEAM_MAGENTA("teams.magenta"),
    TEAM_CYAN("teams.cyan"),
    TEAM_BLUE("teams.blue"),
    TEAM_PURPLE("teams.purple"),
    TEAM_LIGHT_BLUE("teams.light_blue"),
    LIST_COUNT("creator.card_item.desc"),
    ;

    private final String key;
    private String translation;

    private static final Pattern HEX_PATTERN = Pattern.compile("\\{#[a-fA-F0-9]{6}\\}");
    private static final Pattern SMALL_CAPS_PATTERN = Pattern.compile("\\{@.+\\}");

    BingoTranslation(String key)
    {
        this.key = key;
        this.translation = ChatColor.GRAY + key;
    }

    public static void setLanguage(FileConfiguration text, FileConfiguration fallbackText)
    {
        for (BingoTranslation value : BingoTranslation.values())
        {
            value.translation = text.getString(value.key, fallbackText.getString(value.key, value.translation));
        }
    }

    public String translate(String... args)
    {
        String rawTranslation = translation;
        rawTranslation = convertColors(rawTranslation);
        rawTranslation = convertSmallCaps(rawTranslation);

        for (int i = 0; i < args.length; i++)
        {
            rawTranslation = rawTranslation.replace("{" + i + "}", args[i]);
        }
        return rawTranslation;
    }

    public String rawTranslation()
    {
        return translation;
    }

    /**
     * convert translated string with arguments to ItemText and preserve argument order, like translate() does
     * @param args
     * @return An array of itemText where each element is a line,
     *  where each line is split using '\n' in the translated string.
     */
    public ItemText[] asItemText(Set<ChatColor> modifiers, boolean useSmallCaps, ItemText... args)
    {
        //TODO: fix issue where raw translations cannot convert the colors defined in lang files properly on items
        String rawTranslation = translation;
        rawTranslation = convertColors(rawTranslation);
        TextComponent.fromLegacyText(rawTranslation);

        List<ItemText> result = new ArrayList<>();
        String[] lines = rawTranslation.split("\\n");
        String[] pieces;
        for (int i = 0; i < lines.length; i++)
        {
            ItemText line = new ItemText(modifiers.toArray(new ChatColor[]{}));
            pieces = lines[i].split("\\{");
            for (String piece : pieces)
            {
                String pieceToAdd = piece;
                for (int argIdx = 0; argIdx < args.length; argIdx++)
                {
                    if (pieceToAdd.contains(argIdx + "}"))
                    {
                        line.add(args[argIdx]);
                        pieceToAdd = pieceToAdd.replace(i + "}", "");
                        break;
                    }
                }
                if (useSmallCaps)
                    line.addSmallCapsText(pieceToAdd);
                else
                    line.addText(pieceToAdd);
            }
            result.add(line);
        }
        return result.toArray(new ItemText[]{});
    }

    public ItemText[] asItemText(Set<ChatColor> modifiers, ItemText... args)
    {
        return asItemText(modifiers, false, args);
    }

    /**
     * @param input The input string, can look something like this: "{#00bb33}Hello, I like to &2&lDance && &rSing!"
     * @return Legacy text string that can be used in TextComponent#fromLegacyText
     */
    public static String convertColors(String input)
    {
        String part = input;
        part = part.replaceAll("(?<!&)&(?!&)", "§");
        part = part.replaceAll("&&", "&");

        Matcher matcher = HEX_PATTERN.matcher(part);
        while (matcher.find())
        {
            String match = matcher.group();
            String color = match.replaceAll("[\\{\\}]", "");
            part = part.replace(match, "" + net.md_5.bungee.api.ChatColor.of(color));
        }

        return part;
    }

    public static String convertSmallCaps(String input)
    {
        String part = input;
        Matcher matcher = SMALL_CAPS_PATTERN.matcher(part);
        while (matcher.find())
        {
            String match = matcher.group();
            String result = match.replace("{@", "").replace("}", "");
            part = part.replace(match, SmallCaps.toSmallCaps(result));
        }

        return part;
    }

    public static BingoTranslation getByKey(String key)
    {
        for (BingoTranslation value : values())
        {
            if (value.key.equals(key))
                return value;
        }
        return null;
    }
}
