package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.data.RecoveryCardData;
import io.github.steaf23.bingoreloaded.gui.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.gui.cards.BingoCard;
import io.github.steaf23.bingoreloaded.gui.cards.CardBuilder;
import io.github.steaf23.bingoreloaded.item.BingoCardSlotCompleteEvent;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.item.ItemCardSlot;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.player.TeamManager;
import io.github.steaf23.bingoreloaded.util.FlexibleColor;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BingoGame implements Listener
{
    public boolean inProgress;

    private final BingoScoreboard scoreboard;
    private final GameTimer timer;
    private BingoGameSettings settings;
    private final Map<String, Location> deadPlayers;
    private static final int TELEPORT_DISTANCE = ConfigData.getConfig().teleportMaxDistance;

    public BingoGame()
    {
        this.inProgress = false;
        this.scoreboard = new BingoScoreboard(this);
        this.timer = new GameTimer(scoreboard);
        this.settings = new BingoGameSettings();
        this.deadPlayers = new HashMap<>();

        BingoReloaded.registerListener(this);
    }

    public void start()
    {
        //TODO: Remove all advancements from all participants when starting.

        // Pre-start Setup
        if (getTeamManager().getParticipants().size() <= 0)
        {
            BingoReloaded.broadcast("" + ChatColor.RED + ChatColor.ITALIC + ChatColor.BOLD + "Bingo could not be started since nobody joined :(");
            return;
        }
        if (inProgress)
        {
            BingoReloaded.broadcast("" + ChatColor.RED + ChatColor.ITALIC + ChatColor.BOLD + "Cannot start a game of Bingo when there is already one active!");
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
        masterCard.generateCard(settings.card);
        getTeamManager().initializeCards(masterCard);

        BingoReloaded.broadcast(ChatColor.GREEN + "Giving all participants Kits and Cards!");
        Set<Player> players = getTeamManager().getParticipants();
        players.forEach(this::givePlayerKit);
        players.forEach(this::returnCardToPlayer);
        players.forEach(p -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "advancement revoke " + p.getName() + " everything"));

        teleportPlayersToStart(world);
        givePlayersEffects();

        // Post-start Setup
        scoreboard.resetBoards();
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
        if(inProgress)
        {
            inProgress = false;
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
            BingoReloaded.print(ChatColor.RED + "No game to end!");
            return;
        }
    }

    public void bingo(BingoTeam team)
    {
        BingoReloaded.broadcast("Congratulations! Team " + team.getName() + ChatColor.RESET + " has won the Bingo!");
        for (Player p : getTeamManager().getParticipants())
        {
            p.playSound(p, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 0.8f, 1.0f);
            p.playSound(p, Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.75f, 1.0f);
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
        FlexibleColor teamColor = FlexibleColor.fromChatColor(getTeamManager().getTeamOfPlayer(p).team.getColor());

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
                BingoReloaded.print(color + "" + countdown, p);
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
        String itemName = ItemCardSlot.convertToReadableName(settings.deathMatchItem);
        for (Player p : getTeamManager().getParticipants())
        {
            p.sendTitle(ChatColor.GOLD + itemName, ChatColor.DARK_PURPLE + "Death Match: Get this item to win!", -1, -1, -1);
            BingoReloaded.print(ChatColor.GOLD + itemName, p);
        }
    }

    public void showDeathMatchItem(Player p)
    {
        String itemName = ItemCardSlot.convertToReadableName(settings.deathMatchItem);
        p.sendTitle(ChatColor.GOLD + itemName, ChatColor.DARK_PURPLE + "DeathMatch - Find this item to win!", -1, -1, -1);
        BingoReloaded.print(ChatColor.GOLD + itemName, p);
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
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, BingoReloaded.ONE_SECOND * ConfigData.getConfig().gracePeriod, 100, false, false));
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
        Location location = deadPlayers.get(player.getName());
        BingoReloaded.print("Death Location: " + location, player);
        player.teleport(deadPlayers.get(player.getName()), PlayerTeleportEvent.TeleportCause.PLUGIN);
        deadPlayers.remove(player.getName());
    }

    public static void spawnPlatform(@Nullable Location spawnLocation, int size)
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

    private void teleportPlayersToStart(World world)
    {
        switch (ConfigData.getConfig().playerTeleportStrategy)
        {
            case ALONE:
                for (Player p : getTeamManager().getParticipants())
                {
                    Location playerLoc = getRandomSpawnLocation(world);

                    p.teleport(playerLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);

                    if (getTeamManager().getParticipants().size() > 0)
                        spawnPlatform(playerLoc, 5);
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
                    }

                    if (getTeamManager().getParticipants().size() > 0)
                        spawnPlatform(teamLocation, 5);
                }
                break;

            case ALL:
                Location spawnLocation = getRandomSpawnLocation(world);

                Set<Player> players = getTeamManager().getParticipants();
                for (Player p : players)
                {
                    p.teleport(spawnLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
                }

                if (getTeamManager().getParticipants().size() > 0)
                    spawnPlatform(spawnLocation, 5);
                break;
            default:
                return;
        }
    }

    private static Location getRandomSpawnLocation(World world)
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

    @EventHandler
    public void onCardSlotCompleteEvent(final BingoCardSlotCompleteEvent event)
    {
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
                dropEvent.getItemDrop().getItemStack().equals(settings.kit.wandItem.item.getAsStack()))
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
                BingoReloaded.print("NO TEAM?", event.getPlayer());
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
                BingoReloaded.print(ChatColor.RED + "The game has not started yet!", event.getPlayer());
            }
        }

        if (event.getItem().equals(settings.kit.wandItem.item.getAsStack())
                && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK))
        {
            if (settings.kit.wandItem.tryUse(event.getPlayer()))
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

        if (settings.effects.contains(EffectOptionFlags.NO_FALL_DAMAGE))
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
                BingoReloaded.print("You joined back!", event.getPlayer());
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
        if (deadPlayers.containsKey(event.getPlayer().getName()))
        {
            returnCardToPlayer(event.getPlayer());
        }
    }
}