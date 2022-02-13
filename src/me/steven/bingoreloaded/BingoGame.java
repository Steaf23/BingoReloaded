package me.steven.bingoreloaded;

import me.steven.bingoreloaded.GUIInventories.cards.*;
import me.steven.bingoreloaded.cardcreator.CardEntry;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class BingoGame implements Listener
{
    public CardEntry card;

    private static final int TELEPORT_DISTANCE = 1000000;
    private final TeamManager teamManager;
    private BingoGameMode currentMode;
    private PlayerKit currentKit;
    private final InventoryItem cardItem = new InventoryItem(
            Material.MAP,
            "" + ChatColor.DARK_PURPLE + ChatColor.ITALIC + ChatColor.BOLD + "Bingo Card",
            "Click To Open The Bingo Card!");

    private final ItemCooldownManager wandItem = new ItemCooldownManager(new InventoryItem(
            Material.WARPED_FUNGUS_ON_A_STICK,
            "" + ChatColor.DARK_PURPLE + ChatColor.ITALIC + ChatColor.BOLD + "The Go-Up-Wand",
            "Right-Click To Teleport Upwards!"
    ), 5000);

    private boolean gameInProgress = false;

    public BingoGame()
    {
        currentMode = BingoGameMode.REGULAR;
        currentKit = PlayerKit.NORMAL;

        teamManager = new TeamManager(this);

        ItemMeta cardMeta = cardItem.getItemMeta();
        if (cardMeta != null)
            cardMeta.setDisplayName("" + ChatColor.DARK_PURPLE + ChatColor.ITALIC + ChatColor.BOLD + "Bingo Card");
        if (cardMeta != null)
            cardMeta.setLore(List.of("Click To Open The Bingo Card!"));
        cardItem.setItemMeta(cardMeta);
        wandItem.item.addEnchantment(Enchantment.DURABILITY, 3);
    }

    /**
     * Sets the game mode for future Bingo rounds.
     *
     * @param mode the chosen bingo game mode
     */
    public void setGameMode(BingoGameMode mode)
    {
        currentMode = mode;

        TextComponent[] message = BingoReloaded.createHoverCommandMessage(
                currentMode.name + ChatColor.GOLD + " Bingo has been selected by an admin, join the game using ",
                ChatColor.DARK_RED + "/bingo",
                "",
                "/bingo",
                "Or click here to join the game ;)");

        for (Player p : Bukkit.getOnlinePlayers())
        {
            p.spigot().sendMessage(message);
        }
    }

    /**
     * Starts a round of bingo using currentMode as the game mode.
     */
    public void start()
    {
        if (teamManager.getParticipants().size() <= 0)
        {
            BingoReloaded.broadcast("" + ChatColor.RED + ChatColor.ITALIC + ChatColor.BOLD + "Bingo could not be started since nobody joined :(");
            return;
        }

        if (gameInProgress)
        {
            BingoReloaded.broadcast("" + ChatColor.RED + ChatColor.ITALIC + ChatColor.BOLD + "Cannot start a game of Bingo when there is already one active!");
            return;
        }

        gameInProgress = true;
        BingoCard masterCard = CardBuilder.fromMode(currentMode);
        masterCard.generateCard(card);

        World world = Bukkit.getWorlds().get(0);
        world.setStorm(false);
        world.setTime(1000);

        teamManager.initializeCards(masterCard);
        givePlayerKits();
        teleportPlayers(world);
        givePlayersEffects();
        teamManager.clearTeamDisplay();
        teamManager.updateTeamDisplay();
    }

    public void end()
    {
        if (gameInProgress)
        {
            gameInProgress = false;
            TextComponent[] commandMessage = BingoReloaded.createHoverCommandMessage(
                    "Game has ended! Click to ",
                    "Restart!",
                    "",
                    "/bingo start",
                    "Click to restart using the same rules!");

            for(Player p : Bukkit.getOnlinePlayers())
            {
                p.spigot().sendMessage(commandMessage);
            }
        }
        else
        {
            BingoReloaded.print(ChatColor.RED + "No Game to end!");
        }
    }

    public void setKit(String command)
    {
        switch (command)
        {
            case "reset", "normal" -> {
                BingoReloaded.broadcast(ChatColor.GOLD + "Selected Normal Kit!");
                currentKit = PlayerKit.NORMAL;
            }
            default -> BingoReloaded.broadcast(ChatColor.RED + "Kit '" + command + "' not found!");
        }
    }

    public void givePlayerKits()
    {
        BingoReloaded.broadcast("Giving all participants Kits and Cards!");
        Set<Player> players = teamManager.getParticipants();
        players.forEach(p ->
        {
            p.getInventory().clear();
            p.closeInventory();

            ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
            helmet.addEnchantments(new HashMap<>(){{
                put(Enchantment.WATER_WORKER, 1);
                put(Enchantment.DURABILITY, 3);
                put(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
            }});

            LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
            if (helmetMeta != null)
            {
                java.awt.Color color = teamManager.getPlayerTeam(p).getColor().asBungee().getColor();
                helmetMeta.setColor(Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()));
            }
            helmet.setItemMeta(helmetMeta);

            ItemStack boots = new ItemStack(Material.NETHERITE_BOOTS);
            boots.addEnchantments(new HashMap<>(){{
                put(Enchantment.DEPTH_STRIDER, 3);
                put(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
            }});
            p.getInventory().setArmorContents(new ItemStack[] {boots, null, null, helmet});
            p.getInventory().setItem(0, new ItemStack(Material.GOLDEN_CARROT, 64));
            p.getInventory().setItem(7, wandItem.item);
            p.getInventory().setItem(8, cardItem);
        });
    }

    public void givePlayersEffects()
    {
        Set<Player> players = teamManager.getParticipants();
        players.forEach(p ->
        {
            p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100000, 1, false, false));
            p.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 100000, 1, false, false));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000, 2, false, false));
        });
    }

    public void playerQuit(Player player)
    {
        if (!teamManager.getParticipants().contains(player)) return;

        teamManager.removePlayerFromAllTeams(player);
        BingoReloaded.print("You have been successfully removed from the game, use " + ChatColor.DARK_RED + "/bingo join " + ChatColor.RESET + "to come back to me :D", player);
        takePlayerEffects(player);
    }

    public void bingo(Team team)
    {
        BingoReloaded.broadcast("Congratulations! Team " + team.getDisplayName() + ChatColor.RESET + " has won the Bingo!");
        end();
    }

    public boolean isGameInProgress()
    {
        return gameInProgress;
    }

    public TeamManager getTeamManager()
    {
        return teamManager;
    }

    @EventHandler
    public void onPlayerDropItem(final PlayerDropItemEvent dropEvent)
    {
        BingoReloaded.broadcast(cardItem.getItemMeta().getDisplayName() + " " + dropEvent.getItemDrop().getItemStack().getItemMeta().getDisplayName());
        if (dropEvent.getItemDrop().getItemStack().equals(cardItem.getAsStack()) ||
                dropEvent.getItemDrop().getItemStack().equals(wandItem.item.getAsStack()))
        {
            dropEvent.setCancelled(true);
            return;
        }

        if (!gameInProgress) return;

        ItemStack stack = dropEvent.getItemDrop().getItemStack();
        Material item = stack.getType();
        Player player = dropEvent.getPlayer();

        player.getUniqueId();

        Team team = teamManager.getPlayerTeam(player);

        BingoCard card = teamManager.getCardForTeam(team);

        if (card.completeItem(item, team))
        {
            stack.setAmount(stack.getAmount() - 1);
            for (Player p : teamManager.getParticipants())
            {
                p.playSound(p, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 0.8f, 1.0f);
            }
            teamManager.updateTeamDisplay();

            if (card.hasBingo(team))
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

        if (item.equals(cardItem.getAsStack()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventorDrag(final InventoryDragEvent event)
    {
        if (event.getCursor() == null) return;

        if (event.getCursor().equals(cardItem.getAsStack()))
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

        if (event.getItem().equals(cardItem.getAsStack()))
        {
            event.setCancelled(true);
            if (!teamManager.getParticipants().contains(event.getPlayer())) return;
            Team playerTeam = teamManager.getPlayerTeam(event.getPlayer());
            if (playerTeam == null)
            {
                BingoReloaded.print("NO TEAM?", event.getPlayer());
            }

            BingoCard card = teamManager.getCardForTeam(playerTeam);

            // if the player is actually participating, show it
            if (card != null)
            {
                card.showInventory(event.getPlayer());
            }
            else
            {
                BingoReloaded.print("CARD IS NULL!", event.getPlayer());
            }
        }

        if (event.getItem().equals(wandItem.item.getAsStack())
                && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK))
        {
            int teleportHeight = 75;
            event.setCancelled(true);
            if (!teamManager.getParticipants().contains(event.getPlayer())) return;
            if (wandItem.use(event.getPlayer()))
            {
                event.setCancelled(true);
                teleportPlayerUp(event.getPlayer(), teleportHeight);
                event.getPlayer().playSound(event.getPlayer(), Sound.ENTITY_SHULKER_TELEPORT, 0.8f, 1.0f);
            }
            else
            {
                double seconds = wandItem.getTimeLeft(event.getPlayer());
                BingoReloaded.print(ChatColor.RED + String.format("You cannot use this item for another %.2f seconds!", seconds), event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onEntityDamage(final EntityDamageEvent event)
    {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!teamManager.getParticipants().contains(player)) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;

        event.setCancelled(true);
    }

    private void takePlayerEffects(Player player)
    {
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        player.removePotionEffect(PotionEffectType.WATER_BREATHING);
        player.removePotionEffect(PotionEffectType.SPEED);
    }

    private void teleportPlayers(World world)
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
                spawnLocation = new Location(world, targetPosition.getX(), 200, targetPosition.getZ());
            }

            p.teleport(spawnLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }

        if (teamManager.getParticipants().size() > 0)
            spawnPlatform(spawnLocation, 5);
    }

    private static void teleportPlayerUp(Player player, int distance)
    {
        int fallDistance = 5;
        Location newLocation = player.getLocation();
        newLocation.setY(newLocation.getY() + distance + fallDistance);

        player.teleport(newLocation, PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT);

        newLocation.setY(newLocation.getY() - fallDistance);

        spawnPlatform(newLocation, 1);
    }

    private static void spawnPlatform(@Nullable Location spawnLocation, int size)
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
}
