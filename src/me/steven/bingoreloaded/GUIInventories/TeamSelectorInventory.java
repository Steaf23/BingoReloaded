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
        MenuItem item = new MenuItem(Material.BARRIER, "", "");
        ItemMeta meta = item.getItemMeta();
        if (meta != null)
            meta.setDisplayName("NONE");

        switch (color)
        {
//            case WHITE:
//                item = new ItemStack(Material.WHITE_CONCRETE);
//                meta = item.getItemMeta();
//                if (meta != null)
//                    meta.setDisplayName(ChatColor.WHITE + "White" + ChatColor.RESET);
//                break;
//
//            case DARK_RED:
//                item = new ItemStack(Material.BROWN_CONCRETE);
//                meta = item.getItemMeta();
//                if (meta != null)
//                    meta.setDisplayName(ChatColor.DARK_RED + "Evil Red" + ChatColor.RESET);
//                break;
//
//            case RED:
//                item = new ItemStack(Material.RED_CONCRETE);
//                meta = item.getItemMeta();
//                if (meta != null)
//                    meta.setDisplayName(ChatColor.RED + "Red" + ChatColor.RESET);
//                break;
//
//            case GOLD:
//                item = new ItemStack(Material.ORANGE_CONCRETE);
//                meta = item.getItemMeta();
//                if (meta != null)
//                    meta.setDisplayName(ChatColor.GOLD + "Gold" + ChatColor.RESET);
//                break;
//
//            case YELLOW:
//                item = new ItemStack(Material.YELLOW_CONCRETE);
//                meta = item.getItemMeta();
//                if (meta != null)
//                    meta.setDisplayName(ChatColor.YELLOW + "Yellow" + ChatColor.RESET);
//                break;
//
//            case DARK_GREEN:
//                item = new ItemStack(Material.GREEN_CONCRETE);
//                meta = item.getItemMeta();
//                if (meta != null)
//                    meta.setDisplayName(ChatColor.DARK_GREEN + "Dark Green" + ChatColor.RESET);
//                break;
//
//            case GREEN:
//                item = new ItemStack(Material.LIME_CONCRETE);
//                meta = item.getItemMeta();
//                if (meta != null)
//                    meta.setDisplayName(ChatColor.GREEN + "Light Green" + ChatColor.RESET);
//                break;
//
//            case AQUA:
//                item = new ItemStack(Material.MAGENTA_CONCRETE);
//                meta = item.getItemMeta();
//                if (meta != null)
//                    meta.setDisplayName(ChatColor.AQUA + "Fake Magenta" + ChatColor.RESET);
//                break;
//
//            case DARK_AQUA:
//                item = new ItemStack(Material.CYAN_CONCRETE);
//                meta = item.getItemMeta();
//                if (meta != null)
//                    meta.setDisplayName(ChatColor.DARK_AQUA + "Cyan" + ChatColor.RESET);
//                break;
//
//            case DARK_BLUE:
//                item = new ItemStack(Material.BLUE_CONCRETE);
//                meta = item.getItemMeta();
//                if (meta != null)
//                    meta.setDisplayName(ChatColor.DARK_BLUE + "Dark Blue" + ChatColor.RESET);
//                break;
//
//            case BLUE:
//                item = new ItemStack(Material.LIGHT_BLUE_CONCRETE);
//                meta = item.getItemMeta();
//                if (meta != null)
//                    meta.setDisplayName(ChatColor.BLUE + "Light Blue" + ChatColor.RESET);
//                break;
//
//            case LIGHT_PURPLE:
//                item = new ItemStack(Material.PINK_CONCRETE);
//                meta = item.getItemMeta();
//                if (meta != null)
//                    meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Pink" + ChatColor.RESET);
//                break;
//
//            case DARK_PURPLE:
//                item = new ItemStack(Material.PURPLE_CONCRETE);
//                meta = item.getItemMeta();
//                if (meta != null)
//                    meta.setDisplayName(ChatColor.DARK_PURPLE + "Purple" + ChatColor.RESET);
//                break;
//
//            case GRAY:
//                item = new ItemStack(Material.LIGHT_GRAY_CONCRETE);
//                meta = item.getItemMeta();
//                if (meta != null)
//                    meta.setDisplayName(ChatColor.GRAY + "Light Gray" + ChatColor.RESET);
//                break;
//
//            case DARK_GRAY:
//                item = new ItemStack(Material.GRAY_CONCRETE);
//                meta = item.getItemMeta();
//                if (meta != null)
//                    meta.setDisplayName(ChatColor.DARK_GRAY + "Gray" + ChatColor.RESET);
//                break;
//
//            case BLACK:
//                item = new ItemStack(Material.BLACK_CONCRETE);
//                meta = item.getItemMeta();
//                if (meta != null)
//                    meta.setDisplayName(ChatColor.BLACK + "Dark Gray" + ChatColor.RESET);
//                break;

            default:
                return item;

        }

//        if (meta != null)
//            meta.setLore(List.of("Click to join the " + meta.getDisplayName() + ChatColor.DARK_PURPLE + ChatColor.ITALIC + " Team!"));
//        item.setItemMeta(meta);
//        return item;
    }
}
