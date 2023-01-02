package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.data.*;
import io.github.steaf23.bingoreloaded.event.BingoGameEvent;
import io.github.steaf23.bingoreloaded.gui.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.gui.cards.BingoCard;
import io.github.steaf23.bingoreloaded.gui.cards.CardBuilder;
import io.github.steaf23.bingoreloaded.event.BingoCardSlotCompleteEvent;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.item.ItemTextBuilder;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.player.PlayerKit;
import io.github.steaf23.bingoreloaded.player.TeamManager;
import io.github.steaf23.bingoreloaded.util.FlexibleColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
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
import net.md_5.bungee.api.ChatColor;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BingoGame implements Listener
{
    public boolean inProgress;

    private final BingoScoreboard scoreboard;
    private final GameTimer timer;
    private BingoGameSettings settings;
    private final Map<UUID, Location> deadPlayers;

    public BingoGame()
    {
        this.settings = new BingoGameSettings();
        this.inProgress = false;
        this.scoreboard = new BingoScoreboard(this);
        this.timer = new GameTimer(scoreboard);
        this.deadPlayers = new HashMap<>();

        BingoReloaded.registerListener(this);
    }

    public void start()
    {
        start(0);
    }

    public void start(int seed)
    {
        if (!BingoCardsData.getCardNames().contains(settings.card))
        {
            new Message("game.start.no_card").color(ChatColor.RED).arg(settings.card).sendAll();
            return;
        }

        // Pre-start Setup
        if (getTeamManager().getParticipants().size() <= 0)
        {
            new Message("game.start.no_players").color(ChatColor.RED).sendAll();
            return;
        }
        if (inProgress)
        {
            new Message("game.start.already_started").color(ChatColor.RED).sendAll();
            return;
        }

        settings.deathMatchItem = null;
        getTeamManager().updateActivePlayers();
        getTeamManager().removeEmptyTeams();
        World world = Bukkit.getWorlds().get(0);
        world.setStorm(false);
        world.setTime(1000);

        // Start
        inProgress = true;

        BingoCard masterCard = CardBuilder.fromMode(settings.mode, settings.cardSize, this);
        masterCard.generateCard(settings.card, seed);
        getTeamManager().initializeCards(masterCard);

        new Message("game.start.give_cards").sendAll();
        Set<Player> players = getTeamManager().getParticipants();
        players.forEach(this::givePlayerKit);
        players.forEach(this::returnCardToPlayer);
        players.forEach(p -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "advancement revoke " + p.getName() + " everything"));
        players.forEach(p -> {p.setLevel(0); p.setExp(0.0f);});
        teleportPlayersToStart(world);
        givePlayersEffects();

        // Post-start Setup
        scoreboard.resetBoards();
        scoreboard.updateItemCount();
        timer.start();
    }

    public void resume()
    {
        inProgress = true;
        scoreboard.updateItemCount();
    }

    public void end()
    {
        settings.deathMatchItem = null;
        timer.stop();
        if(!inProgress)
            return;

        inProgress = false;
        TextComponent[] commandMessage = Message.createHoverCommandMessage("game.end.restart", "/bingo start");
        Set<Player> players = getTeamManager().getParticipants();
        players.forEach(p -> p.spigot().sendMessage(commandMessage));
        new Message("game.end.duration").color(ChatColor.GREEN)
                .arg(GameTimer.getTimeAsString(timer.getTime())).color(ChatColor.WHITE)
                .sendAll();
        RecoveryCardData.markCardEnded(true);
        players.forEach(p -> takePlayerEffects(p));
        scoreboard.resetBoards();
    }

    public void bingo(BingoTeam team)
    {
        new Message("game.end.bingo").arg(FlexibleColor.fromName(team.getName()).getTranslation()).color(team.getColor()).bold().sendAll();
        for (Player p : getTeamManager().getParticipants())
        {
            p.playSound(p, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 0.8f, 1.0f);
            p.playSound(p, Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.75f, 1.0f);

            if (getTeamManager().getTeamOfPlayer(p).equals(team))
            {
                BingoStatsData.incrementPlayerStat(p.getUniqueId(), BingoStatType.WINS);
            }
            else
            {
                BingoStatsData.incrementPlayerStat(p.getUniqueId(), BingoStatType.LOSSES);
            }
        }
        end();
    }

    public int getGameTime()
    {
        return timer.getTime();
    }

    public BingoGameSettings getSettings()
    {
        return settings;
    }

    public TeamManager getTeamManager()
    {
        return scoreboard.getTeamManager();
    }

    public void givePlayerKit(Player p)
    {
        p.getInventory().clear();
        p.closeInventory();
        FlexibleColor teamColor = FlexibleColor.fromChatColor(getTeamManager().getTeamOfPlayer(p).getColor());

        if (teamColor == null) return;
        for(InventoryItem item : settings.kit.getItems(teamColor))
        {
            p.getInventory().setItem(item.getSlot(), item);
        }
    }

    public void returnCardToPlayer(Player player)
    {
        if (!inProgress)
            return;

        while (player.getInventory().contains(settings.kit.cardItem.getAsStack()))
            player.getInventory().remove(settings.kit.cardItem.getAsStack());

        player.getInventory().setItem(8, settings.kit.cardItem.getAsStack());

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                givePlayerEffects(player, settings);
            }
        }.runTaskLater(BingoReloaded.getPlugin(BingoReloaded.class), BingoReloaded.ONE_SECOND);
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
            for (Player p : getTeamManager().getParticipants())
            {
                p.sendTitle(color + "" + countdown, "", -1, -1, -1);
                Message.sendDebug(color + "" + countdown, p);
            }

            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    startDeathMatch(countdown - 1);
                }
            }.runTaskLater(BingoReloaded.getPlugin(BingoReloaded.class), BingoReloaded.ONE_SECOND);
            return;
        }

        settings.deathMatchItem = settings.generateDeathMatchItem();

        for (Player p : getTeamManager().getParticipants())
        {
            showDeathMatchItem(p);
            p.sendTitle("" + ChatColor.GOLD + ChatColor.GOLD + "GO", "" + ChatColor.DARK_PURPLE + ChatColor.ITALIC + "find the item listed in the chat to win!", -1, -1, -1);
        }
    }

    public void showDeathMatchItem(Player p)
    {
        String itemKey = ItemTextBuilder.getItemKey(settings.deathMatchItem);

        new Message("game.item.deathmatch").color(ChatColor.GOLD)
                .component(new TranslatableComponent(itemKey))
                .send(p);
    }

    public static void givePlayerEffects(Player player, BingoGameSettings settings)
    {
        takePlayerEffects(player);

        if (settings.effects.contains(EffectOptionFlags.NIGHT_VISION))
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100000, 1, false, false));
        if (settings.effects.contains(EffectOptionFlags.WATER_BREATHING))
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 100000, 1, false, false));
        if (settings.effects.contains(EffectOptionFlags.FIRE_RESISTANCE))
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100000, 1, false, false));

        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 2, 100, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 2, 100, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, BingoReloaded.ONE_SECOND * ConfigData.instance.gracePeriod, 100, false, false));
    }

    public static void takePlayerEffects(Player player)
    {
        for (PotionEffectType effect : PotionEffectType.values())
        {
            player.removePotionEffect(effect);
        }
    }

    public void givePlayersEffects()
    {
        Set<Player> players = getTeamManager().getParticipants();
        players.forEach((p) -> {
            givePlayerEffects(p, settings);
            p.setGameMode(GameMode.SURVIVAL);
        });
    }

    public void teleportPlayerAfterDeath(Player player)
    {
        if (player == null) return;
        Location location = deadPlayers.get(player.getUniqueId());
        Message.sendDebug("Death location" + location, player);
        player.teleport(deadPlayers.get(player.getUniqueId()), PlayerTeleportEvent.TeleportCause.PLUGIN);
        deadPlayers.remove(player.getUniqueId());
    }

    public static void spawnPlatform(Location spawnLocation, int size)
    {
        for (int x = -size; x < size + 1; x++)
        {
            for (int z = -size; z < size + 1; z++)
            {
                if (!spawnLocation.getWorld().getType(
                     (int)spawnLocation.getX() + x,
                    (int)spawnLocation.getY() - 20,
                    (int)spawnLocation.getZ() + z).isSolid())
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

    public static void removePlatform(Location platformLocation, int size)
    {
        for (int x = -size; x < size + 1; x++)
        {
            for (int z = -size; z < size + 1; z++)
            {
                if (platformLocation.getWorld().getType(
                        (int)platformLocation.getX() + x,
                        (int)platformLocation.getY() - 20,
                        (int)platformLocation.getZ() + z) == Material.WHITE_STAINED_GLASS)
                {
                    platformLocation.getWorld().setType(
                            (int)platformLocation.getX() + x,
                            (int)platformLocation.getY() - 20,
                            (int)platformLocation.getZ() + z,
                            Material.AIR);
                }
            }
        }
    }

    private void teleportPlayersToStart(World world)
    {
        switch (ConfigData.instance.playerTeleportStrategy)
        {
            case ALONE:
                for (Player p : getTeamManager().getParticipants())
                {
                    Location playerLoc = getRandomSpawnLocation(world);

                    p.teleport(playerLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    Location bedSpawn = playerLoc.clone();
                    bedSpawn.setY(bedSpawn.getWorld().getHighestBlockYAt(bedSpawn.getBlockX(), bedSpawn.getBlockZ()) + 2);
                    p.setBedSpawnLocation(bedSpawn, true);

                    if (getTeamManager().getParticipants().size() > 0)
                    {
                        spawnPlatform(playerLoc, 5);

                        new BukkitRunnable()
                        {
                            @Override
                            public void run()
                            {
                                BingoGame.removePlatform(playerLoc, 5);
                            }
                        }.runTaskLater(Bukkit.getPluginManager().getPlugin(BingoReloaded.NAME),
                                (Math.max(0, ConfigData.instance.gracePeriod - 5)) * BingoReloaded.ONE_SECOND);
                    }
                }
                break;

            case TEAM:
                for (BingoTeam t: getTeamManager().getActiveTeams())
                {
                    Location teamLocation = getRandomSpawnLocation(world);

                    Set<Player> teamPlayers = getTeamManager().getPlayersOfTeam(t);
                    for (Player p : teamPlayers)
                    {
                        p.teleport(teamLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
                        Location bedSpawn = teamLocation.clone();
                        bedSpawn.setY(bedSpawn.getWorld().getHighestBlockYAt(bedSpawn.getBlockX(), bedSpawn.getBlockZ()) + 2);
                        p.setBedSpawnLocation(bedSpawn, true);
                    }

                    if (getTeamManager().getParticipants().size() > 0)
                    {
                        spawnPlatform(teamLocation, 5);

                        new BukkitRunnable()
                        {
                            @Override
                            public void run()
                            {
                                BingoGame.removePlatform(teamLocation, 5);
                            }
                        }.runTaskLater(Bukkit.getPluginManager().getPlugin(BingoReloaded.NAME),
                                (Math.max(0, ConfigData.instance.gracePeriod - 7)) * BingoReloaded.ONE_SECOND);
                    }
                }
                break;

            case ALL:
                Location spawnLocation = getRandomSpawnLocation(world);

                Set<Player> players = getTeamManager().getParticipants();
                for (Player p : players)
                {
                    p.teleport(spawnLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    Location bedSpawn = spawnLocation.clone();
                    bedSpawn.setY(bedSpawn.getWorld().getHighestBlockYAt(bedSpawn.getBlockX(), bedSpawn.getBlockZ()) + 2);
                    p.setBedSpawnLocation(bedSpawn, true);
                }

                if (getTeamManager().getParticipants().size() > 0)
                {
                    spawnPlatform(spawnLocation, 5);

                    new BukkitRunnable()
                    {
                        @Override
                        public void run()
                        {
                            BingoGame.removePlatform(spawnLocation, 5);
                        }
                    }.runTaskLater(Bukkit.getPluginManager().getPlugin(BingoReloaded.NAME),
                            ((long)Math.max(0, ConfigData.instance.gracePeriod - 7)) * BingoReloaded.ONE_SECOND);
                }
                break;
            default:
                return;
        }
    }

    private static Location getRandomSpawnLocation(World world)
    {
        Vector position = Vector.getRandom().multiply(ConfigData.instance.teleportMaxDistance);
        Location location = new Location(world, position.getX(), ConfigData.instance.lobbySpawnHeight, position.getZ());

        //find a not ocean biome to start the game in
        while (isOceanBiome(world.getBiome(location)))
        {
            position = Vector.getRandom().multiply(ConfigData.instance.teleportMaxDistance);
            location = new Location(world, position.getX(), ConfigData.instance.lobbySpawnHeight, position.getZ());
        }

        return location;
    }

    private static boolean isOceanBiome(Biome biome)
    {
        return switch (biome)
                {
                    case OCEAN,
                            DEEP_COLD_OCEAN,
                            COLD_OCEAN,
                            DEEP_OCEAN,
                            FROZEN_OCEAN,
                            DEEP_FROZEN_OCEAN,
                            LUKEWARM_OCEAN,
                            DEEP_LUKEWARM_OCEAN,
                            WARM_OCEAN -> true;
                    default -> false;
                };
    }

    public void playerQuit(Player player)
    {
        if (!getTeamManager().getParticipants().contains(player)) return;

        getTeamManager().removePlayerFromAllTeams(player);
        new Message("game.player.leave").arg(ChatColor.RED + "/bingo join").send(player);
        BingoGame.takePlayerEffects(player);
        if (deadPlayers.containsKey(player.getUniqueId()))
        {
            deadPlayers.remove(player.getUniqueId());
        }
    }

    /**
     *
     * @param player
     * @param item
     * @return true if wand was used successfully
     */
    public static boolean useGoUpWand(Player player, ItemStack item)
    {
        if (!item.equals(PlayerKit.wandItem.getAsStack()))
        {
            return false;
        }

        if (ItemCooldownManager.isCooldownOver(player, item))
        {
            ItemCooldownManager.addCooldown(player, item, (int)(ConfigData.instance.wandCooldown * 1000));

            double distance = 0.0;
            double fallDistance = 5.0;
            // Use the wand
            if (player.isSneaking())
            {
                distance = -ConfigData.instance.wandDown;
                fallDistance = 0.0;
            }
            else
            {
                distance = ConfigData.instance.wandUp + 5;
                fallDistance = 5.0;
            }

            Location newLocation = player.getLocation();
            newLocation.setY(newLocation.getY() + distance + fallDistance);
            player.teleport(newLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
            newLocation.setY(newLocation.getY() - fallDistance);
            BingoGame.spawnPlatform(newLocation, 1);

            new BukkitRunnable() {
                @Override
                public void run()
                {
                    BingoGame.removePlatform(newLocation, 1);
                }
            }.runTaskLater(Bukkit.getPluginManager().getPlugin(BingoReloaded.NAME),
                    Math.max(0, ConfigData.instance.platformLifetime) * BingoReloaded.ONE_SECOND);

            player.playSound(player, Sound.ENTITY_SHULKER_TELEPORT, 0.8f, 1.0f);
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, BingoReloaded.ONE_SECOND * 10, 100, false, false));

            BingoStatsData.incrementPlayerStat(player.getUniqueId(), BingoStatType.WAND_USES);
            return true;
        }

        double timeLeft = ItemCooldownManager.getTimeLeft(player, item) / 1000.0;
        new Message("game.item.cooldown").color(ChatColor.RED).arg(String.format("%.2f", timeLeft)).send(player);
        return false;
    }

// @EventHandlers ========================================================================

    @EventHandler
    public void onBingoGameEvent(final BingoGameEvent event)
    {
        switch (event.eventName)
        {
            case "start_game":
                start();
                break;
            case "end_game":
                end();
                break;
        }
    }

    @EventHandler
    public void onCardSlotCompleteEvent(final BingoCardSlotCompleteEvent event)
    {
        BingoStatsData.incrementPlayerStat(event.getPlayer().getUniqueId(), BingoStatType.TASKS);
        for (Player p : getTeamManager().getParticipants())
        {
            p.playSound(p, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 0.8f, 1.0f);
        }
        if (event.hasBingo())
        {
            bingo(event.getTeam());
        }
        scoreboard.updateItemCount();
    }

    @EventHandler
    public void onPlayerDropItem(final PlayerDropItemEvent dropEvent)
    {
        if (dropEvent.getItemDrop().getItemStack().equals(settings.kit.cardItem.getAsStack()) ||
                dropEvent.getItemDrop().getItemStack().equals(PlayerKit.wandItem.getAsStack()))
        {
            dropEvent.setCancelled(true);
            return;
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
        if (!getTeamManager().getParticipants().contains(event.getPlayer()))
            return;

        if (event.getItem().equals(settings.kit.cardItem.getAsStack()))
        {
            event.setCancelled(true);
            BingoTeam playerTeam = getTeamManager().getTeamOfPlayer(event.getPlayer());
            if (playerTeam == null)
            {
                return;
            }

            BingoCard card = playerTeam.card;

            // if the player is actually participating, show it
            if (card != null)
            {
                if (settings.deathMatchItem != null)
                {
                    showDeathMatchItem(event.getPlayer());
                    return;
                }
                card.showInventory(event.getPlayer());
            }
            else
            {
                new Message("game.player.no_start").send(event.getPlayer());
            }
        }

        if (event.getItem().equals(PlayerKit.wandItem.getAsStack())
                && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK))
        {
            if (!inProgress)
            {
                new Message("game.player.no_start").send(event.getPlayer());
            }
            else if (useGoUpWand(event.getPlayer(), event.getItem()))
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event)
    {
        ItemStack item = event.getCurrentItem();
        if (item == null) return;

        if (item.equals(settings.kit.cardItem.getAsStack()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent event)
    {
        if (event.getCursor() == null) return;

        if (event.getCursor().equals(settings.kit.cardItem.getAsStack()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(final EntityDamageEvent event)
    {
        if (!(event.getEntity() instanceof Player player))
            return;
        if (!getTeamManager().getParticipants().contains(player))
            return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL)
            return;

        if (inProgress && settings.effects.contains(EffectOptionFlags.NO_FALL_DAMAGE))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event)
    {
        if (inProgress)
        {
            if (getTeamManager().getParticipants().contains(event.getPlayer()))
            {
                new Message("game.player.join_back").send(event.getPlayer());
                scoreboard.updateItemCount();
                return;
            }

            BingoTeam team = RecoveryCardData.getActiveTeamOfPlayer(event.getPlayer(), getTeamManager());
            if (team == null)
                return;

            getTeamManager().addPlayerToTeam(event.getPlayer(), team.getName());
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
        if (inProgress)
        {
            if (getTeamManager().getParticipants().contains(event.getEntity()))
            {
                while (event.getDrops().contains(settings.kit.cardItem.getAsStack()))
                    event.getDrops().remove(settings.kit.cardItem.getAsStack());

                Location deathCoords = event.getEntity().getLocation();
                if (ConfigData.instance.teleportAfterDeath)
                {
                    TextComponent[] teleportMsg = Message.createHoverCommandMessage("game.player.respawn", "/bingo back");

                    event.getEntity().spigot().sendMessage(teleportMsg);
                    deadPlayers.put(event.getEntity().getUniqueId(), deathCoords);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerHoldsCard(final PlayerItemHeldEvent event)
    {
        if (getTeamManager().getParticipants().contains(event.getPlayer()) && inProgress)
        {
            if (event.getNewSlot() == settings.kit.cardItem.getSlot() && settings.effects.contains(EffectOptionFlags.CARD_SPEED))
            {
                event.getPlayer().addPotionEffect(
                        new PotionEffect(PotionEffectType.SPEED, 100000, 1, false, false));
            }

            if (event.getPreviousSlot() == settings.kit.cardItem.getSlot())
            {
                event.getPlayer().removePotionEffect(PotionEffectType.SPEED);
            }
        }
    }

    @EventHandler
    public void onPlayerRespawnEvent(final PlayerRespawnEvent event)
    {
        if (getTeamManager().getTeamOfPlayer(event.getPlayer()) == null)
        {
            event.getPlayer().setGameMode(GameMode.SPECTATOR);
        }

        if (deadPlayers.containsKey(event.getPlayer().getUniqueId()))
        {
            returnCardToPlayer(event.getPlayer());
        }
    }
}