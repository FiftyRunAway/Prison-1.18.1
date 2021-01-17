package org.runaway.entity.mobs;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.runaway.entity.Attributable;
import org.runaway.entity.IMobController;
import org.runaway.utils.Utils;

public class Rat extends EntityMonster {

    private IMobController iMobController;
    private Attributable attributable;
    public Rat(IMobController iMobController) {
        super(((CraftWorld)iMobController.getSpawnLocation().getWorld()).getHandle());
        this.attributable = iMobController.getAttributable();
        this.setCustomName(Utils.colored(attributable.getName()));
        double health = attributable.getHealth();
        double damage = attributable.getDamage();
        double speed = attributable.getSpeed();
        if (iMobController.isRare()) {
            this.glowing = true;
            damage += 1;
            health *= 1.5;
            speed *= 1.5;
            iMobController.setRare(true);
        }
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(128.0);
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(health);
        this.getAttributeInstance(GenericAttributes.c).setValue(-1);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(speed);
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
        this.canPickUpLoot = false;
        this.fireProof = true;
        this.persistent = true;
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (damagesource.getEntity() != null) {
            if (damagesource.getEntity().getBukkitEntity().getType() == EntityType.PLAYER) {
                int damage = Math.round(getHealth() - f);
                if (damage < 0) damage = 0;
                //Prison.gamers.get(Bukkit.getPlayer(damagesource.getEntity().getBukkitEntity().getName()).getUniqueId()).sendTitle("&c" + damage + "♥",  "&c" + this.name);
            }
        }
        return super.damageEntity(damagesource, f);
    }

    public void n() {
        try {
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
                        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(attributable.getSpeed());
                    } else {
                        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.0);
                    }
                }
            }
        } catch (Exception ignored) { }
    }

    public void die() {

        super.die();
    }
}
