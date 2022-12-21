package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.data.BingoCardsData;
import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.gui.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.gui.cards.CardSize;
import io.github.steaf23.bingoreloaded.player.PlayerKit;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

import java.util.EnumSet;

public class BingoGameSettings
{
    public String card = ConfigData.instance.selectedCard;
    public BingoGamemode mode;
    public CardSize cardSize;
    public PlayerKit kit;
    public EnumSet<EffectOptionFlags> effects;
    public Material deathMatchItem;

    public BingoGameSettings()
    {
        this.card = ConfigData.instance.selectedCard;
        this.mode = BingoGamemode.REGULAR;
        this.cardSize = CardSize.X5;
        this.kit = PlayerKit.OVERPOWERED;
        this.effects = kit.defaultEffects;
    }

    public Material generateDeathMatchItem()
    {
        return BingoCardsData.getRandomItemTask(card).item.getType();
    }

    public void setKit(PlayerKit kit)
    {
        this.kit = kit;
        new Message("game.settings.kit_selected").color(ChatColor.GOLD).arg(kit.displayName).sendAll();
    }

    public void setEffects(EnumSet<EffectOptionFlags> effects)
    {
        this.effects = effects;
        new Message("game.settings.effects_selected").color(ChatColor.GOLD).sendAll();
    }
}
