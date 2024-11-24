package io.github.steaf23.bingoreloaded.gameloop.vote;

import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import org.bukkit.scoreboard.Score;
import org.jetbrains.annotations.Nullable;

import java.io.Console;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Each player can cast a single vote for all categories, To keep track of this a VoteTicket will be made for every player that votes on something
public class VoteTicket
{
    public static final VoteCategory<BingoGamemode> CATEGORY_GAMEMODE = new GamemodeCategory();
    public static final VoteCategory<PlayerKit> CATEGORY_KIT = new KitCategory();
    public static final VoteCategory<String> CATEGORY_CARD = new CardCategory();
    public static final VoteCategory<CardSize> CATEGORY_CARDSIZE = new CardSizeCategory();

    private final Map<VoteCategory<?>, String> votes = new HashMap<>();

    public boolean isEmpty() {
        return votes.isEmpty();
    }

    public boolean addVote(VoteCategory<?> category, String value) {
        if (category.getValidValues().contains(value)) {
            votes.put(category, value);
            return true;
        }
        return false;
    }

    public @Nullable String getVote(VoteCategory<?> category) {
        return votes.get(category);
    }

    public boolean containsCategory(VoteCategory<?> category) {
        return votes.containsKey(category);
    }

    public String toString() {
        List<String> result = new ArrayList<>();
        for (VoteCategory<?> category : votes.keySet()) {
            String part = category.getConfigName() + ": " + votes.get(category);
            result.add(part);
        }
        return String.join(", ", result);
    }

    public static VoteTicket getVoteResult(Collection<VoteTicket> tickets) {
        Map<VoteCategory<?>, Map<String, Integer>> maps = new HashMap<>();

        for (VoteTicket ticket : tickets) {
            for (VoteCategory<?> category : ticket.votes.keySet()) {
                Map<String, Integer> categoryMap = maps.getOrDefault(category, new HashMap<>());
                String vote = ticket.votes.get(category);
                categoryMap.put(vote, categoryMap.getOrDefault(vote, 0) + 1);
                maps.put(category, categoryMap);
            }
        }

        VoteTicket outcome = new VoteTicket();
        for (VoteCategory<?> category : maps.keySet()) {
            outcome.addVote(category, getVoteWithHighestCount(maps.get(category)));
        }

        return outcome;
    }

    private static String getVoteWithHighestCount(Map<String, Integer> values) {

        List<String> sortedCounts = new ArrayList<>(values.keySet().stream()
                .sorted(Comparator.comparingInt(a -> -values.get(a)))
                .toList());

        int recordCount = values.get(sortedCounts.getFirst());
        int currentCount = 0;
        int index = sortedCounts.size() - 1;
        for (int i = sortedCounts.size() - 1; i >= 0; i--) {
            currentCount = values.get(sortedCounts.get(index));
            if (recordCount == currentCount) {
                break;
            }
            sortedCounts.remove(index);
        }

        return sortedCounts.get((int)(Math.random() * sortedCounts.size()));
    }
}

