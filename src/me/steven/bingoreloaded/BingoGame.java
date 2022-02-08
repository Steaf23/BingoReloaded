package me.steven.bingoreloaded;

import me.steven.bingoreloaded.cards.*;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class BingoGame implements Listener
{
    private static final int TELEPORT_DISTANCE = 1000000;
    private BingoGameMode currentMode = BingoGameMode.REGULAR;
    private final ItemStack cardItem = new ItemStack(Material.MAP);
    public final TeamManager teamManager = new TeamManager();


    public BingoGame()
    {
        ItemMeta cardMeta = cardItem.getItemMeta();
        if (cardMeta != null)
            cardMeta.setDisplayName("" + ChatColor.DARK_PURPLE + ChatColor.ITALIC + ChatColor.BOLD + "Bingo Card");
        if (cardMeta != null)
            cardMeta.setLore(List.of("Click To Open The Bingo Card!"));
        cardItem.setItemMeta(cardMeta);
    }

    /**
     * Sets the game mode for future Bingo rounds.
     *
     * @param mode the chosen bingo game mode
     */
    public void setup(BingoGameMode mode)
    {
        currentMode = mode;

        BingoReloaded.broadcast(currentMode.name + ChatColor.GOLD + " Bingo is about to start, join the game using " + ChatColor.DARK_RED + "/bingo join" + ChatColor.GOLD + "!");
    }

    /**
     * Starts a round of bingo using currentMode as the game mode.
     */
    public void start()
    {
        BingoCard masterCard = BingoCard.fromMode(currentMode);
        masterCard.generateCard(null);

        if (teamManager.getParticipants().size() <= 0)
        {
            BingoReloaded.broadcast("" + ChatColor.RED + ChatColor.ITALIC + ChatColor.BOLD + "Bingo could not be started since nobody joined :(");
            return;
        }

        teamManager.initializeCards(masterCard);
        givePlayerKits();
        teleportPlayers();
    }

    public void end()
    {

    }

    public void restart(BingoCard card)
    {
        end();
        start();
    }

    public void quickRestart()
    {
        end();
        start();
    }

    @EventHandler
    public void onPlayerDropItem(final PlayerDropItemEvent dropEvent)
    {
        if (dropEvent.getItemDrop().getItemStack().equals(cardItem))
        {
            dropEvent.setCancelled(true);
            return;
        }

        Material item = dropEvent.getItemDrop().getItemStack().getType();
        Player player = dropEvent.getPlayer();

        player.getUniqueId();

        Team team = teamManager.getPlayerTeam(player);

        BingoCard card = teamManager.getCardForTeam(team);

        if (card.completeItem(item))
        {
            dropEvent.getItemDrop().remove();
            for (Player p : teamManager.getParticipants())
            {
                p.playSound(p, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 0.8f, 1.0f);
            }

            if (card.checkBingo())
            {
                bingo(team);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event)
    {
        ItemStack item = event.getCurrentItem();
        if (item == null) return;

        if (item.equals(cardItem))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventorDrag(final InventoryDragEvent event)
    {
        if (event.getCursor() == null) return;

        if (event.getCursor().equals(cardItem))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemInteract(final PlayerInteractEvent event)
    {
        // guards
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getItem() == null) return;
        if (event.getItem().getType().isAir()) return;

        if (event.getItem().equals(cardItem))
        {
            event.setCancelled(true);

            BingoCard card = teamManager.getCardForTeam(teamManager.getPlayerTeam(event.getPlayer()));

            // if the player is actually participating, show it
            if (card != null)
            {
                card.showInventory(event.getPlayer());
            }
        }
    }

    public void givePlayerKits()
    {
        BingoReloaded.broadcast("Giving all participants Kits and Cards!");
        Set<Player> players = teamManager.getParticipants();
        players.forEach(p ->
        {
            p.getInventory().clear();

            ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
            helmet.addEnchantments(new HashMap<>(){{
                put(Enchantment.WATER_WORKER, 1);
                put(Enchantment.DURABILITY, 3);
                put(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
            }});
            ItemStack boots = new ItemStack(Material.NETHERITE_BOOTS);
            boots.addEnchantments(new HashMap<>(){{
                put(Enchantment.DEPTH_STRIDER, 3);
                put(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
            }});
            p.getInventory().setArmorContents(new ItemStack[] {boots, null, null, helmet});
            p.getInventory().setItem(8, cardItem);
        });
    }

    public void teleportPlayers()
    {
        Vector targetPosition = Vector.getRandom().multiply(TELEPORT_DISTANCE);

        Location spawnLocation = null;
        boolean locationSet = false;

        Set<Player> players = teamManager.getParticipants();
        for (Player p : players)
        {
            if (!locationSet)
            {
                locationSet = true;
                spawnLocation = new Location(p.getWorld(), targetPosition.getX(), 200, targetPosition.getZ());
            }

            p.teleport(spawnLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }

        if (teamManager.getParticipants().size() > 0)
            spawnStartPlatform(spawnLocation, 5);
    }

    public void spawnStartPlatform(@Nullable Location spawnLocation, int size)
    {
        for (int x = -size; x < size + 1; x++)
        {
            for (int z = -size; z < size + 1; z++)
            {
                spawnLocation.getWorld().setType(
                        (int)spawnLocation.getX() + x,
                        (int)spawnLocation.getY() - 20,
                        (int)spawnLocation.getZ() + z,
                        Material.GLASS);
            }
        }
    }

    public void bingo(Team team)
    {
        BingoReloaded.broadcast("Congratulations! Team " + team.getDisplayName() + " has won the Bingo!");
    }
}
