package org.runaway.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.bukkit.Location;
import org.runaway.tasks.Cancellable;

import java.util.List;
import java.util.Map;

public interface IMobController {
    Attributable getAttributable();

    Location getSpawnLocation();

    int getRespawnTime();

    void setMobRare(MobRare mobRare);

    MobRare getMobRare();

    Map<String, DamageInfo> getDamageMap();

    void setTotalDamage(double totalDamage);

    double getTotalDamage();

    String getUID();

    List<Cancellable> getMobTasks();

    Entity getNmsEntity();

    void kill();

    void spawn();

    boolean onDamage(DamageSource damageSource, float f);

    void die();

    boolean isAlive();

    long getLastDeathTime();

    int getRespawnTimeLeft();
}
