package io.github.steaf23.bingoreloaded.core;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.core.cards.CardSize;
import io.github.steaf23.bingoreloaded.core.data.BingoCardsData;
import io.github.steaf23.bingoreloaded.core.data.ConfigData;
import io.github.steaf23.bingoreloaded.core.player.CustomKit;
import io.github.steaf23.bingoreloaded.core.player.PlayerKit;
import io.github.steaf23.bingoreloaded.gui.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

import java.util.EnumSet;

public class BingoSettings
{
    public String card;
    public BingoGamemode mode;
    public CardSize cardSize;
    public int cardSeed;
    public PlayerKit kit;
    public EnumSet<EffectOptionFlags> effects;
    public Material deathMatchItem;
    public final int maxTeamSize;
    public boolean enableCountdown;
    public int countdownGameDuration;

    public BingoSettings(int maxTeamSize)
    {
        ConfigData config = BingoReloaded.get().config();
        this.card = config.selectedCard;
        this.mode = BingoGamemode.REGULAR;
        this.cardSize = CardSize.X5;
        this.cardSeed = config.cardSeed;
        this.kit = PlayerKit.fromConfig(config.defaultKit);
        this.effects = kit.defaultEffects;
        this.maxTeamSize = config.defaultTeamSize;
        this.countdownGameDuration = config.defaultGameDuration;
        this.enableCountdown = false;
    }

    public Material generateDeathMatchItem()
    {
        BingoCardsData cardsData = new BingoCardsData();
        return cardsData.getRandomItemTask(card).material();
    }

    public void setKit(PlayerKit kit, BingoSession session)
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
    }

    public void setEffects(EnumSet<EffectOptionFlags> effects, BingoSession session)
    {
        this.effects = effects;
        new TranslatedMessage("game.settings.effects_selected").color(ChatColor.GOLD).sendAll(session);
    }
}
