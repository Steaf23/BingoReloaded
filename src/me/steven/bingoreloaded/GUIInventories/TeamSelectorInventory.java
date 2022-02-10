package me.steven.bingoreloaded.GUIInventories;

import me.steven.bingoreloaded.MenuItem;
import me.steven.bingoreloaded.TeamManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public class TeamSelectorInventory extends SubGUIInventory
{
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

    public static void open(TeamManager manager, AbstractGUIInventory parent, HumanEntity player)
    {
        TeamSelectorInventory inv = new TeamSelectorInventory(manager, parent);
        inv.open(player);
    }

    @Override
    public void delegateClick(InventoryClickEvent event)
    {
        final ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType().isAir()) return;

        manager.addPlayerToTeam((Player)event.getWhoClicked(), Objects.requireNonNull(event.getCurrentItem().getItemMeta()).getDisplayName());
        openParent((Player)event.getWhoClicked());
    }

    @Override
    public void delegateDrag(InventoryDragEvent event)
    {

    }

    public MenuItem createTeamItem(ChatColor color)
    {
        MenuItem item;
        switch (color)
        {
            case WHITE:
                item = new MenuItem(Material.WHITE_CONCRETE, color + "White");
                break;

            case DARK_RED:
                item = new MenuItem(Material.BROWN_CONCRETE, color + "Evil Red");
                break;

            case RED:
                item = new MenuItem(Material.RED_CONCRETE, color + "Red");
                break;

            case GOLD:
                item = new MenuItem(Material.ORANGE_CONCRETE, color + "Gold");
                break;

            case YELLOW:
                item = new MenuItem(Material.YELLOW_CONCRETE, color + "Yellow");
                break;

            case DARK_GREEN:
                item = new MenuItem(Material.GREEN_CONCRETE, color + "Dark Green");
                break;

            case GREEN:
                item = new MenuItem(Material.LIME_CONCRETE, color + "Light Green");
                break;

            case AQUA:
                item = new MenuItem(Material.MAGENTA_CONCRETE, color + "fake Magenta");
                break;

            case DARK_AQUA:
                item = new MenuItem(Material.CYAN_CONCRETE, color + "Cyan");
                break;

            case DARK_BLUE:
                item = new MenuItem(Material.BLUE_CONCRETE, color + "Dark Blue");
                break;

            case BLUE:
                item = new MenuItem(Material.LIGHT_BLUE_CONCRETE, color + "Light Blue");
                break;

            case LIGHT_PURPLE:
                item = new MenuItem(Material.PINK_CONCRETE, color + "Pink");
                break;

            case DARK_PURPLE:
                item = new MenuItem(Material.PURPLE_CONCRETE, color + "Purple");
                break;

            case GRAY:
                item = new MenuItem(Material.LIGHT_GRAY_CONCRETE, color + "Light Gray");
                break;

            case DARK_GRAY:
                item = new MenuItem(Material.GRAY_CONCRETE, color + "Gray");
                break;

            case BLACK:
                item = new MenuItem(Material.BLACK_CONCRETE, color + "Dark Gray");
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
}
