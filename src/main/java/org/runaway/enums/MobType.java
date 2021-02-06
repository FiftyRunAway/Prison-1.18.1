package org.runaway.enums;

import net.minecraft.server.v1_12_R1.Entity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.runaway.entity.CustomEntity;
import org.runaway.entity.mobs.CustomMonsterEntity;
import org.runaway.entity.mobs.RatEntity;

import java.util.Arrays;

public enum MobType {
    RAT(60, RatEntity.class, null),
    ZOMBIE(54, CustomMonsterEntity.class, null);

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
        Arrays.stream(values()).forEach(mobs -> CustomEntity.registerCustomEntity(mobs.id, mobs.name().toLowerCase(), mobs.custom));
    }

    public Class<? extends Entity> getCustom() {
        return custom;
    }

    public ItemStack getIcon() {
        return icon;
    }
}
