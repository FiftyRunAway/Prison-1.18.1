package org.runaway.entity;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public interface IMob {

    void scheduleAbility();
    ItemStack[] drops();
    Entity getEntity();
    MobType getType();
    BukkitTask[] getTasks();

    HashMap<String, Integer> getDamage();

    int getTotalDamage();
    void addTotalDamage(int damage);

    void setSpawner(Spawner spawner);

    String getConfigPath();
}
