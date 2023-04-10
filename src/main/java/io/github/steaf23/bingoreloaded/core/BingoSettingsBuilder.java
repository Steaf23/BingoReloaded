package io.github.steaf23.bingoreloaded.core;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.core.cards.CardSize;
import io.github.steaf23.bingoreloaded.core.data.ConfigData;
import io.github.steaf23.bingoreloaded.core.player.CustomKit;
import io.github.steaf23.bingoreloaded.core.player.PlayerKit;
import io.github.steaf23.bingoreloaded.gui.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;
import net.md_5.bungee.api.ChatColor;

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
        this.card = "default_card";
        this.mode = BingoGamemode.REGULAR;
        this.cardSize = CardSize.X5;
        this.cardSeed = 0;
        this.kit = PlayerKit.OVERPOWERED;
        this.effects = kit.defaultEffects;
        this.maxTeamSize = 5;
        this.countdownGameDuration = 20;
        this.enableCountdown = false;
    }

    public static BingoSettingsBuilder fromConfig(BingoSession session)
    {
        ConfigData config = BingoReloaded.get().config();
        BingoSettingsBuilder settings = new BingoSettingsBuilder(session);
        settings.maxTeamSize = config.defaultTeamSize;
        settings.card = config.selectedCard;
        settings.cardSeed = config.cardSeed;
        settings.kit = PlayerKit.fromConfig(config.defaultKit);
        settings.effects = settings.kit.defaultEffects;
        settings.countdownGameDuration = config.defaultGameDuration;
        return settings;
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

    public BingoSettingsBuilder card(String card)
    {
        this.card = card;
        return this;
    }

    public BingoSettingsBuilder mode(BingoGamemode mode)
    {
        this.mode = mode;
        return this;
    }

    public BingoSettingsBuilder cardSize(CardSize cardSize)
    {
        this.cardSize = cardSize;
        return this;
    }

    public BingoSettingsBuilder cardSeed(int cardSeed)
    {
        this.cardSeed = cardSeed;
        return this;
    }

    public BingoSettingsBuilder kit(PlayerKit kit)
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
        new TranslatedMessage("game.settings.kit_selected").color(ChatColor.GOLD).arg(ChatColor.RESET + kitName).sendAll(session);
        return this;
    }

    public BingoSettingsBuilder effects(EnumSet<EffectOptionFlags> effects)
    {
        this.effects = effects;
        new TranslatedMessage("game.settings.effects_selected").color(ChatColor.GOLD).sendAll(session);
        return this;
    }

    public void toggleEffect(EffectOptionFlags effect, boolean enable)
    {
        if (enable)
            this.effects.add(effect);
        else
            this.effects.remove(effect);
    }

    public BingoSettingsBuilder maxTeamSize(int maxTeamSize)
    {
        this.maxTeamSize = maxTeamSize;
        return this;
    }

    public BingoSettingsBuilder enableCountdown(boolean enableCountdown)
    {
        this.enableCountdown = enableCountdown;
        return this;
    }

    public BingoSettingsBuilder countdownGameDuration(int countdownGameDuration)
    {
        this.countdownGameDuration = countdownGameDuration;
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
}
