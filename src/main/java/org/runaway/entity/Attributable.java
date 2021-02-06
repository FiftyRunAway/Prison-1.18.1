package org.runaway.entity;

import net.minecraft.server.v1_12_R1.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.runaway.enums.MobType;

import java.util.function.Consumer;

public interface Attributable {
    String getName();

    String getTechName();

    double getDamage();

    double getSpeed();

    int getRegenerationDelay();

    int getRegenerationValue();

    int getMobLevel();

    double getHealth();

    boolean isBoss();

    MobType getMobType();

    MobLoot getMobLoot();

    Consumer<LivingEntity> getOnSpawnConsumer();
}
