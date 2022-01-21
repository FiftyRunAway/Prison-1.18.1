package org.runaway.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.LivingEntity;
import org.runaway.entity.skills.MobSkill;

import java.util.List;
import java.util.function.Consumer;

@Getter @Builder
public class PrisonMobPattern implements Attributable {
    String name, techName;
    int regenerationDelay, regenerationValue, mobLevel;
    double damage, speed, health;
    boolean boss;
    MobLoot mobLoot;
    MobType mobType;
    Armor armor;
    Consumer<LivingEntity> onSpawnConsumer;
    @Setter
    List<MobSkill> mobSkills;
}
