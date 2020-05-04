package org.runaway.enums;

import net.minecraft.server.v1_12_R1.Entity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.runaway.entity.CustomEntity;
import org.runaway.entity.bosses.Blaze;
import org.runaway.entity.bosses.Spider;
import org.runaway.entity.mobs.Rat;
import org.runaway.entity.mobs.Zombie;

import java.util.Arrays;

public enum Mobs {
    RAT(60, "rat", Rat.class, true, null),
    SPIDER(52, "spider", Spider.class, false, Material.SPIDER_EYE),
    BLAZE(61, "blaze", Blaze.class, false, Material.BLAZE_POWDER),
    ZOMBIE(54, "zombie", Zombie.class, true, null);

    String name;
    int id;

    boolean multispawn;
    ItemStack icon;
    Class<? extends Entity> custom;

    Mobs(int id, String name, Class<? extends Entity> custom, boolean multispawn, Material icon) {
        this.id = id;
        this.name = name;
        if (icon != null) this.icon = new ItemStack(icon, 1);
        this.multispawn = multispawn;
        this.custom = custom;
    }

    public static void registerMobs() {
        Arrays.stream(values()).forEach(mobs -> CustomEntity.registerCustomEntity(mobs.id, mobs.name, mobs.custom));
    }

    public Class<? extends Entity> getCustom() {
        return custom;
    }

    public boolean isMultispawn() {
        return multispawn;
    }

    public ItemStack getIcon() {
        return icon;
    }
}
