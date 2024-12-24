package io.github.steaf23.bingoreloaded.settings;

import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.data.BingoSettingsData;
import io.github.steaf23.bingoreloaded.event.BingoSettingsUpdatedEvent;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.vote.VoteTicket;
import io.github.steaf23.bingoreloaded.gui.inventory.EffectOptionFlags;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import org.bukkit.Bukkit;

import java.util.EnumSet;
import java.util.Objects;

public class BingoSettingsBuilder
{
    private final BingoSession session;
    private String card;
    private BingoGamemode mode;
    private CardSize cardSize;
    private int cardSeed;
    private PlayerKit kit;
    private EnumSet<EffectOptionFlags> effects;
    private int maxTeamSize;
    private BingoSettings.CountdownType countdownType;
    private int countdownGameDuration;
    private int hotswapGoal;
    private boolean expireHotswapTasks;
    private int completeGoal;
    private boolean differentCardPerTeam;

    public BingoSettingsBuilder(BingoSession session)
    {
        this.session = session;

        BingoSettings def = new BingoSettingsData().getDefaultSettings();
        if (def == null) {
            ConsoleMessenger.error("Could not find default settings, make sure you have at least 1 existing settings preset and its set to be the default settings!");
            return;
        }
        this.card = def.card();
        this.mode = def.mode();
        this.cardSize = def.size();
        this.cardSeed = def.seed();
        this.kit = def.kit();
        this.effects = def.effects();
        this.maxTeamSize = def.maxTeamSize();
        this.countdownGameDuration = def.countdownDuration();
        this.countdownType = def.countdownType();
        this.hotswapGoal = def.hotswapGoal();
        this.completeGoal = def.completeGoal();
        this.differentCardPerTeam = def.differentCardPerTeam();
    }

    public void fromOther(BingoSettings settings)
    {
        card = settings.card();
        mode = settings.mode();
        cardSize = settings.size();
        cardSeed = settings.seed();
        kit = settings.kit();
        effects = settings.effects();
        maxTeamSize = settings.maxTeamSize();
        countdownGameDuration = settings.countdownDuration();
        countdownType = settings.countdownType();
        hotswapGoal = settings.hotswapGoal();
        completeGoal = settings.completeGoal();
        differentCardPerTeam = settings.differentCardPerTeam();
        settingsUpdated();
    }

    public BingoSettingsBuilder applyVoteResult(VoteTicket voteResult)
    {
        BingoSettingsBuilder resultBuilder = new BingoSettingsBuilder(session);
        resultBuilder.fromOther(view());

        BingoGamemode newMode = VoteTicket.CATEGORY_GAMEMODE.getValidResultOrNull(voteResult);
        if (newMode != null)
        {
            resultBuilder.mode = newMode;
        }

        PlayerKit newKit = VoteTicket.CATEGORY_KIT.getValidResultOrNull(voteResult);
        if (newKit != null)
        {
            resultBuilder.kit = newKit;
        }

        String newCard = VoteTicket.CATEGORY_CARD.getValidResultOrNull(voteResult);
        if (newCard != null)
        {
            resultBuilder.card = newCard;
        }

        CardSize newCardsize = VoteTicket.CATEGORY_CARDSIZE.getValidResultOrNull(voteResult);
        if (newCardsize != null)
        {
            resultBuilder.cardSize = newCardsize;
        }

        return resultBuilder;
    }

    public BingoSettingsBuilder card(String card)
    {
        if (!Objects.equals(this.card, card)) {
            this.card = card;
            settingsUpdated();
        }
        return this;
    }

    public BingoSettingsBuilder mode(BingoGamemode mode)
    {
        if (this.mode != mode) {
            this.mode = mode;
            settingsUpdated();
        }
        return this;
    }

    public BingoSettingsBuilder cardSize(CardSize cardSize)
    {
        if (this.cardSize != cardSize) {
            this.cardSize = cardSize;
            // reset complete goal to remain full card completion settings by default
            this.completeGoal = cardSize.fullCardSize;
            settingsUpdated();
        }
        return this;
    }

    public BingoSettingsBuilder cardSeed(int cardSeed)
    {
        if (this.cardSeed != cardSeed) {
            this.cardSeed = cardSeed;
            settingsUpdated();
        }
        return this;
    }

    public BingoSettingsBuilder kit(PlayerKit kit)
    {
        if (this.kit != kit) {
            this.kit = kit;
            settingsUpdated();
        }
        return this;
    }

    public BingoSettingsBuilder effects(EnumSet<EffectOptionFlags> effects)
    {
        this.effects = effects;
        settingsUpdated();
        return this;
    }

    public BingoSettingsBuilder toggleEffect(EffectOptionFlags effect, boolean enable)
    {
        if (enable)
            this.effects.add(effect);
        else
            this.effects.remove(effect);
        settingsUpdated();
        return this;
    }

    public BingoSettingsBuilder maxTeamSize(int maxTeamSize)
    {
        if (this.maxTeamSize != maxTeamSize) {
            this.maxTeamSize = maxTeamSize;
            settingsUpdated();
        }
        return this;
    }

    public BingoSettingsBuilder countdownType(BingoSettings.CountdownType countdownType)
    {
        if (this.countdownType != countdownType) {
            this.countdownType = countdownType;
            settingsUpdated();
        }
        return this;
    }

    public BingoSettingsBuilder countdownGameDuration(int countdownGameDuration)
    {
        if (this.countdownGameDuration != countdownGameDuration) {
            this.countdownGameDuration = countdownGameDuration;
            settingsUpdated();
        }
        return this;
    }

    public BingoSettingsBuilder expireHotswapTasks(boolean expireHotswapTasks) {
        if (this.expireHotswapTasks != expireHotswapTasks) {
            this.expireHotswapTasks = expireHotswapTasks;
            settingsUpdated();
        }
        return this;
    }

    public BingoSettingsBuilder hotswapGoal(int hotswapGoal) {
        if (this.hotswapGoal != hotswapGoal) {
            this.hotswapGoal = hotswapGoal;
            settingsUpdated();
        }
        return this;
    }

    public BingoSettingsBuilder completeGoal(int completeGoal) {
        if (this.completeGoal != completeGoal) {
            // clamp completeGoal based on card size as a goal of 25 on a small card is not possible...
            this.completeGoal = Math.min(completeGoal, cardSize.fullCardSize);
            settingsUpdated();
        }
        return this;
    }

    public BingoSettingsBuilder differentCardPerTeam(boolean value) {
        if (this.differentCardPerTeam != value) {
            this.differentCardPerTeam = value;
            settingsUpdated();
        }
        return this;
    }

    public BingoSettings view()
    {
        return new BingoSettings(
                card,
                mode,
                cardSize,
                cardSeed,
                kit,
                effects,
                maxTeamSize,
                countdownType,
                countdownGameDuration,
                hotswapGoal,
                expireHotswapTasks,
                completeGoal,
                differentCardPerTeam);
    }

    public void settingsUpdated()
    {
        var event = new BingoSettingsUpdatedEvent(view(), session);
        Bukkit.getPluginManager().callEvent(event);
    }
}
