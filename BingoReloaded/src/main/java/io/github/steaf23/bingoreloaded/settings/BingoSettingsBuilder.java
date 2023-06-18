package io.github.steaf23.bingoreloaded.settings;

import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.event.BingoSettingsUpdatedEvent;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.PregameLobby;
import io.github.steaf23.bingoreloaded.gui.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import java.util.EnumSet;

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

    public BingoSettingsBuilder(BingoSession session)
    {
        this.session = session;

        BingoSettings def = BingoSettings.getDefaultSettings();
        this.card = def.card();
        this.mode = def.mode();
        this.cardSize = def.size();
        this.cardSeed = def.seed();
        this.kit = def.kit();
        this.effects = def.effects();
        this.maxTeamSize = def.maxTeamSize();
        this.countdownGameDuration = def.countdownDuration();
        this.enableCountdown = def.enableCountdown();
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
    }

    public BingoSettingsBuilder getVoteResult(PregameLobby.VoteTicket voteResult)
    {
        BingoSettingsBuilder resultBuilder = new BingoSettingsBuilder(session);
        resultBuilder.fromOther(view());

        switch (voteResult.gamemode)
        {
            case "regular_3" -> {
                resultBuilder.cardSize = CardSize.X3;
                resultBuilder.mode = BingoGamemode.REGULAR;
            }
            case "regular_5" -> {
                resultBuilder.cardSize = CardSize.X5;
                resultBuilder.mode = BingoGamemode.REGULAR;
            }
            case "complete_3" -> {
                resultBuilder.cardSize = CardSize.X3;
                resultBuilder.mode = BingoGamemode.COMPLETE;
            }
            case "complete_5" -> {
                resultBuilder.cardSize = CardSize.X5;
                resultBuilder.mode = BingoGamemode.COMPLETE;
            }
            case "lockout_3" -> {
                resultBuilder.cardSize = CardSize.X3;
                resultBuilder.mode = BingoGamemode.LOCKOUT;
            }
            case "lockout_5" -> {
                resultBuilder.cardSize = CardSize.X5;
                resultBuilder.mode = BingoGamemode.LOCKOUT;
            }
        }

        if (!voteResult.kit.isEmpty())
            resultBuilder.kit = PlayerKit.fromConfig(voteResult.kit);

        if (!voteResult.kit.isEmpty() && new BingoCardData().getCardNames().contains(voteResult.card))
            resultBuilder.card = voteResult.card;

        return resultBuilder;
    }

    public BingoSettingsBuilder card(String card)
    {
        this.card = card;
        settingsUpdated();
        return this;
    }

    public BingoSettingsBuilder mode(BingoGamemode mode)
    {
        this.mode = mode;
        settingsUpdated();
        return this;
    }

    public BingoSettingsBuilder cardSize(CardSize cardSize)
    {
        this.cardSize = cardSize;
        settingsUpdated();
        return this;
    }

    public BingoSettingsBuilder cardSeed(int cardSeed)
    {
        this.cardSeed = cardSeed;
        settingsUpdated();
        return this;
    }

    public BingoSettingsBuilder kit(PlayerKit kit, BingoSession session)
    {
        this.kit = kit;
        String kitName = switch (kit)
                {
                    case CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5 -> {
                        CustomKit customKit = PlayerKit.getCustomKit(kit);
                        yield customKit == null ? kit.displayName : customKit.getName();
                    }
                    default -> kit.displayName;
                };
        new TranslatedMessage(BingoTranslation.KIT_SELECTED).color(ChatColor.GOLD).arg(ChatColor.RESET + kitName).sendAll(session);
        settingsUpdated();
        return this;
    }

    public BingoSettingsBuilder effects(EnumSet<EffectOptionFlags> effects, BingoSession session)
    {
        this.effects = effects;
        new TranslatedMessage(BingoTranslation.EFFECTS_SELECTED).color(ChatColor.GOLD).sendAll(session);
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
        this.maxTeamSize = maxTeamSize;
        settingsUpdated();
        return this;
    }

    public BingoSettingsBuilder enableCountdown(boolean enableCountdown)
    {
        this.enableCountdown = enableCountdown;
        settingsUpdated();
        return this;
    }

    public BingoSettingsBuilder countdownGameDuration(int countdownGameDuration)
    {
        this.countdownGameDuration = countdownGameDuration;
        settingsUpdated();
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
                countdownGameDuration);
    }

    public void settingsUpdated()
    {
        var event = new BingoSettingsUpdatedEvent(view(), session);
        Bukkit.getPluginManager().callEvent(event);
    }
}
