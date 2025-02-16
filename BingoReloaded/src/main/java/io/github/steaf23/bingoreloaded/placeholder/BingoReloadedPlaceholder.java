package io.github.steaf23.bingoreloaded.placeholder;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public enum BingoReloadedPlaceholder {
    // plugin information
    CREATED_SESSION("created_session_"),
    COUNT_SESSION_PLAYERS("count_session_players_"),

    //current match information
    TEAM_FULL("team_full"),
    TEAM_COLOR("team_color"),
    TEAM_NAME("team_name"),
    CURRENT_TASKS_TEAM("current_tasks_team"),
    CURRENT_TASKS_PLAYER("current_tasks_player"),
    CURRENT_TIME("current_time"),
    GAME_STATUS("game_status"),

    // current settings information
    SETTING_GAMEMODE("setting_gamemode"),
    SETTING_CARDSIZE("setting_cardsize"),
    SETTING_CARDNAME("setting_cardname"),
    SETTING_KIT("setting_kit"),
    SETTING_DURATION("setting_duration"),
    SETTING_EFFECTS("setting_effects"),
    SETTING_HOTSWAP_WINSCORE("setting_hotswap_winscore"),
    SETTING_HOTSWAP_EXPIRE("setting_hotswap_expire"),
    SETTING_COMPLETE_WINSCORE("setting_complete_winscore"),
    SETTING_SEED("setting_seed"),
    SETTING_TEAMSIZE("setting_teamsize"),
    SETTING_SEPARATE_CARDS("setting_separate_cards"),

    // player lifetime information
    SESSION_NAME("session_name"),
    GAMES_WINS("games_won"),
    GAMES_LOSSES("games_lost"),
    GAMES_PLAYED("games_played"),
    TASKS_COMPLETED("tasks_completed"),
    TASK_COMPLETED_RECORD("tasks_completed_record"),
    ITEM_USES_WAND("item_uses_wand");

    private final String placeholderName;

    BingoReloadedPlaceholder(String placeholderName) {
        this.placeholderName = placeholderName;
    }

    public static @Nullable BingoReloadedPlaceholder fromString(String name) {
        if (name.startsWith("created_session_")) {
            return BingoReloadedPlaceholder.CREATED_SESSION;
        }
        return Arrays.stream(values()).filter(p -> p.placeholderName.equals(name)).findFirst().orElse(null);
    }

    public String getName() {
        return placeholderName;
    }
}
