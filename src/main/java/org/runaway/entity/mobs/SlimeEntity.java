package org.runaway.entity.mobs;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftSlime;
import org.bukkit.entity.LivingEntity;
import org.runaway.entity.Attributable;
import org.runaway.entity.IMobController;
import org.runaway.entity.MobRare;
import org.runaway.utils.NMS;

import java.lang.reflect.Field;

public class SlimeEntity extends EntitySlime {

    private IMobController iMobController;
    private Attributable attributable;

    public SlimeEntity(IMobController mobController) {
        super(((CraftWorld) mobController.getSpawnLocation().getWorld()).getHandle());
        this.iMobController = mobController;
        this.attributable = iMobController.getAttributable();
        double health = attributable.getHealth();
        double speed = attributable.getSpeed();
        MobRare mobRare = iMobController.getMobRare();
        if (mobRare != MobRare.DEFAULT && mobRare != null) {
            this.glowing = true;
            health *= mobRare.getMultiplier();
            speed *= mobRare.getMultiplier();
        }
        setSize(4, true);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(128.0);
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(health);
        this.getAttributeInstance(GenericAttributes.c).setValue(-1);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(speed);
        ((LivingEntity)this.getBukkitEntity()).setRemoveWhenFarAway(false);
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, (float)128.0));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 64.0f));
        this.setHealth((float) health);
        this.setCustomNameVisible(true);
        this.canPickUpLoot = false;
        this.fireProof = true;
        this.persistent = true;
        mobController.getSpawnLocation().getChunk().load();
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if(!iMobController.onDamage(damagesource, f)) {
            return false;
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
                    Location currentLocation = this.getBukkitEntity().getLocation();
                    Location targetLocation = this.getGoalTarget().getBukkitEntity().getLocation();
                    boolean isSameWorld = currentLocation.getWorld() == targetLocation.getWorld();
                    double distance = isSameWorld ? currentLocation.distance(targetLocation) : 32.0;
                    if (distance <= 16.0) {
                        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(attributable.getSpeed());
                    } else {
                        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.0);
                    }
                }
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    public void die() {
        iMobController.die();
        super.die();
    }
}
