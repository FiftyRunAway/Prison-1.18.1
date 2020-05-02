package org.runaway.entity;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityTypes;
import net.minecraft.server.v1_12_R1.MinecraftKey;
import net.minecraft.server.v1_12_R1.RegistryMaterials;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.runaway.Main;
import org.runaway.enums.Mobs;

public class CustomEntity extends RegistryMaterials {

    private static CustomEntity instance = null;

    private final BiMap<MinecraftKey, Class<? extends Entity>> customEntities = HashBiMap.create();
    private final BiMap<Class<? extends Entity>, MinecraftKey> customEntityClasses = this.customEntities.inverse();
    private final Map<Class<? extends Entity>, Integer> customEntityIds = new HashMap<>();
    private final RegistryMaterials wrapped;

    private CustomEntity(RegistryMaterials original) {
        this.wrapped = original;
    }

    public static void spawnEntity(Mobs entityType, Location location, Spawner spawner) {
        try {
            Entity entity = entityType.getCustom().getConstructor(Spawner.class).newInstance(spawner);
            entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            ((CraftWorld)location.getWorld()).getHandle().addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
            Main.bosses.add(entity.getUniqueID());
        } catch (Exception ex) { }
    }

    public static CustomEntity getInstance() {
        if (instance != null) {
            return instance;
        }
        instance = new CustomEntity(EntityTypes.b);
        try {
            //TODO: Update name on version change (RegistryMaterials)
            Field registryMaterialsField = EntityTypes.class.getDeclaredField("b");
            registryMaterialsField.setAccessible(true);

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(registryMaterialsField, registryMaterialsField.getModifiers() & ~Modifier.FINAL);

            registryMaterialsField.set(null, instance);
        } catch (Exception e) {
            instance = null;

            throw new RuntimeException("Unable to override the old entity RegistryMaterials", e);
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
