package me.steven.bingoreloaded.cardcreator.GUI;

import me.steven.bingoreloaded.GUIInventories.AbstractGUIInventory;
import me.steven.bingoreloaded.cardcreator.CardEntry;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class CardEditorUI extends AbstractGUIInventory
{
    public CardEditorUI(CardEntry card)
    {
        super(54, "Editing '" + card.name + "'");
        this.card = card;
    }

    @Override
    public void delegateClick(InventoryClickEvent event, ItemStack itemClicked, Player player)
    {

    }

    private final CardEntry card;
}
