package org.runaway.entity;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import org.runaway.enums.MobType;

import java.util.function.Consumer;

@Getter @Builder
public class PrisonMobPattern implements Attributable {
    String name, techName;
    int regenerationDelay, regenerationValue, mobLevel;
    double damage, speed, health;
    boolean boss;
    MobLoot mobLoot;
    MobType mobType;
    Consumer<LivingEntity> onSpawnConsumer;
}
