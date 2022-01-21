package org.runaway.entity.mobs;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Slime;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.runaway.entity.Attributable;
import org.runaway.entity.IMobController;
import org.runaway.entity.MobRare;

import java.util.Objects;

public class SlimeEntity extends Slime {

    private IMobController iMobController;
    private Attributable attributable;

    public SlimeEntity(IMobController mobController) {
        super(EntityType.SLIME, ((CraftWorld) mobController.getSpawnLocation().getWorld()).getHandle());
        this.iMobController = mobController;
        this.attributable = iMobController.getAttributable();
        double health = attributable.getHealth();
        double speed = attributable.getSpeed();
        MobRare mobRare = iMobController.getMobRare();
        if (mobRare != MobRare.DEFAULT && mobRare != null) {
            setGlowingTag(true);
            health *= mobRare.getMultiplier();
            speed *= mobRare.getMultiplier();
        }
        setSize(4, true);
        this.getAttributes().getInstance(Attributes.FOLLOW_RANGE).setBaseValue(128.0);
        this.getAttributes().getInstance(Attributes.MAX_HEALTH).setBaseValue(health);
        this.getAttributes().getInstance(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(-1);
        this.getAttributes().getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(speed);
        ((LivingEntity)this.getBukkitEntity()).setRemoveWhenFarAway(false);
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, ServerPlayer.class, 64.0f));
        this.setHealth((float) health);
        this.setCustomNameVisible(true);
        this.setCanPickUpLoot(false);
        this.setSecondsOnFire(0);
        this.setPersistenceRequired();
        mobController.getSpawnLocation().getChunk().load();
    }

    protected boolean damageEntity0(DamageSource damagesource, float f) {
        if (!iMobController.onDamage(damagesource, f)) {
            return false;
        }
        return super.damageEntity0(damagesource, f);
    }

    public void baseTick() {
        try {
            super.baseTick();
            if (this.getTarget() != null) {
                if (this.getTarget() != null && this.getTarget().getBukkitEntity().getLocation().getWorld() != this.getBukkitEntity().getWorld()) {
                    this.setTarget(null);
                } else if (!(this.getTarget() instanceof ServerPlayer)) {
                    this.setTarget(null);
                } else if (this.getTarget() != null && this.passengers.isEmpty()) {
                    Location currentLocation = this.getBukkitEntity().getLocation();
                    Location targetLocation = this.getTarget().getBukkitEntity().getLocation();
                    boolean isSameWorld = currentLocation.getWorld() == targetLocation.getWorld();
                    double distance = isSameWorld ? currentLocation.distance(targetLocation) : 32.0;
                    if (distance <= 16.0) {
                        this.getAttributes().getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(attributable.getSpeed());
                    } else {
                        this.getAttributes().getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
                    }
                }
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    public void die(DamageSource damagesource) {
        iMobController.die();
        super.die(damagesource);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SlimeEntity that = (SlimeEntity) o;
        return Objects.equals(iMobController, that.iMobController) && Objects.equals(attributable, that.attributable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), iMobController, attributable);
    }
}
