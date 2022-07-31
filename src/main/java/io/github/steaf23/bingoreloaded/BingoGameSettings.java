package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.data.BingoCardsData;
import io.github.steaf23.bingoreloaded.gui.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.gui.cards.CardSize;
import io.github.steaf23.bingoreloaded.player.PlayerKit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.EnumSet;

public class BingoGameSettings
{
    public String card;
    public BingoGameMode mode;
    public CardSize cardSize;
    public PlayerKit kit;
    public EnumSet<EffectOptionFlags> effects;
    public Material deathMatchItem;

    public BingoGameSettings()
    {
        this.card = "default_card";
        this.mode = BingoGameMode.REGULAR;
        this.cardSize = CardSize.X5;
        this.kit = PlayerKit.OVERPOWERED;
        this.effects = kit.defaultEffects;
    }

    public Material generateDeathMatchItem()
    {
        return BingoCardsData.getRandomItemSlot(card).item.getType();
    }

    public void setKit(PlayerKit kit)
    {
        this.kit = kit;
        BingoReloaded.broadcast(ChatColor.GOLD + "Selected " + kit.displayName + ChatColor.GOLD + " player kit!");
    }

    public void setEffects(EnumSet<EffectOptionFlags> effects)
    {
        this.effects = effects;
        BingoReloaded.broadcast(ChatColor.GOLD + "Selected Effect options, view them in the /bingo options!");
    }
}
