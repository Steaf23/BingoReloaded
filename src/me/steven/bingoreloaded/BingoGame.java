package me.steven.bingoreloaded;

import me.steven.bingoreloaded.data.BingoCardsData;
import me.steven.bingoreloaded.data.RecoveryCardData;
import me.steven.bingoreloaded.gui.cards.*;
import me.steven.bingoreloaded.cardcreator.CardEntry;
import me.steven.bingoreloaded.item.BingoItem;
import me.steven.bingoreloaded.item.InventoryItem;
import me.steven.bingoreloaded.player.PlayerKit;
import me.steven.bingoreloaded.player.TeamManager;
import me.steven.bingoreloaded.util.FlexibleColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class BingoGame implements Listener
{
    public CardEntry card;

    private static final int TELEPORT_DISTANCE = 1000000;
    private static final int ONE_SECOND = 20;
    private final TeamManager teamManager;
    private final GameTimer timer;
    private final BingoScoreboard scoreboard;
    private BingoGameMode currentMode;
    private CardSize currentSize;
    private PlayerKit currentKit;
    private Material deathMatchItem;

    private final Map<String, Location> deadPlayers;

    private boolean gameInProgress = false;

    public BingoGame()
    {
        currentMode = BingoGameMode.REGULAR;
        currentSize = CardSize.X5;
        currentKit = PlayerKit.NORMAL;

        scoreboard = new BingoScoreboard(this);

        teamManager = scoreboard.getTeamManager();
        timer = new GameTimer(scoreboard);
        deadPlayers = new HashMap<>();
    }

    /**
     * Sets the game mode for future Bingo rounds.
     *
     * @param mode the chosen bingo game mode
     */
    public void setCardSettings(BingoGameMode mode, CardSize size)
    {
        currentMode = mode;
        currentSize = size;
    }

    /**
     * Starts a round of bingo using currentMode as the game mode.
     */
    public void start()
    {
        deathMatchItem = null;
        teamManager.updateActivePlayers();

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

        if (card == null)
        {
            card = BingoCardsData.getOrCreateCard("default_card");
        }

        gameInProgress = true;
        BingoCard masterCard = CardBuilder.fromMode(currentMode, currentSize);
        masterCard.generateCard(card);

        World world = Bukkit.getWorlds().get(0);
        world.setStorm(false);
        world.setTime(1000);

        teamManager.removeEmptyTeams();
        teamManager.initializeCards(masterCard);
        givePlayerKits();
        teleportPlayersToStart(world);
        givePlayersEffects();

        scoreboard.resetBoards();
        scoreboard.updateItemCount();

        timer.start();

        RecoveryCardData.saveCards(teamManager, currentMode, currentSize);
        RecoveryCardData.markCardEnded(false);
    }

    public void resume()
    {
        gameInProgress = true;
        scoreboard.updateItemCount();
    }

    public void end()
    {
        deathMatchItem = null;
        timer.stop();
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
            BingoReloaded.broadcast(ChatColor.GREEN + "Game Duration: " + ChatColor.GRAY + ChatColor.ITALIC + GameTimer.getTimeAsString(timer.getTime()));
            RecoveryCardData.markCardEnded(true);
        }
        else
        {
            BingoReloaded.print(ChatColor.RED + "No Game to end!");
        }

        teamManager.updateActivePlayers();
    }

    public void setKit(PlayerKit kit)
    {
        currentKit = kit;
        BingoReloaded.broadcast(ChatColor.GOLD + "Selected " + kit.displayName + ChatColor.GOLD + " player kit!");
    }

    public void givePlayerKits()
    {
        BingoReloaded.broadcast(ChatColor.GREEN + "Giving all participants Kits and Cards!");
        Set<Player> players = teamManager.getParticipants();
        players.forEach(p ->
        {
            p.getInventory().clear();
            p.closeInventory();
            FlexibleColor teamColor = FlexibleColor.fromChatColor(teamManager.getTeamOfPlayer(p).getColor());

            if (teamColor == null) return;
            for(InventoryItem item : currentKit.getItems(teamColor))
            {
                p.getInventory().setItem(item.getSlot(), item);
            }
        });
    }

    public void givePlayersEffects()
    {
        Set<Player> players = teamManager.getParticipants();
        players.forEach(this::givePlayerEffects);
    }

    private void givePlayerEffects(Player player)
    {
        takePlayerEffects(player);

        if (teamManager.getParticipants().contains(player))
        {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100000, 1, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 100000, 1, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100000, 1, false, false));

            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 2, 100, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 2, 100, false, false));
        }
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
        for (Player p : teamManager.getParticipants())
        {
            p.playSound(p, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 0.8f, 1.0f);
            p.playSound(p, Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.5f, 1.0f);
        }
        end();
    }

    public void startDeathMatch(int countdown)
    {
        if (countdown > 0)
        {
            ChatColor color = switch (countdown)
                    {
                        case 1 -> ChatColor.RED;
                        case 2 -> ChatColor.GOLD;
                        default -> ChatColor.GREEN;
                    };
            for (Player p : teamManager.getParticipants())
            {
                p.sendTitle(color + "" + countdown, "", -1, -1, -1);
                BingoReloaded.print(color + "" + countdown, p);
            }

            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    startDeathMatch(countdown - 1);
                }
            }.runTaskLater(BingoReloaded.getPlugin(BingoReloaded.class), ONE_SECOND);
            return;
        }
        deathMatchItem = card.getRandomItem();
        String itemName = BingoItem.convertToReadableName(deathMatchItem);
        for (Player p : teamManager.getParticipants())
        {
            p.sendTitle(ChatColor.GOLD + itemName, ChatColor.DARK_PURPLE + "Death Match: Get this item to win!", -1, -1, -1);
            BingoReloaded.print(ChatColor.GOLD + itemName, p);
        }
    }

    public void showDeathMatchItem(Player p)
    {
        String itemName = BingoItem.convertToReadableName(deathMatchItem);
        p.sendTitle(ChatColor.GOLD + itemName, ChatColor.DARK_PURPLE + "DeathMatch - Find this item to win!", -1, -1, -1);
        BingoReloaded.print(ChatColor.GOLD + itemName, p);
    }

    public boolean isGameInProgress()
    {
        return gameInProgress;
    }

    public TeamManager getTeamManager()
    {
        return teamManager;
    }

    public void teleportPlayerAfterDeath(Player player)
    {
        givePlayerEffects(player);

        if (teamManager.getParticipants().contains(player) && gameInProgress)
        {
            returnCardToPlayer(player);
        }

        if (player == null) return;
        Location location = deadPlayers.get(player.getName());
        BingoReloaded.print("Death Location: " + location, player);
        player.teleport(deadPlayers.get(player.getName()), PlayerTeleportEvent.TeleportCause.PLUGIN);
        deadPlayers.remove(player.getName());
    }

    @EventHandler
    public void onPlayerDropItem(final PlayerDropItemEvent dropEvent)
    {
        if (dropEvent.getItemDrop().getItemStack().equals(currentKit.cardItem.getAsStack()) ||
                dropEvent.getItemDrop().getItemStack().equals(currentKit.wandItem.item.getAsStack()))
        {
            dropEvent.setCancelled(true);
            return;
        }

        if (!gameInProgress) return;

        ItemStack stack = dropEvent.getItemDrop().getItemStack();
        Material item = stack.getType();
        Player player = dropEvent.getPlayer();

        player.getUniqueId();

        Team team = teamManager.getTeamOfPlayer(player);

        if (deathMatchItem != null && deathMatchItem.equals(item))
        {
            bingo(team);
        }

        BingoCard card = teamManager.getCardForTeam(team);
        if (card.completeItem(item, team, timer.getTime()))
        {
            stack.setAmount(stack.getAmount() - 1);
            for (Player p : teamManager.getParticipants())
            {
                p.playSound(p, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 0.8f, 1.0f);
            }
            scoreboard.updateItemCount();

            if (card.hasBingo(team))
            {
                bingo(team);
            }
        }

        RecoveryCardData.saveCards(teamManager, currentMode, currentSize);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event)
    {
        ItemStack item = event.getCurrentItem();
        if (item == null) return;

        if (item.equals(currentKit.cardItem.getAsStack()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent event)
    {
        if (event.getCursor() == null) return;

        if (event.getCursor().equals(currentKit.cardItem.getAsStack()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemInteract(final PlayerInteractEvent event)
    {
        if (event.getHand() != EquipmentSlot.HAND)
            return;
        if (event.getItem() == null)
            return;
        if (event.getItem().getType().isAir())
            return;

        if (event.getItem().equals(currentKit.cardItem.getAsStack()))
        {
            event.setCancelled(true);
            if (!teamManager.getParticipants().contains(event.getPlayer()))
                return;
            Team playerTeam = teamManager.getTeamOfPlayer(event.getPlayer());
            if (playerTeam == null)
            {
                BingoReloaded.print("NO TEAM?", event.getPlayer());
            }

            BingoCard card = teamManager.getCardForTeam(playerTeam);

            // if the player is actually participating, show it
            if (card != null)
            {
                if (deathMatchItem != null)
                {
                    showDeathMatchItem(event.getPlayer());
                    return;
                }
                card.showInventory(event.getPlayer());
            }
            else
            {
                BingoReloaded.print(ChatColor.RED + "The game has not started yet!", event.getPlayer());
            }
        }

        if (event.getItem().equals(currentKit.wandItem.item.getAsStack())
                && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK))
        {
            event.setCancelled(true);
            if (!teamManager.getParticipants().contains(event.getPlayer()))
                return;
            if (currentKit.wandItem.use(event.getPlayer()))
            {
                event.setCancelled(true);
                if (event.getPlayer().isSneaking())
                {
                    teleportPlayerUp(event.getPlayer(), -5, 0);
                }
                else
                {
                    teleportPlayerUp(event.getPlayer(), 75, 5);
                }

                event.getPlayer().playSound(event.getPlayer(), Sound.ENTITY_SHULKER_TELEPORT, 0.8f, 1.0f);
            }
            else
            {
                double seconds = currentKit.wandItem.getTimeLeft(event.getPlayer());
                BingoReloaded.print(ChatColor.RED + String.format("You cannot use this item for another %.2f seconds!", seconds), event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onEntityDamage(final EntityDamageEvent event)
    {
        if (!(event.getEntity() instanceof Player player))
            return;
        if (!teamManager.getParticipants().contains(player))
            return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL)
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event)
    {
        if (gameInProgress)
        {
            if (teamManager.getParticipants().contains(event.getPlayer()))
            {
                BingoReloaded.print("You joined back!", event.getPlayer());
                scoreboard.updateItemCount();
                return;
            }

            Team team = RecoveryCardData.getActiveTeamOfPlayer(event.getPlayer(), teamManager);
            if (team == null)
                return;

            teamManager.addPlayerToTeam(event.getPlayer(), team.getName());
            scoreboard.updateItemCount();
            scoreboard.updateItemCount();
        }
    }

    @EventHandler
    public void onPlayerLeave(final PlayerQuitEvent event)
    {

    }

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event)
    {
        if (gameInProgress)
        {
            if (teamManager.getParticipants().contains(event.getEntity()))
            {
                while (event.getDrops().contains(currentKit.cardItem.getAsStack()))
                    event.getDrops().remove(currentKit.cardItem.getAsStack());

                Location deathCoords = event.getEntity().getLocation();

                TextComponent[] teleportMsg = BingoReloaded.createHoverCommandMessage("",
                        "" + ChatColor.DARK_AQUA + ChatColor.BOLD + "Click here to teleport back to where you died",
                        "",
                        "/bingo back",
                        "Click to teleport to " + deathCoords);

                event.getEntity().spigot().sendMessage(teleportMsg);
                deadPlayers.put(event.getEntity().getName(), deathCoords);

                if (event.getEntity().getLastDamageCause() == null)
                    return;

                if (event.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.LAVA)
                event.getEntity().getWorld().setType(deathCoords, Material.WATER);
            }
        }
    }

    @EventHandler
    public void onPlayerHoldsCard(final PlayerItemHeldEvent event)
    {
        if (teamManager.getParticipants().contains(event.getPlayer()) && gameInProgress)
        {
            if (event.getNewSlot() == currentKit.cardItem.getSlot())
            {
                event.getPlayer().addPotionEffect(
                        new PotionEffect(PotionEffectType.SPEED, 100000, 1, false, false));
            }

            if (event.getPreviousSlot() == currentKit.cardItem.getSlot())
            {
                event.getPlayer().removePotionEffect(PotionEffectType.SPEED);
            }
        }
    }

    public void returnCardToPlayer(Player player)
    {
        if (!gameInProgress)
            return;

        while (player.getInventory().contains(currentKit.cardItem.getAsStack()))
            player.getInventory().remove(currentKit.cardItem.getAsStack());

        player.getInventory().setItem(8, currentKit.cardItem.getAsStack());
        givePlayerEffects(player);
    }

    public boolean isOceanBiome(Biome biome)
    {
        return switch (biome)
        {
            case
                    OCEAN,
                    DEEP_COLD_OCEAN,
                    COLD_OCEAN, DEEP_OCEAN,
                    FROZEN_OCEAN, DEEP_FROZEN_OCEAN,
                    LUKEWARM_OCEAN,
                    DEEP_LUKEWARM_OCEAN,
                    WARM_OCEAN -> true;
            default -> false;
        };
    }

    private void takePlayerEffects(Player player)
    {
        for (PotionEffectType effect : PotionEffectType.values())
        {
            player.removePotionEffect(effect);
        }
    }

    private void teleportPlayersToStart(World world)
    {
        Vector targetPosition = Vector.getRandom().multiply(TELEPORT_DISTANCE);
        Location spawnLocation = new Location(world, targetPosition.getX(), 200, targetPosition.getZ());

        //find a not ocean biome to start the game in
        while (isOceanBiome(world.getBiome(spawnLocation)))
        {
            targetPosition = Vector.getRandom().multiply(TELEPORT_DISTANCE);
            spawnLocation = new Location(world, targetPosition.getX(), 200, targetPosition.getZ());
        }

        Set<Player> players = teamManager.getParticipants();
        for (Player p : players)
        {
            p.teleport(spawnLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }

        if (teamManager.getParticipants().size() > 0)
            spawnPlatform(spawnLocation, 5);
    }

    private static void teleportPlayerUp(Player player, int distance, int fallDistance)
    {
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
                        Material.WHITE_STAINED_GLASS);
            }
        }
    }
}
