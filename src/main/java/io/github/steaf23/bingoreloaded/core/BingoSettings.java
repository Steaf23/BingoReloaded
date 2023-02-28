package io.github.steaf23.bingoreloaded.core;

import io.github.steaf23.bingoreloaded.core.data.BingoCardsData;
import io.github.steaf23.bingoreloaded.core.data.ConfigData;
import io.github.steaf23.bingoreloaded.gui.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.core.cards.CardSize;
import io.github.steaf23.bingoreloaded.core.player.CustomKit;
import io.github.steaf23.bingoreloaded.core.player.PlayerKit;
import io.github.steaf23.bingoreloaded.util.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

import java.util.EnumSet;

public class BingoSettings
{
    public String card = ConfigData.instance.selectedCard;
    public BingoGamemode mode;
    public CardSize cardSize;
    public int cardSeed;
    public PlayerKit kit;
    public EnumSet<EffectOptionFlags> effects;
    public Material deathMatchItem;
    public int maxTeamSize;
    public boolean enableCountdown;
    public int countdownGameDuration;

    private final String worldName;

    public BingoSettings(String worldName)
    {
        this.worldName = worldName;

        this.card = ConfigData.instance.selectedCard;
        this.mode = BingoGamemode.REGULAR;
        this.cardSize = CardSize.X5;
        this.cardSeed = ConfigData.instance.cardSeed;
        this.kit = PlayerKit.OVERPOWERED;
        this.effects = kit.defaultEffects;
        this.maxTeamSize = ConfigData.instance.defaultTeamSize;
        this.countdownGameDuration = ConfigData.instance.defaultGameDuration;
        this.enableCountdown = false;
    }

    public Material generateDeathMatchItem()
    {
        return BingoCardsData.getRandomItemTask(card).material();
    }

    public void setKit(PlayerKit kit)
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
        new Message("game.settings.kit_selected").color(ChatColor.GOLD).arg(ChatColor.RESET + kitName).sendAll(worldName);
    }

    public void setEffects(EnumSet<EffectOptionFlags> effects)
    {
        this.effects = effects;
        new Message("game.settings.effects_selected").color(ChatColor.GOLD).sendAll(worldName);
    }
}
