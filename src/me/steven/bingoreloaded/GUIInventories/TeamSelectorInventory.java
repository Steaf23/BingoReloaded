package me.steven.bingoreloaded.GUIInventories;

import me.steven.bingoreloaded.InventoryItem;
import me.steven.bingoreloaded.TeamManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public class TeamSelectorInventory extends AbstractGUIInventory
{
    public static void open(TeamManager manager, AbstractGUIInventory parent, HumanEntity player)
    {
        TeamSelectorInventory inv = new TeamSelectorInventory(manager, parent);
        inv.open(player);
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player)
    {
        InventoryItem item = getOption(slotClicked);
        if (item == null) return;

        manager.addPlayerToTeam(player, Objects.requireNonNull(item.getItemMeta()).getDisplayName());
        openParent(player);
    }

    public InventoryItem createTeamItem(ChatColor color)
    {
        InventoryItem item;
        switch (color)
        {
            case WHITE:
                item = new InventoryItem(Material.WHITE_CONCRETE, color + "White");
                break;

            case DARK_RED:
                item = new InventoryItem(Material.BROWN_CONCRETE, color + "Evil Red");
                break;

            case RED:
                item = new InventoryItem(Material.RED_CONCRETE, color + "Red");
                break;

            case GOLD:
                item = new InventoryItem(Material.ORANGE_CONCRETE, color + "Gold");
                break;

            case YELLOW:
                item = new InventoryItem(Material.YELLOW_CONCRETE, color + "Yellow");
                break;

            case DARK_GREEN:
                item = new InventoryItem(Material.GREEN_CONCRETE, color + "Dark Green");
                break;

            case GREEN:
                item = new InventoryItem(Material.LIME_CONCRETE, color + "Light Green");
                break;

            case AQUA:
                item = new InventoryItem(Material.MAGENTA_CONCRETE, color + "fake Magenta");
                break;

            case DARK_AQUA:
                item = new InventoryItem(Material.CYAN_CONCRETE, color + "Cyan");
                break;

            case DARK_BLUE:
                item = new InventoryItem(Material.BLUE_CONCRETE, color + "Dark Blue");
                break;

            case BLUE:
                item = new InventoryItem(Material.LIGHT_BLUE_CONCRETE, color + "Light Blue");
                break;

            case LIGHT_PURPLE:
                item = new InventoryItem(Material.PINK_CONCRETE, color + "Pink");
                break;

            case DARK_PURPLE:
                item = new InventoryItem(Material.PURPLE_CONCRETE, color + "Purple");
                break;

            case GRAY:
                item = new InventoryItem(Material.LIGHT_GRAY_CONCRETE, color + "Light Gray");
                break;

            case DARK_GRAY:
                item = new InventoryItem(Material.GRAY_CONCRETE, color + "Gray");
                break;

            case BLACK:
                item = new InventoryItem(Material.BLACK_CONCRETE, color + "Dark Gray");
                break;

            default:
                return null;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null)
            meta.setLore(List.of("Click to join the " + meta.getDisplayName() + ChatColor.DARK_PURPLE + ChatColor.ITALIC + " Team!"));
        item.setItemMeta(meta);
        return item;
    }

    private final TeamManager manager;

    private TeamSelectorInventory(TeamManager manager, AbstractGUIInventory parent)
    {
        super(18, "Join A Team", parent);

        for (ChatColor color : ChatColor.values())
        {
            ItemStack item = createTeamItem(color);
            if (item != null)
            {
                addOption(-1, createTeamItem(color));
            }
        }

        this.manager = manager;
    }
}
