package me.darkeyedragon.randomtp.world;

import io.papermc.lib.PaperLib;
import me.darkeyedragon.randomtp.api.world.RandomChunk;
import me.darkeyedragon.randomtp.api.world.RandomEnvironment;
import me.darkeyedragon.randomtp.api.world.RandomWorld;
import me.darkeyedragon.randomtp.api.world.RandomWorldBorder;
import me.darkeyedragon.randomtp.api.world.block.RandomBlock;
import me.darkeyedragon.randomtp.api.world.location.RandomLocation;
import me.darkeyedragon.randomtp.util.WorldUtil;
import me.darkeyedragon.randomtp.world.block.SpigotBlock;
import org.bukkit.World;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SpigotWorld implements RandomWorld {

    private final World world;

    public SpigotWorld(World world) {
        this.world = world;
    }

    @Override
    public UUID getUUID() {
        return world.getUID();
    }

    @Override
    public RandomBlock getHighestBlockAt(int x, int z) {
        return new SpigotBlock(world.getHighestBlockAt(x, z));
    }

    @Override
    public CompletableFuture<RandomChunk> getChunkAtAsync(RandomWorld world, int x, int z) {
        World regWorld = WorldUtil.toWorld(world);
        if (PaperLib.isPaper()) {
            return regWorld.getChunkAtAsyncUrgently(x, z).thenApply(SpigotChunk::new);
        }
        return PaperLib.getChunkAtAsync(regWorld, x, z).thenApply(SpigotChunk::new);
    }

    @Override
    public RandomBlock getBlockAt(RandomLocation location) {
        return new SpigotBlock(world.getBlockAt(location.getX(), location.getY(), location.getZ()));
    }

    @Override
    public String getName() {
        return world.getName();
    }

    @Override
    public RandomBlock getBlockAt(int x, int y, int z) {
        return new SpigotBlock(world.getBlockAt(x, y, z));
    }

    @Override
    public RandomWorldBorder getWorldBorder() {
        return new SpigotWorldBorder(world.getWorldBorder());
    }

    @Override
    public RandomEnvironment getEnvironment() {
        return WorldUtil.toRandomEnvironment(world.getEnvironment());
    }

    @Override
    public int hashCode() {
        return world.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(world == obj) return true;
        if(obj instanceof RandomWorld){
            return this.getUUID().equals(((RandomWorld)obj).getUUID());
        }
        return false;
    }
}
