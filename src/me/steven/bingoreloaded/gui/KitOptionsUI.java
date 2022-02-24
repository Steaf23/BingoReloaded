package me.steven.bingoreloaded.gui;

import me.steven.bingoreloaded.BingoGame;
import me.steven.bingoreloaded.item.InventoryItem;
import me.steven.bingoreloaded.player.PlayerKit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class KitOptionsUI extends AbstractGUIInventory
{
    private final BingoGame game;
    private static final InventoryItem HARDCORE = new InventoryItem(GUIBuilder5x9.OptionPositions.FOUR_CENTER3.positions[0],
            Material.RED_CONCRETE, TITLE_PREFIX + PlayerKit.HARDCORE.displayName,
            "This kit includes:", "",
            "   - Nothing, good luck!");
    private static final InventoryItem NORMAL = new InventoryItem(GUIBuilder5x9.OptionPositions.FOUR_CENTER3.positions[1],
            Material.YELLOW_CONCRETE, TITLE_PREFIX + PlayerKit.NORMAL.displayName,
            "This kit includes:", "",
            "   - Iron Axe",
            "   - Iron Pickaxe",
            "   - Iron Shovel (Silk Touch)",
            "   - 32 baked Potatoes",
            "   - Leather Helmet",
            "   - Leather Boots");
    private static final InventoryItem OVERPOWERED = new InventoryItem(GUIBuilder5x9.OptionPositions.FOUR_CENTER3.positions[2],
            Material.PURPLE_CONCRETE, TITLE_PREFIX + PlayerKit.OVERPOWERED.displayName,
            "This kit includes:", "",
            "   - Netherite Axe",
            "   - Netherite Pickaxe (Fortune)",
            "   - Netherite Shovel (Silk Touch)",
            "   - 64 Golden Carrots",
            "   - The Go-Up-Wand",
            "   - Leather Helmet",
            "   - Leather Boots");
    private static final InventoryItem RELOADED = new InventoryItem(GUIBuilder5x9.OptionPositions.FOUR_CENTER3.positions[3],
            Material.CYAN_CONCRETE, TITLE_PREFIX + PlayerKit.RELOADED.displayName,
            "This kit includes:", "",
            "   - Netherite Axe",
            "   - Netherite Pickaxe (Fortune)",
            "   - Netherite Shovel (Silk Touch)",
            "   - 64 Golden Carrots",
            "   - The Go-Up-Wand",
            "   - Leather Helmet",
            "   - Elytra (Unbreaking X)",
            "   - Leather Boots");

    public KitOptionsUI(AbstractGUIInventory parent, BingoGame game)
    {
        super(45, "Choose Player Kit", parent);
        this.game = game;

        fillOptions(new InventoryItem[]{HARDCORE, NORMAL, OVERPOWERED, RELOADED});
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player)
    {
        if (slotClicked == HARDCORE.getSlot())
        {
            game.setKit(PlayerKit.HARDCORE);
        }
        else if (slotClicked == NORMAL.getSlot())
        {
            game.setKit(PlayerKit.NORMAL);
        }
        else if (slotClicked == OVERPOWERED.getSlot())
        {
            game.setKit(PlayerKit.OVERPOWERED);
        }
        else if (slotClicked == RELOADED.getSlot())
        {
            game.setKit(PlayerKit.RELOADED);
        }
        close(player);
    }
}
