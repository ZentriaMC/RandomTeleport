package me.darkeyedragon.randomtp.teleport;

import io.papermc.lib.PaperLib;
import me.darkeyedragon.randomtp.RandomTeleport;
import me.darkeyedragon.randomtp.api.world.location.RandomLocation;
import me.darkeyedragon.randomtp.api.world.location.search.LocationSearcher;
import me.darkeyedragon.randomtp.config.ConfigHandler;
import me.darkeyedragon.randomtp.eco.EcoFactory;
import me.darkeyedragon.randomtp.eco.EcoHandler;
import me.darkeyedragon.randomtp.exception.EcoNotSupportedException;
import me.darkeyedragon.randomtp.failsafe.DeathTracker;
import me.darkeyedragon.randomtp.util.MessageUtil;
import me.darkeyedragon.randomtp.util.location.LocationUtil;
import me.darkeyedragon.randomtp.world.location.WorldConfigSection;
import me.darkeyedragon.randomtp.world.location.search.LocationSearcherFactory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicBoolean;

public class Teleport {
    private static final Vector NULL_VECTOR = new Vector(0, 0, 0);

    private final RandomTeleport plugin;
    private final TeleportProperty property;
    private final ConfigHandler configHandler;
    private final Player player;
    private EcoHandler ecoHandler;

    public Teleport(RandomTeleport plugin, TeleportProperty property) {
        this.plugin = plugin;
        this.property = property;
        this.configHandler = property.getConfigHandler();
        this.player = property.getPlayer();
    }

    public void random() {
        final long delay;
        double price = configHandler.getSectionEconomy().getPrice();
        if (property.isUseEco()) {
            try {
                ecoHandler = EcoFactory.getInstance();
                if (!ecoHandler.hasEnough(player, price)) {
                    MessageUtil.sendMessage(plugin, player, configHandler.getSectionMessage().getSubSectionEconomy().getInsufficientFunds());
                    return;
                }
            } catch (EcoNotSupportedException e) {
                MessageUtil.sendMessage(plugin, property.getCommandSender(), ChatColor.RED + "Economy based features are disabled. Vault not found. Set the rtp cost to 0 or install vault.");
                Bukkit.getLogger().severe("Economy based features are disabled. Vault not found. Set the rtp cost to 0 or install vault.");
            }

        }
        //Teleport instantly if the command sender has bypass permission
        if (property.isIgnoreTeleportDelay()) {
            delay = 0;
        } else {
            delay = configHandler.getSectionTeleport().getDelay();
        }
        // Check if the player still has a cooldown active.
        if (property.getCooldown() > 0 && plugin.getCooldowns().containsKey(player.getUniqueId()) && !property.isBypassCooldown()) {
            long lastTp = plugin.getCooldowns().get(player.getUniqueId());
            long remaining = lastTp + property.getCooldown() - System.currentTimeMillis();
            boolean ableToTp = remaining < 0;
            if (!ableToTp) {
                MessageUtil.sendMessage(plugin, player, configHandler.getSectionMessage().getCountdown(remaining));
                return;
            }
        }
        if (delay == 0) {
            teleport();
        } else {
            MessageUtil.sendMessage(plugin, player, configHandler.getSectionMessage().getInitTeleportDelay(delay));
            AtomicBoolean complete = new AtomicBoolean(false);
            int taskId = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                complete.set(true);
                teleport();
            }, delay).getTaskId();
            Location originalLoc = player.getLocation().clone();
            if (configHandler.getSectionTeleport().isCancelOnMove()) {
                Bukkit.getScheduler().runTaskTimer(plugin, bukkitTask -> {
                    Location currentLoc = player.getLocation();
                    if (complete.get()) {
                        bukkitTask.cancel();
                    } else if ((originalLoc.getX() != currentLoc.getX() || originalLoc.getY() != currentLoc.getY() || originalLoc.getZ() != currentLoc.getZ())) {
                        Bukkit.getScheduler().cancelTask(taskId);
                        bukkitTask.cancel();
                        MessageUtil.sendMessage(plugin, player, configHandler.getSectionMessage().getTeleportCanceled());
                    }
                }, 0, 5L);
            }
        }
    }

    private void addToDeathTimer(Player player) {
        DeathTracker tracker = plugin.getDeathTracker();
        if (tracker.contains(player)) {
            tracker.getBukkitTask(player).cancel();
            tracker.remove(player);
        }
        plugin.getDeathTracker().add(player, configHandler.getSectionTeleport().getDeathTimer());
    }

    private void drawWarpParticles(Player player) {
        Location spawnLoc = player.getEyeLocation().add(player.getLocation().getDirection());
        player.getWorld().spawnParticle(Particle.CLOUD, spawnLoc, 20);
    }

    private void teleport() {
        final RandomLocation randomLocation = plugin.getWorldQueue().popLocation(property.getWorld());
        if (randomLocation == null) {
            MessageUtil.sendMessage(plugin, property.getCommandSender(), configHandler.getSectionMessage().getDepletedQueue());
            return;
        }
        Location location = LocationUtil.toLocation(randomLocation);
        PaperLib.getChunkAtAsync(location).thenApply(chunk -> {
            LocationSearcher baseLocationSearcher = LocationSearcherFactory.getLocationSearcher(property.getWorld(), plugin);
            if (!baseLocationSearcher.isSafe(randomLocation)) {
                // TODO: this sucks
                random();
                return null;
            }

            return chunk;
        }).thenCompose(chunk -> {
            if (chunk == null) {
                return null;
            }

            Block block = chunk.getWorld().getBlockAt(LocationUtil.toLocation(randomLocation));
            Location loc = block.getLocation().add(0.5, 2.0, 0.5);
            plugin.getCooldowns().put(player.getUniqueId(), System.currentTimeMillis());
            drawWarpParticles(player);
            player.setFallDistance(0.0F);
            player.setVelocity(NULL_VECTOR);
            return PaperLib.teleportAsync(player, loc);
        }).thenAccept(teleportSuccess -> {
            if (teleportSuccess != Boolean.FALSE) {
                return;
            }

            if (configHandler.getSectionTeleport().getDeathTimer() > 0) {
                addToDeathTimer(player);
            }
            if (property.isUseEco() && EcoFactory.isUseEco()) {
                ecoHandler.makePayment(player, configHandler.getSectionEconomy().getPrice());
                MessageUtil.sendMessage(plugin, player, configHandler.getSectionMessage().getSubSectionEconomy().getPayment());
            }
            drawWarpParticles(player);
            MessageUtil.sendMessage(plugin, player, configHandler.getSectionMessage().getTeleport(randomLocation));
        }).thenRun(() -> {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                WorldConfigSection worldConfigSection = plugin.getLocationFactory().getWorldConfigSection(property.getWorld());
                plugin.getWorldQueue().get(property.getWorld()).generate(worldConfigSection, 1);
            }, configHandler.getSectionQueue().getInitDelay());
        });
    }
}


