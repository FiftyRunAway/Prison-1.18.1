package org.runaway.entity;

import net.minecraft.server.v1_12_R1.Entity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.runaway.entity.mobs.CustomMonsterEntity;
import org.runaway.entity.mobs.SilverfishEntity;
import org.runaway.entity.mobs.SlimeEntity;
import org.runaway.entity.mobs.SpiderEntity;

import java.util.Arrays;

public enum MobType {
    SILVERFISH(60, SilverfishEntity.class, null),
    ZOMBIE(54, CustomMonsterEntity.class, null),
    SPIDER(52, SpiderEntity.class, Material.WEB),
    SLIME(55, SlimeEntity.class, Material.SLIME_BALL);

    String name;
    int id;
    ItemStack icon;
    Class<? extends Entity> custom;

    MobType(int id, Class<? extends Entity> custom, Material icon) {
        this.id = id;
        this.name = custom.getSimpleName().toLowerCase();
        if (icon != null) this.icon = new ItemStack(icon, 1);
        this.custom = custom;
    }

    public static void registerMobs() {
        Arrays.stream(values()).forEach(mobs -> CustomEntity.registerCustomEntity(mobs.id, mobs.custom.getSimpleName().toLowerCase(), mobs.custom));
    }

    public Class<? extends Entity> getCustom() {
        return custom;
    }

    public ItemStack getIcon() {
        return icon;
    }
}
