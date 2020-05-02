package org.runaway.entity;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.runaway.enums.Mobs;

import java.util.HashMap;

public interface IMob {

    void scheduleAbility();
    ItemStack[] drops();
    Entity getEntity();
    Mobs getType();
    BukkitTask[] getTasks();

    HashMap<String, Integer> getDamage();

    int getTotalDamage();
    void addTotalDamage(int damage);

    void setSpawner(Spawner spawner);

    String getConfigPath();
}
