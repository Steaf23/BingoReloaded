package me.steven.bingoreloaded;

import me.steven.bingoreloaded.criteria.BingoCriteriaCompleteEvent;
import me.steven.bingoreloaded.data.BingoCardsData;
import me.steven.bingoreloaded.data.ConfigData;
import me.steven.bingoreloaded.data.RecoveryCardData;
import me.steven.bingoreloaded.gui.EffectOptionFlags;
import me.steven.bingoreloaded.gui.cards.*;
import me.steven.bingoreloaded.cardcreator.CardEntry;
import me.steven.bingoreloaded.item.BingoCardItem;
import me.steven.bingoreloaded.item.InventoryItem;
import me.steven.bingoreloaded.player.BingoTeam;
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
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class BingoGame implements Listener
{
    public CardEntry card;

    private static final int TELEPORT_DISTANCE = ConfigData.getConfig().teleportMaxDistance;
    private static final int ONE_SECOND = 20;
    private final TeamManager teamManager;
    private final GameTimer timer;
    private final BingoScoreboard scoreboard;
    private BingoGameMode currentMode;
    private CardSize currentSize;
    private PlayerKit currentKit;
    private EnumSet<EffectOptionFlags> currentEffects;
    private Material deathMatchItem;

    private final Map<String, Location> deadPlayers;

    private boolean gameInProgress = false;

    public BingoGame()
    {
        currentMode = BingoGameMode.REGULAR;
        currentSize = CardSize.X5;
        currentKit = ConfigData.getConfig().defaultKit;
        currentEffects = currentKit.defaultEffects;

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
        BingoCard masterCard = CardBuilder.fromMode(currentMode, currentSize, teamManager);
        masterCard.generateCard(card);

        World world = Bukkit.getWorlds().get(0);
        world.setStorm(false);
        world.setTime(1000);

        teamManager.removeEmptyTeams();
        teamManager.initializeCards(masterCard);

        BingoReloaded.broadcast(ChatColor.GREEN + "Giving all participants Kits and Cards!");
        Set<Player> players = teamManager.getParticipants();
        players.forEach(this::givePlayerKit);
        players.forEach(this::returnCardToPlayer);

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
        currentEffects = kit.defaultEffects;
        BingoReloaded.broadcast(ChatColor.GOLD + "Selected " + kit.displayName + ChatColor.GOLD + " player kit!");
    }

    public void setEffects(EnumSet<EffectOptionFlags> effects)
    {
        currentEffects = effects;
        BingoReloaded.broadcast(ChatColor.GOLD + "Selected Effect options, view them in the /bingo options!");
    }

    public EnumSet<EffectOptionFlags> getEffects()
    {
        return currentEffects;
    }

    public void givePlayerKit(Player p)
    {
        p.getInventory().clear();
        p.closeInventory();
        FlexibleColor teamColor = FlexibleColor.fromChatColor(teamManager.getTeamOfPlayer(p).team.getColor());

        if (teamColor == null) return;
        for(InventoryItem item : currentKit.getItems(teamColor))
        {
            p.getInventory().setItem(item.getSlot(), item);
        }
    }

    public void givePlayersEffects()
    {
        Set<Player> players = teamManager.getParticipants();
        players.forEach((p) -> {
            givePlayerEffects(p);
            p.setGameMode(GameMode.SURVIVAL);
        });
    }

    private void givePlayerEffects(Player player)
    {
        takePlayerEffects(player);

        if (currentEffects.contains(EffectOptionFlags.NIGHT_VISION))
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100000, 1, false, false));
        if (currentEffects.contains(EffectOptionFlags.WATER_BREATHING))
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 100000, 1, false, false));
        if (currentEffects.contains(EffectOptionFlags.FIRE_RESISTANCE))
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100000, 1, false, false));

        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 2, 100, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 2, 100, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, ONE_SECOND * ConfigData.getConfig().gracePeriod, 100, false, false));
    }

    public void playerQuit(Player player)
    {
        if (!teamManager.getParticipants().contains(player)) return;

        teamManager.removePlayerFromAllTeams(player);
        BingoReloaded.print("You have been successfully removed from the game, use " + ChatColor.DARK_RED + "/bingo join " + ChatColor.RESET + "to come back to me :D", player);
        takePlayerEffects(player);
    }

    public void bingo(BingoTeam team)
    {
        BingoReloaded.broadcast("Congratulations! Team " + team.getName() + ChatColor.RESET + " has won the Bingo!");
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
        String itemName = BingoCardItem.convertToReadableName(deathMatchItem);
        for (Player p : teamManager.getParticipants())
        {
            p.sendTitle(ChatColor.GOLD + itemName, ChatColor.DARK_PURPLE + "Death Match: Get this item to win!", -1, -1, -1);
            BingoReloaded.print(ChatColor.GOLD + itemName, p);
        }
    }

    public void showDeathMatchItem(Player p)
    {
        String itemName = BingoCardItem.convertToReadableName(deathMatchItem);
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
        if (player == null) return;
        Location location = deadPlayers.get(player.getName());
        BingoReloaded.print("Death Location: " + location, player);
        player.teleport(deadPlayers.get(player.getName()), PlayerTeleportEvent.TeleportCause.PLUGIN);
        deadPlayers.remove(player.getName());
    }

    @EventHandler
    public void onPlayerCompletedCriteria(final BingoCriteriaCompleteEvent event)
    {
        BingoTeam team = teamManager.getTeamOfPlayer(event.getPlayer());

        if (team.card.completeCriteria(event.getCriteria(), timer.getTime()))
        {
            for (Player p : teamManager.getParticipants())
            {
                p.playSound(p, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 0.8f, 1.0f);
            }
            scoreboard.updateItemCount();

            if (team.card.hasBingo(team))
            {
                bingo(team);
            }
        }
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

        BingoTeam team = teamManager.getTeamOfPlayer(player);

        if (deathMatchItem != null && deathMatchItem.equals(item))
        {
            bingo(team);
        }

        BingoCard card = team.card;
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
            BingoTeam playerTeam = teamManager.getTeamOfPlayer(event.getPlayer());
            if (playerTeam == null)
            {
                BingoReloaded.print("NO TEAM?", event.getPlayer());
                return;
            }

            BingoCard card = playerTeam.card;

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
                    teleportPlayerUp(event.getPlayer(), -ConfigData.getConfig().wandDown, 0);
                }
                else
                {
                    teleportPlayerUp(event.getPlayer(), ConfigData.getConfig().wandUp, 5);
                }

                event.getPlayer().playSound(event.getPlayer(), Sound.ENTITY_SHULKER_TELEPORT, 0.8f, 1.0f);
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, ONE_SECOND * 10, 100, false, false));
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

        if (currentEffects.contains(EffectOptionFlags.NO_FALL_DAMAGE))
        {
            event.setCancelled(true);
        }
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

            BingoTeam team = RecoveryCardData.getActiveTeamOfPlayer(event.getPlayer(), teamManager);
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
                if (ConfigData.getConfig().teleportAfterDeath)
                {
                    TextComponent[] teleportMsg = BingoReloaded.createHoverCommandMessage("",
                            "" + ChatColor.DARK_AQUA + ChatColor.BOLD + "Click here to teleport back to where you died",
                            "",
                            "/bingo back",
                            "Click to teleport to " + deathCoords);

                    event.getEntity().spigot().sendMessage(teleportMsg);
                    deadPlayers.put(event.getEntity().getName(), deathCoords);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerHoldsCard(final PlayerItemHeldEvent event)
    {
        if (teamManager.getParticipants().contains(event.getPlayer()) && gameInProgress)
        {
            if (event.getNewSlot() == currentKit.cardItem.getSlot() && currentEffects.contains(EffectOptionFlags.CARD_SPEED))
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

    @EventHandler
    public void onPlayerRespawnEvent(final PlayerRespawnEvent event)
    {
        if (deadPlayers.containsKey(event.getPlayer().getName()))
        {
            returnCardToPlayer(event.getPlayer());
        }
    }

    public void returnCardToPlayer(Player player)
    {
        if (!gameInProgress)
            return;

        while (player.getInventory().contains(currentKit.cardItem.getAsStack()))
            player.getInventory().remove(currentKit.cardItem.getAsStack());

        player.getInventory().setItem(8, currentKit.cardItem.getAsStack());

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                givePlayerEffects(player);
            }
        }.runTaskLater(BingoReloaded.getPlugin(BingoReloaded.class), ONE_SECOND);
    }

    private boolean isOceanBiome(Biome biome)
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
        switch (ConfigData.getConfig().playerTeleportStrategy)
        {
            case ALONE:
                for (Player p : teamManager.getParticipants())
                {
                    Location playerLoc = getRandomSpawnLocation(world);

                    p.teleport(playerLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);

                    if (teamManager.getParticipants().size() > 0)
                        spawnPlatform(playerLoc, 5);
                }
                break;

            case TEAM:
                for (BingoTeam t: teamManager.getActiveTeams())
                {
                    Location teamLocation = getRandomSpawnLocation(world);

                    Set<Player> teamPlayers = teamManager.getPlayersOfTeam(t);
                    for (Player p : teamPlayers)
                    {
                        p.teleport(teamLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    }

                    if (teamManager.getParticipants().size() > 0)
                        spawnPlatform(teamLocation, 5);
                }
                break;

            case ALL:
                Location spawnLocation = getRandomSpawnLocation(world);

                Set<Player> players = teamManager.getParticipants();
                for (Player p : players)
                {
                    p.teleport(spawnLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
                }

                if (teamManager.getParticipants().size() > 0)
                    spawnPlatform(spawnLocation, 5);
                break;
        }
    }

    private Location getRandomSpawnLocation(World world)
    {
        Vector position = Vector.getRandom().multiply(TELEPORT_DISTANCE);
        Location location = new Location(world, position.getX(), ConfigData.getConfig().lobbySpawnHeight, position.getZ());

        //find a not ocean biome to start the game in
        while (isOceanBiome(world.getBiome(location)))
        {
            position = Vector.getRandom().multiply(TELEPORT_DISTANCE);
            location = new Location(world, position.getX(), ConfigData.getConfig().lobbySpawnHeight, position.getZ());
        }

        return location;
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
