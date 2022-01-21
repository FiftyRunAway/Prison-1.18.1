package org.runaway.entity;

import net.minecraft.world.entity.Entity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CustomEntity {

    public static Entity spawnEntity(MobType entityType, Location location, IMobController mobController) {
        try {
            Entity entity = entityType.getCustom().getConstructor(IMobController.class).newInstance(mobController);
            ((CraftWorld) location.getWorld()).getHandle().addFreshEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
            return entity;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
