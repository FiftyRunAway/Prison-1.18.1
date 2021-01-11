package org.runaway.entity.mobs;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.achievements.Achievement;
import org.runaway.entity.Spawner;
import org.runaway.enums.EConfig;
import org.runaway.enums.EStat;
import org.runaway.events.custom.KillRatsEvent;
import org.runaway.utils.Utils;

import java.util.concurrent.ThreadLocalRandom;

public class Rat extends EntityMonster {

    private Spawner spawner;
    private double health;
    private String name;
    private int hpDelay;
    private double speed;

    public Rat(Spawner spawner) {
        super(((CraftWorld)spawner.getSpawnLocation().getWorld()).getHandle());
        ConfigurationSection section = EConfig.MOBS.getConfig().getConfigurationSection("rat");
        this.health = section.getDouble("health");
        this.hpDelay = 20;
        this.speed = section.getDouble("speed");
        double damage = section.getDouble("damage");
        this.name = section.getString("name");
        this.setCustomName(Utils.colored(this.name));
        if (Math.random() < 0.04) {
            this.glowing = true;
            damage += 1;
            this.health *= 1.5;
            this.speed += this.speed / 2;
            this.setCustomName(Utils.colored(this.name.concat(" &7(&dРедкая&7)")));
        }
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(128.0);
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(this.health);
        this.getAttributeInstance(GenericAttributes.c).setValue(-1);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(this.speed);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(damage);
        ((LivingEntity)this.getBukkitEntity()).setRemoveWhenFarAway(false);
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, (float)128.0));
        this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 10.0));
        this.goalSelector.a(7, new PathfinderGoalRandomStroll(this, 1.0));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 64.0f));
        this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, damage, true));
        this.setHealth((float) health);
        this.setCustomNameVisible(true);
        this.spawner = spawner;
        this.canPickUpLoot = false;
        this.fireProof = true;
        this.persistent = true;
        this.spawner.register(this);
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (damagesource.getEntity() != null) {
            if (damagesource.getEntity().getBukkitEntity().getType() == EntityType.PLAYER) {
                int damage = Math.round(getHealth() - f);
                if (damage < 0) damage = 0;
                //Main.gamers.get(Bukkit.getPlayer(damagesource.getEntity().getBukkitEntity().getName()).getUniqueId()).sendTitle("&c" + damage + "♥",  "&c" + this.name);
            }
        }
        return super.damageEntity(damagesource, f);
    }

    public void n() {
        try {
            if (this.hpDelay <= 0) {
                if (this.getHealth() < this.health) {
                    this.heal(0.5f);
                }
                this.hpDelay = 20;
            }
            --this.hpDelay;
            super.n();
            if (this.getGoalTarget() != null) {
                if (this.getGoalTarget() != null && this.getGoalTarget().world != this.world) {
                    this.setGoalTarget(null);
                } else if (!(this.getGoalTarget() instanceof EntityPlayer)) {
                    this.setGoalTarget(null);
                } else if (this.getGoalTarget() != null && this.passengers.isEmpty()) {
                    boolean isSameWorld = this.getBukkitEntity().getLocation().getWorld() == this.getGoalTarget().getBukkitEntity().getLocation().getWorld();
                    double distance = isSameWorld ? this.getBukkitEntity().getLocation().distance(this.getGoalTarget().getBukkitEntity().getLocation()) : 32.0;
                    if (distance <= 16.0 && this.getGoalTarget() != null) {
                        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(this.speed);
                    } else {
                        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.0);
                    }
                }
            }
        } catch (Exception ignored) { }
    }

    public void die() {
        if (!Main.bosses.contains(this.getUniqueID())) return;
        try {
            if (this.spawner != null) {
                this.spawner.dead();
            }
            if (this.killer != null && this.killer instanceof EntityPlayer) {
                if (this.spawner != null) {
                    Gamer gamer = Main.gamers.get(Bukkit.getPlayer(killer.getName()).getUniqueId());
                    double money = (10.0 + ThreadLocalRandom.current().nextInt(15)) / 100.0;
                    int rats = 1;
                    if (this.glowing) {
                        money++; rats++;
                        Achievement.RARE_RAT.get(Bukkit.getPlayer(this.killer.getName()), false);
                    }
                    gamer.depositMoney(money);
                    Bukkit.getServer().getPluginManager().callEvent(new KillRatsEvent(gamer.getPlayer(), this.glowing));
                    gamer.setStatistics(EStat.RATS, (int)gamer.getStatistics(EStat.RATS) + rats);
                    if ((int)gamer.getStatistics(EStat.RATS) == 15) {
                        Achievement.FIFTEEN_RATS.get(gamer.getPlayer(), false);
                    }
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        super.die();
    }
}
