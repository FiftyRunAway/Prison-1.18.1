package org.runaway.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.runaway.entity.mobs.*;

import java.util.Arrays;

public enum MobType {
    SILVERFISH(EntityType.SILVERFISH, SilverfishEntity.class, null),
    ZOMBIE(EntityType.ZOMBIE, CustomMonsterEntity.class, null),
    SKELETON(EntityType.SKELETON, SkeletonEntity.class, null),
    WOLF(EntityType.WOLF, WolfEntity.class, null),
    SPIDER(EntityType.SPIDER, SpiderEntity.class, Material.COBWEB),
    SLIME(EntityType.SLIME, SlimeEntity.class, Material.SLIME_BALL),
    GOLEM(EntityType.IRON_GOLEM, GolemEntity.class, Material.IRON_INGOT),
    BLAZE(EntityType.BLAZE, BlazeEntity.class, Material.BLAZE_ROD),
    WITHERSKELETON(EntityType.WITHER_SKELETON, WitherSkeletonEntity.class, Material.STONE_SWORD),
    BEAR(EntityType.ZOMBIE, BearEntity.class, Material.SNOWBALL);

    String name;
    EntityType type;
    ItemStack icon;
    Class<? extends Entity> custom;

    MobType(EntityType type, Class<? extends Entity> custom, Material icon) {
        this.type = type;
        this.name = custom.getSimpleName().toLowerCase();
        if (icon != null) this.icon = new ItemStack(icon, 1);
        this.custom = custom;
    }
/*
    public static void registerMobs() {
        Arrays.stream(values()).forEach(mobs -> CustomEntity.registerCustomEntity(mobs.id, mobs.custom.getSimpleName().toLowerCase(), mobs.custom));
    }
*/
    public Class<? extends Entity> getCustom() {
        return custom;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public EntityType getType() {
        return type;
    }
}
