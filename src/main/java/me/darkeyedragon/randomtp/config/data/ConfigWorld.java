package me.darkeyedragon.randomtp.config.data;

import me.darkeyedragon.randomtp.RandomTeleport;
import me.darkeyedragon.randomtp.location.WorldConfigSection;
import me.darkeyedragon.randomtp.world.LocationQueue;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.Set;


public class ConfigWorld {

    private Map<World, WorldConfigSection> worldConfigSectionMap;
    private final RandomTeleport plugin;
    public ConfigWorld(RandomTeleport plugin) {
        this.plugin = plugin;
    }

    public ConfigWorld set(Map<World, WorldConfigSection> worldConfigSectionMap){
        this.worldConfigSectionMap = worldConfigSectionMap;
        return this;
    }

    public WorldConfigSection get(World world){
        return worldConfigSectionMap.get(world);
    }

    public Map<World, WorldConfigSection> getWorldConfigSectionMap(){
        return  worldConfigSectionMap;
    }

    public Set<World> getWorlds(){
        return worldConfigSectionMap.keySet();
    }

    public boolean contains(World world){
        return worldConfigSectionMap.containsKey(world);
    }
    public boolean contains(WorldConfigSection worldConfigSection){
        return worldConfigSectionMap.containsKey(worldConfigSection.getWorld());
    }

    public LocationQueue add(WorldConfigSection worldConfigSection) {
        final ConfigurationSection section = plugin.getConfig().getConfigurationSection("worlds");
        if (section == null) {
            return null;
        }
        section.set(worldConfigSection.getWorld().getName() + ".use_worldborder", worldConfigSection.useWorldBorder());
        section.set(worldConfigSection.getWorld().getName() + ".needs_world_permission", worldConfigSection.needsWorldPermission());
        section.set(worldConfigSection.getWorld().getName() + ".radius", worldConfigSection.getRadius());
        section.set(worldConfigSection.getWorld().getName() + ".offsetX", worldConfigSection.getX());
        section.set(worldConfigSection.getWorld().getName() + ".offsetZ", worldConfigSection.getZ());
        plugin.saveConfig();
        worldConfigSectionMap.put(worldConfigSection.getWorld(), worldConfigSection);
        return generateLocations(worldConfigSection.getWorld());
    }

    private LocationQueue generateLocations(World world){
        WorldConfigSection worldConfigSection = plugin.getLocationFactory().getWorldConfigSection(world);
        ConfigQueue configQueue = plugin.getConfigHandler().getConfigQueue();
        LocationQueue locationQueue = new LocationQueue(configQueue.getSize(), plugin.getLocationSearcher());
        plugin.subscribe(locationQueue, world);
        locationQueue.generate(worldConfigSection, configQueue.getSize());
        plugin.getWorldQueue().put(world, locationQueue);
        return locationQueue;
    }
}
