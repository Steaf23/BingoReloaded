package io.github.steaf23.bingoreloaded.settings;

import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.BingoSettingsData;
import io.github.steaf23.bingoreloaded.event.BingoSettingsUpdatedEvent;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.phase.PregameLobby;
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
    private boolean enableCountdown;
    private int countdownGameDuration;
    private int hotswapGoal;

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
        this.enableCountdown = def.enableCountdown();
        this.hotswapGoal = def.hotswapGoal();
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
        enableCountdown = settings.enableCountdown();
        hotswapGoal = settings.hotswapGoal();
        settingsUpdated();
    }

    public BingoSettingsBuilder getVoteResult(PregameLobby.VoteTicket voteResult)
    {
        BingoSettingsBuilder resultBuilder = new BingoSettingsBuilder(session);
        resultBuilder.fromOther(view());

        String[] tuple = voteResult.gamemode.split("_");
        if (tuple.length != 2) {
            ConsoleMessenger.bug("Could not read vote results", this);
            return resultBuilder;
        }
        int cardWidth = 0;
        try {
            cardWidth = Integer.parseInt(tuple[1]);
        } catch (NumberFormatException e) {
            ConsoleMessenger.bug("Could not read card size", this);
        }
        if (cardWidth == 0) {
            return resultBuilder;
        }

        BingoGamemode mode = BingoGamemode.fromDataString(tuple[0]);
        CardSize size = CardSize.fromWidth(cardWidth);

        resultBuilder.mode = mode;
        resultBuilder.cardSize = size;

        if (!voteResult.kit.isEmpty())
            resultBuilder.kit = PlayerKit.fromConfig(voteResult.kit);

        if (!voteResult.kit.isEmpty() && new BingoCardData().getCardNames().contains(voteResult.card))
            resultBuilder.card = voteResult.card;

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
        if (this.effects.equals(effects)) {
            this.effects = effects;
            settingsUpdated();
        }
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

    public BingoSettingsBuilder enableCountdown(boolean enableCountdown)
    {
        if (this.enableCountdown != enableCountdown) {
            this.enableCountdown = enableCountdown;
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

    public BingoSettingsBuilder hotswapGoal(int hotswapGoal) {
        if (this.hotswapGoal != hotswapGoal) {
            this.hotswapGoal = hotswapGoal;
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
                enableCountdown,
                countdownGameDuration,
                hotswapGoal);
    }

    public void settingsUpdated()
    {
        var event = new BingoSettingsUpdatedEvent(view(), session);
        Bukkit.getPluginManager().callEvent(event);
    }
}
