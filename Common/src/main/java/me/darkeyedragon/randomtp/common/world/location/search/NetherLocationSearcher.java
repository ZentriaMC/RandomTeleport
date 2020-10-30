package me.darkeyedragon.randomtp.common.world.location.search;

import me.darkeyedragon.randomtp.api.config.Dimension;
import me.darkeyedragon.randomtp.api.world.RandomChunk;
import me.darkeyedragon.randomtp.api.world.block.RandomBlock;
import me.darkeyedragon.randomtp.api.world.location.RandomLocation;
import me.darkeyedragon.randomtp.api.world.location.search.BaseLocationSearcher;
import me.darkeyedragon.randomtp.common.plugin.RandomTeleportPlugin;
import me.darkeyedragon.randomtp.common.plugin.RandomTeleportPluginImpl;

public class NetherLocationSearcher extends BaseLocationSearcher {

    private final int MAX_HEIGHT = 120; //Everything above this is nether ceiling

    /**
     * A simple utility class to help with {@link org.bukkit.Location}
     *
     * @param plugin the plugin instance
     */
    public NetherLocationSearcher(RandomTeleportPlugin<RandomTeleportPluginImpl> plugin) {
        super(plugin.getInstance().getValidatorSet(), plugin.getConfigHandler().getSectionBlacklist().getBlacklist(), Dimension.NETHER);
    }

    /* Will search through the chunk to find a location that is safe, returning null if none is found. */
    @Override
    public RandomLocation getRandomLocationFromChunk(RandomChunk chunk) {
        for (int x = 8; x < CHUNK_SIZE; x++) {
            for (int z = 8; z < CHUNK_SIZE; z++) {
                for (int y = 0; y < MAX_HEIGHT; y++) {
                    RandomBlock block = chunk.getWorld().getBlockAt((chunk.getX() << CHUNK_SHIFT) + x, y, (chunk.getZ() << CHUNK_SHIFT) + z);
                    if (isSafe(block.getLocation())) {
                        return block.getLocation();
                    }
                }
            }
        }
        return null;
    }
}
