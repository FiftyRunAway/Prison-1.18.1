package org.runaway.entity;

import org.bukkit.entity.LivingEntity;
import org.runaway.entity.skills.MobSkill;

import java.util.List;
import java.util.function.Consumer;

public interface Attributable {
    String getName();

    String getTechName();

    double getDamage();

    double getSpeed();

    int getRegenerationDelay();

    int getRegenerationValue();

    int getMobLevel();

    void setMobSkills(List<MobSkill> mobSkills);

    List<MobSkill> getMobSkills();

    double getHealth();

    boolean isBoss();

    MobType getMobType();

    MobLoot getMobLoot();

    Consumer<LivingEntity> getOnSpawnConsumer();

    Armor getArmor();
}
