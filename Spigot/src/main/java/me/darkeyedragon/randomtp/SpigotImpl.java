package me.darkeyedragon.randomtp;

import eu.mikroskeem.zentria.randomteleport.api.RandomTeleportAPI;
import me.darkeyedragon.randomtp.api.world.RandomWorld;
import me.darkeyedragon.randomtp.api.world.location.RandomLocation;
import me.darkeyedragon.randomtp.teleport.Teleport;
import me.darkeyedragon.randomtp.world.SpigotWorld;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.concurrent.CompletableFuture;

public class SpigotImpl extends JavaPlugin implements RandomTeleportAPI {

    private RandomTeleport randomTeleport;
    @Override
    public void onEnable() {
        saveDefaultConfig();
        randomTeleport = new RandomTeleport(this);
        randomTeleport.init();
    }

    public RandomTeleport getInstance() {
        return randomTeleport;
    }

    @Override
    public void onDisable() {

    }

    @Nullable
    @Override
    public RandomWorld getWorld(String name) {
        World world = getServer().getWorld(name);
        if (world == null) {
            return null;
        }
        return new SpigotWorld(world);
    }

    @Override
    public CompletableFuture<RandomLocation> getValidRandomLocation(RandomWorld world) {
        return Teleport.calculateRandomLocation(this.getInstance(), world, true);
    }
}
