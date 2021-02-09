package org.runaway.entity;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.runaway.utils.NMS;

import java.lang.invoke.MethodHandle;
import java.util.HashMap;
import java.util.Map;

public class CustomEntity extends RegistryMaterials {

    private static CustomEntity instance = null;

    private final BiMap<MinecraftKey, Class<? extends Entity>> customEntities = HashBiMap.create();
    private final Map<Class<? extends Entity>, MinecraftKey> customEntityClasses = this.customEntities.inverse();
    private final Map<Class<? extends Entity>, Integer> customEntityIds = new HashMap<>();
    private final RegistryMaterials wrapped;

    private CustomEntity(RegistryMaterials original) {
        this.wrapped = original;
    }

    public static Entity spawnEntity(MobType entityType, Location location, IMobController mobController) {
        try {
            Entity entity = entityType.getCustom().getConstructor(IMobController.class).newInstance(mobController);
            entity.setPosition(location.getX(), location.getY(), location.getZ());
            ((CraftWorld) location.getWorld()).getHandle().addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
            return entity;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static CustomEntity getInstance() {
        if (instance != null) {
            return instance;
        }
        instance = new CustomEntity(EntityTypes.b);
        try {
            //TODO: Update name on version change (RegistryMaterials)
            MethodHandle setter = NMS.getFinalSetter(EntityTypes.class, "b", false);
            setter.invoke(instance);  
        } catch (Exception e) {
            instance = null;
            e.printStackTrace();
            throw null;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return instance;
    }


    public static void registerCustomEntity(int entityId, String entityName, Class<? extends Entity> entityClass) {
        getInstance().putCustomEntity(entityId, entityName, entityClass);
    }

    private void putCustomEntity(int entityId, String entityName, Class<? extends Entity> entityClass) {
        MinecraftKey minecraftKey = new MinecraftKey(entityName);

        this.customEntities.put(minecraftKey, entityClass);
        this.customEntityIds.put(entityClass, entityId);
    }

    @Override
    public Class<? extends Entity> get(Object key) {
        if (this.customEntities.containsKey(key)) {
            return this.customEntities.get(key);
        }

        return (Class<? extends Entity>) wrapped.get(key);
    }

    @Override
    public int a(Object key) { //TODO: Update name on version change (getId)
        if (this.customEntityIds.containsKey(key)) {
            return this.customEntityIds.get(key);
        }

        return this.wrapped.a(key);
    }

    @Override
    public MinecraftKey b(Object value) { //TODO: Update name on version change (getKey)
        if (this.customEntityClasses.containsKey(value)) {
            return this.customEntityClasses.get(value);
        }

        return (MinecraftKey) wrapped.b(value);
    }
}
